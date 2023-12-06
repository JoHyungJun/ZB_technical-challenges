package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.store.request.StoreModifyRequest;
import zeobase.zbtechnical.challenges.dto.store.request.StoreRegistrationRequest;
import zeobase.zbtechnical.challenges.dto.store.request.StoreWithdrawRequest;
import zeobase.zbtechnical.challenges.dto.store.response.*;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.entity.StoreReservationInfo;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.repository.StoreRepository;
import zeobase.zbtechnical.challenges.repository.StoreReservationInfoRepository;
import zeobase.zbtechnical.challenges.service.StoreService;
import zeobase.zbtechnical.challenges.type.member.MemberRoleType;
import zeobase.zbtechnical.challenges.type.reservation.ReservationAcceptedType;
import zeobase.zbtechnical.challenges.type.review.ReviewStatusType;
import zeobase.zbtechnical.challenges.type.store.StoreSignedStatusType;
import zeobase.zbtechnical.challenges.type.store.StoreSortedType;
import zeobase.zbtechnical.challenges.type.store.StoreStatusType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;

import static zeobase.zbtechnical.challenges.service.impl.ReservationServiceImpl.validateReservationTime;
import static zeobase.zbtechnical.challenges.service.impl.ReservationServiceImpl.validateReservationTimeMatchesTerm;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.*;
import static zeobase.zbtechnical.challenges.utils.CustomStringUtils.getDecodingUrl;

/**
 * 매장 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    // 최소 예약 텀은 30분으로 설정 -> 추가 요구사항 때 바뀔 수 있음
    private static final LocalTime MINIMUM_RESERVATION_TERM = LocalTime.of(0, 30);
    private static final Double DEFAULT_STAR_RATING = 0.0;
    private static final StoreSignedStatusType DEFAULT_SIGNED_STATUS = StoreSignedStatusType.ACTIVE;
    private static final Integer DEFAULT_TABLE_COUNT = 1;
    private static final Integer DEFAULT_SEATING_CAPACITY_PER_TABLE = 4;

    private final MemberServiceImpl memberService;

    private final StoreRepository storeRepository;
    private final StoreReservationInfoRepository storeReservationInfoRepository;


    /**
     * 개별 매장의 정보를 전달하는 메서드
     * storeId (Store 의 PK) 검증
     *
     * @param storeId
     * @return "dto/store/response/StoreInfoResponse"
     * @exception StoreException
     */
    @Override
    @Transactional(readOnly = true)
    public StoreInfoResponse getStoreInfo(Long storeId) {

        // store id 존재 여부 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        validateStoreStatus(store);

        // store signed status 검증
        validateStoreSignedStatus(store);

        return StoreInfoResponse.fromEntity(store);
    }

    /**
     * 검색어가 포함된 상호명의 모든 매장 정보를 전달하는 메서드
     * 검색어가 없다면 전체 목록을 반환
     * 현재 운영 중인 매장을 반환
     *
     * @param name - 검색어
     * @param pageable - 페이징
     * @return List "dto/store/response/StoreInfoResponse"
     */
    @Override
    @Transactional(readOnly = true)
    public Page<StoreInfoResponse> getAllStoresInfoByName(String name, Pageable pageable) {

        Page<Store> stores = null;

        // 검색어가 null 이라면 전체 매장 목록을, 아니라면 검색어가 포함된 상호명의 매장 목록을 반환
        if(name == null) {

            stores = storeRepository.findAllByStatusAndSignedStatus(StoreStatusType.OPEN, StoreSignedStatusType.ACTIVE, pageable);
        }else {
            
            stores = storeRepository.findAllByNameContainingAndStatusAndSignedStatus(getDecodingUrl(name), StoreStatusType.OPEN, StoreSignedStatusType.ACTIVE, pageable);
        }

        return stores.map(store -> StoreInfoResponse.fromEntity(store));
    }

    /**
     * 모든 매장 목록을 정렬에 따라 정보를 전달하는 메서드
     * 전달된 인자에 대한 검증 후 요청값에 따라 정렬을 다르게 하여 반환
     * 현재 운영 중인 매장을 반환
     *
     * @param sortBy - "type/StoreSortedType" 거리(DISTANCE), 이름(ALPHABET), 별점(STAR_RATING)
     * @param latitude - 위도
     * @param longitude - 경도
     * @param pageable - 페이징
     * @return List "dto/store/response/StoreInfoResponse" - 전달된 정렬 방식에 따라 상속 관계의 다른 객체 전달
     * @exception StoreException
     */
    // TODO : 현재는 star rating 이 store 엔티티 내부 로직으로 계산되어 response 불가능. 캐싱 후 리팩토링
    @Override
    @Transactional(readOnly = true)
    public Page<StoreInfoResponse> getAllSortedStoresInfo(StoreSortedType sortBy, Double latitude, Double longitude, Pageable pageable) {

        // 거리순 정렬일 경우, 위도, 경도 둘 다 전달 되었는지 검증
        if((sortBy == StoreSortedType.DISTANCE)
                && (latitude == null || longitude == null)) {

            throw new StoreException(INVALID_LOCATION_TYPE);
        }

        // 매장 정보 전부 받기
        Page<StoreInfoResponse> stores = null;

        // 정렬 값에 따라 다르게 받기
        if(sortBy == StoreSortedType.DISTANCE) {

            stores = storeRepository.findAllByActiveStoresAndDistanceDiffOrderByDistanceDiff(latitude, longitude, pageable)
                    .map(dto -> StoreInfoWithDistanceDiffResponse.fromDto(dto));

        }else if(sortBy == StoreSortedType.ALPHABET) {

            stores = storeRepository.findAllByStatusAndSignedStatusOrderByNameAsc(StoreStatusType.OPEN, StoreSignedStatusType.ACTIVE, pageable)
                    .map(store -> StoreInfoResponse.fromEntity(store));

        }else if(sortBy == StoreSortedType.STAR_RATING) {

            stores = storeRepository.findAllByStatusAndSignedStatusOrderByStarRatingDesc(StoreStatusType.OPEN, StoreSignedStatusType.ACTIVE, pageable)
                    .map(store -> StoreInfoResponse.fromEntity(store));
        }

        return stores;
    }

    /**
     * 이용자(점주)가 자신의 매장을 등록하는 메서드
     * 이용자에 관한 검증 후
     * 이용자 권한(일반 회원인지 점주 회원인지), 설정한 운영 시간, 예약 텀을 검증
     *
     * @param request - 매장 정보, 매장 위치, 매장 운영 시간, 매장 영업 상태
     * @param authentication - 토큰을 활용한 이용자(점주) 검증
     * @return "dto/store/response/StoreRegistrationResponse"
     * @exception MemberException
     * @exception StoreException
     */
    @Override
    @Transactional
    public StoreRegistrationResponse registerStore(
            StoreRegistrationRequest request,
            Authentication authentication) {
        
        // 토큰(authentication)을 통해 이용자 추출
        Member member = memberService.getMemberByAuthentication(authentication);
        
        // 이용자 status 검증
        memberService.validateMemberSignedStatus(member);

        // 이용자가 점주가 맞는지 검증
        if(MemberRoleType.STORE_OWNER != member.getRole()) {
            throw new MemberException(MISMATCH_ROLE);
        }

        // 영업 종료 시간이 영업 시작 시간보다 빠른지 검증
        if(!request.getClosedHours().isAfter(request.getOpenHours())) {
            throw new StoreException(INVALID_OPENING_HOURS);
        }

        // 예약 텀 시간을 너무 빠르게 설정했는지 검증
        if(request.getReservationTerm().isBefore(MINIMUM_RESERVATION_TERM)) {
            throw new StoreException(INVALID_RESERVATION_TERM);
        }

        // 영업 시간보다 예약 텀 시간이 더 긴지 검증
        Duration duration = Duration.between(request.getOpenHours(), request.getClosedHours());
        if(request.getReservationTerm().toSecondOfDay() > duration.getSeconds()) {
            throw new StoreException(INVALID_RESERVATION_TERM);
        }

        StoreReservationInfo storeReservationInfo = storeReservationInfoRepository.save(
                StoreReservationInfo.builder()
                .reservationTerm(request.getReservationTerm())
                .tableCount(DEFAULT_TABLE_COUNT)
                .seatingCapacityPerTable(DEFAULT_SEATING_CAPACITY_PER_TABLE)
                .build()
        );

        Store store = storeRepository.save(
                Store.builder()
                    .name(request.getName())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .explanation(request.getExplanation())
                    .status(request.getStatus())
                    .signedStatus(DEFAULT_SIGNED_STATUS)
                    .openHours(request.getOpenHours())
                    .closedHours(request.getClosedHours())
                    .starRating(DEFAULT_STAR_RATING)
                    .member(member)
                    .storeReservationInfo(storeReservationInfo)
                    .build()
        );

        return StoreRegistrationResponse.builder()
                .storeId(store.getId())
                .build();
    }

    /**
     * 이용자(점주)가 등록한 매장의 정보를 수정하는 메서드
     * tableCount 혹은 seatingCapacityPerTable 수정 요청의 경우, 값이 이전보다 줄었다면 수정 일자 이후의 모든 예약을 취소시킴
     * (비즈니스 논리 상 해당 정책 선택)
     *
     * @param request - store id, store 정보, store reservation info 정보
     * @return "dto/store/response/StoreModifyResponse" - store id
     */
    @Override
    @Transactional
    public StoreModifyResponse modify(StoreModifyRequest request, Authentication authentication) {

        // 토큰(authentication)을 통해 이용자 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // 이용자 status 검증
        memberService.validateMemberSignedStatus(member);

        // 이용자가 점주가 맞는지 검증
        if(MemberRoleType.STORE_OWNER != member.getRole()) {
            throw new MemberException(MISMATCH_ROLE);
        }

        // 전달된 store id 존재 여부 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        StoreReservationInfo storeReservationInfo = store.getStoreReservationInfo();

        // 전달된 store id가 추출한 이용자(점주) 소유 매장인지 여부 검증
        if(!member.getStores()
                .stream()
                .map(ownStore -> ownStore.getId())
                .collect(Collectors.toList())
                .contains(store.getId())) {
            throw new StoreException(NOT_OWNED_STORE_ID);
        }

        // store status 검증
        validateStoreStatus(store);

        // store signed status 검증
        validateStoreSignedStatus(store);

        // name 수정 요청 시 검증 및 수정
        if(request.getName() != null) {

            store.modifyName(request.getName());
        }

        // 위치 정보(latitude, longitude) 수정 요청 시 검증 및 수정
        if(request.getLatitude() != null || request.getLongitude() != null) {

            // 두 위치 정보 중 하나만 입력 되었는지 검증
            if(request.getLatitude() == null || request.getLongitude() == null) {
                throw new StoreException(INVALID_LOCATION_TYPE);
            }

            store.modifyPosition(request.getLatitude(), request.getLongitude());
        }

        // explanation 수정 요청 시 검증 및 수정
        if(request.getExplanation() != null) {

            store.modifyExplanation(request.getExplanation());
        }

        // StoreStatusType 수정 요청 시 검증 및 수정
        if(request.getStatus() != null && request.getStatus() != store.getStatus()) {

            LocalDateTime now = LocalDateTime.now();

            if(request.getStatus() != StoreStatusType.OPEN) {

                // OPEN 상태에서 OPEN 이 아닌 상태로 바꾸면, 연관된 예약 및 리뷰 삭제(soft delete)
                if(store.getStatus() == StoreStatusType.OPEN) {
                    store.getReservations()
                            .stream()
                            .filter(reservation -> reservation.getReservationDateTime().isAfter(now))
                            .forEach(reservation -> reservation.modifyAccepted(ReservationAcceptedType.REJECTED));

                    store.getReviews()
                            .stream()
                            .forEach(review -> review.modifyStatus(ReviewStatusType.HIDE));
                }
            }

            // OPEN 이 아닌 상태에서 OPEN 상태로 바꾸면, 연관된 리뷰 복구
            if(request.getStatus() == StoreStatusType.OPEN) {

                store.getReviews()
                        .stream()
                        .forEach(review -> review.modifyStatus(ReviewStatusType.SHOW));
            }

            store.modifyStatus(request.getStatus());
        }

        // openHours 혹은 closedHours, reservationTerm 수정 요청 시 검증 및 수정
        if(request.getOpenHours() != null || request.getClosedHours() != null) {

            LocalTime openHours =
                    request.getOpenHours() == null ? store.getOpenHours() : request.getOpenHours();
            LocalTime closedHours =
                    request.getClosedHours() == null ? store.getClosedHours() : request.getClosedHours();

            // 영업 종료 시간이 영업 시작 시간보다 빠른지 검증
            if(!closedHours.isAfter(openHours)) {
                throw new StoreException(INVALID_OPENING_HOURS);
            }

            LocalTime reservationTerm =
                    request.getReservationTerm() == null ? store.getStoreReservationInfo().getReservationTerm() : request.getReservationTerm();

            // 영업 시간보다 예약 텀 시간이 더 긴지 검증
            if(request.getReservationTerm().toSecondOfDay() > Duration.between(openHours, closedHours).getSeconds()) {

                // 에러 발생이 예약 시간 설정에서인지, reservationTerm 설정에서인지에 따라 다른 에러 발생
                if(request.getReservationTerm() == null) {
                    throw new StoreException(INVALID_OPENING_HOURS);
                }else {
                    throw new StoreException(INVALID_RESERVATION_TERM);
                }
            }

            LocalDateTime now = LocalDateTime.now();

            // 현재 시각 이후로 등록된 예약들 중, 가게 영업 시간에 맞지 않는 예약은 거절 처리 
            store.getReservations()
                    .stream()
                    .filter(reservation -> reservation.getReservationDateTime().isAfter(now))
                    .filter(reservation -> !validateReservationTime(openHours, closedHours, reservation.getReservationDateTime().toLocalTime()))
                    .forEach(reservation -> reservation.modifyAccepted(ReservationAcceptedType.REJECTED));

            store.modifyOpenHours(openHours);
            store.modifyClosedHours(closedHours);

            // reservationTerm 수정 요청 시, 수정 이후 시간으로 등록된 예약 중 맞지 않는 예약은 거절 처리
            if(request.getReservationTerm() != null) {

                store.getReservations()
                        .stream()
                        .filter(reservation -> reservation.getReservationDateTime().isAfter(now))
                        .filter(reservation -> !validateReservationTimeMatchesTerm(openHours, closedHours, reservationTerm, reservation.getReservationDateTime().toLocalTime()))
                        .forEach(reservation -> reservation.modifyAccepted(ReservationAcceptedType.REJECTED));

                storeReservationInfo.modifyReservationTerm(reservationTerm);
            }
        }

        // tableCount 혹은 seatingCapacityPerTable 수정 요청 시 검증 및 수정
        if(request.getTableCount() != null || request.getSeatingCapacityPerTable() != null) {

            Integer finalTableCount =
                    request.getTableCount() != null ? request.getTableCount() : storeReservationInfo.getTableCount();
            Integer finalSeatingCapacityPerTable =
                    request.getSeatingCapacityPerTable() != null ? request.getSeatingCapacityPerTable() : storeReservationInfo.getSeatingCapacityPerTable();

            // 두 필드 중 하나라도 이전 설정한 값보다 작아졌다면, 수정 일자 이후의 모든 예약을 거절시킴
            if(finalTableCount < storeReservationInfo.getTableCount()
                || finalSeatingCapacityPerTable < storeReservationInfo.getSeatingCapacityPerTable()) {

                LocalDateTime now = LocalDateTime.now();

                store.getReservations()
                        .stream()
                        .filter(reservation -> reservation.getReservationDateTime().isAfter(now))
                        .forEach(reservation -> reservation.modifyAccepted(ReservationAcceptedType.REJECTED));
            }

            storeReservationInfo.modifyTableCount(finalTableCount);
            storeReservationInfo.modifySeatingCapacityPerTable(finalSeatingCapacityPerTable);
        }

        storeRepository.save(store);
        storeReservationInfoRepository.save(storeReservationInfo);

        return StoreModifyResponse.builder()
                .storeId(store.getId())
                .build();
    }

    /**
     * 이용자(점주)가 등록했던 매장을 해제하는 메서드
     * store signed status 를 WITHDRAW 로 수정 (soft delete)
     *
     * @param request - store id
     * @param authentication
     * @return "dto/store/response/StoreWithdrawResponse" - store id
     * @exception MemberException
     */
    @Override
    @Transactional
    public StoreWithdrawResponse withdraw(StoreWithdrawRequest request, Authentication authentication) {

        // 토큰(authentication)을 통해 이용자 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // 이용자 status 검증
        memberService.validateMemberSignedStatus(member);

        // 이용자가 점주가 맞는지 검증
        if(MemberRoleType.STORE_OWNER != member.getRole()) {
            throw new MemberException(MISMATCH_ROLE);
        }

        // 전달된 store id 존재 여부 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // 전달된 store id가 추출한 이용자(점주) 소유 매장인지 여부 검증
        if(!member.getStores()
                .stream()
                .map(ownStore -> ownStore.getId())
                .collect(Collectors.toList())
                .contains(store.getId())) {
            throw new StoreException(NOT_OWNED_STORE_ID);
        }

        // store status 검증
        validateStoreStatus(store);

        // store signed status 검증
        validateStoreSignedStatus(store);

        store = storeRepository.save(
                store.modifySignedStatus(StoreSignedStatusType.WITHDRAW)
        );

        return StoreWithdrawResponse.builder()
                .storeId(store.getId())
                .build();
    }

    /**
     * 가게의 운영 상태를 검증하는 메서드
     *
     * @param store
     * @return
     * @exception StoreException
     */
    public void validateStoreStatus(Store store) {

        StoreStatusType status = store.getStatus();

        if(StoreStatusType.SHUT_DOWN == status) {
            throw new StoreException(SHUT_DOWN_STORE);
        }else if(StoreStatusType.OPEN_PREPARING == status) {
            throw new StoreException(OPEN_PREPARING_STORE);
        }
    }

    /**
     * 가게의 등록 상태를 검증하는 메서드
     *
     * @param store
     * @return
     * @exception StoreException
     */
    public void validateStoreSignedStatus(Store store) {

        StoreSignedStatusType signedStatus = store.getSignedStatus();

        if(StoreSignedStatusType.WITHDRAW == signedStatus) {
            throw new StoreException(WITHDRAW_STORE);
        }else if(StoreSignedStatusType.BLOCKED == signedStatus) {
            throw new StoreException(BLOCKED_STORE);
        }
    }
}
