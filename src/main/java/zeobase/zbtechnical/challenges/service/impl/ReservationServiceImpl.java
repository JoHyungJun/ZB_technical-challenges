package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.reservation.request.ReservationAcceptRequest;
import zeobase.zbtechnical.challenges.dto.reservation.request.ReservationModifyRequest;
import zeobase.zbtechnical.challenges.dto.reservation.request.ReservationReserveRequest;
import zeobase.zbtechnical.challenges.dto.reservation.response.ReservationAcceptResponse;
import zeobase.zbtechnical.challenges.dto.reservation.response.ReservationAvailableResponse;
import zeobase.zbtechnical.challenges.dto.reservation.response.ReservationCanceledResponse;
import zeobase.zbtechnical.challenges.dto.reservation.response.ReservationInfoResponse;
import zeobase.zbtechnical.challenges.dto.reservation.response.ReservationModifyResponse;
import zeobase.zbtechnical.challenges.dto.reservation.response.ReservationReserveResponse;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.entity.StoreReservationInfo;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.exception.ReservationException;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.repository.ReservationRepository;
import zeobase.zbtechnical.challenges.repository.StoreRepository;
import zeobase.zbtechnical.challenges.service.ReservationService;
import zeobase.zbtechnical.challenges.type.member.MemberRoleType;
import zeobase.zbtechnical.challenges.type.reservation.ReservationAcceptedType;
import zeobase.zbtechnical.challenges.type.reservation.ReservationVisitedType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.ALREADY_FULL_RESERVATION_TIME;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.ALREADY_RESERVATION_CANCELED;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.ALREADY_RESERVATION_CHECKED;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.CANCELED_RESERVATION;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_PERSON_COUNT_REQUEST;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_RESERVATION_CANCELED_TIME;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_RESERVATION_TIME;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_TABLE_COUNT_REQUEST;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.MISMATCH_ROLE;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.MISMATCH_TABLE_COUNT_PER_CAPACITY;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_RESERVATION_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_STORE_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_OWNED_RESERVATION_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_OWNED_STORE_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.REJECTED_RESERVATION;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.WAITING_RESERVATION;


/**
 * 예약 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final MemberServiceImpl memberService;
    private final StoreServiceImpl storeService;

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;


    /**
     * 개별 예약의 정보를 전달하는 api
     * reservationId (Reservation 의 PK) 검증
     * 반환되는 예약 정보와 연계된 엔티티의 검증 (member/store status) 은 별도로 진행하지 않음
     *
     * @param reservationId
     * @return "dto/reservation/response/ReservationInfoResponse"
     * @exception ReservationException
     */
    @Override
    @Transactional(readOnly = true)
    public ReservationInfoResponse getReservationInfoById(Long reservationId) {

        // reservation id 존재 여부 검증
        return ReservationInfoResponse.fromEntity(
                reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION_ID))
        );
    }

    /**
     * 특정 매장의 모든 예약 정보를 전달하는 메서드
     * store 관련 검증 후, 예약 날짜 내림차순 순으로 반환
     * 반환되는 예약 정보와 연계된 엔티티의 검증 (member/store status) 은 별도로 진행하지 않음
     *
     * @param storeId
     * @return List "dto/reservation/response/ReservationInfoResponse"
     * @exception StoreException
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReservationInfoResponse> getReservationsInfoByStore(Long storeId) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        // store signed status 검증
        storeService.validateStoreSignedStatus(store);

        // 예약 날짜 기준 정렬
        List<Reservation> reservations = reservationRepository.findAllByStoreId(storeId, Sort.by(
                    Sort.Order.asc("reservationDateTime")));

        return reservations.stream()
                .map(reservation -> ReservationInfoResponse.fromEntity(reservation))
                .collect(Collectors.toList());
    }

    /**
     * 특정 이용자의 모든 예약 정보를 전달하는 메서드
     * member 관련 검증 후 반환
     * 반환되는 예약 정보와 연계된 엔티티의 검증 (member/store status) 은 별도로 진행하지 않음
     *
     * @param authentication
     * @return List "dto/reservation/response/ReservationInfoResponse"
     * @exception MemberException
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReservationInfoResponse> getReservationsInfoByMember(Authentication authentication) {

        // authentication 으로 member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member 의 status 검증
        memberService.validateMemberSignedStatus(member);

        return member.getReservations()
                .stream()
                .map(reservation -> ReservationInfoResponse.fromEntity(reservation))
                .collect(Collectors.toList());
    }

    /**
     * 특정 매장의 특정 시간에 예약이 가능한지 (이미 등록된 예약이 없는지) 확인하는 메서드
     * store 관련 검증 진행
     * 
     * @param storeId
     * @param reservationTime - 확인할 날짜 정보
     * @return "dto/reservation/response/ReservationAvailableResponse"
     * @exception StoreException
     */
    @Override
    @Transactional(readOnly = true)
    public ReservationAvailableResponse checkAvailableReservationTime(
            Long storeId,
            LocalDateTime reservationTime,
            Integer personCount,
            Integer tableCount
    ) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        // store signed status 검증
        storeService.validateStoreSignedStatus(store);

        // 영업시간에 맞는 예약 시간인지 검증
        validateReservationWithinOpeningHoursAndTerm(store, reservationTime);

        // 예약 인원 수가 0 보다 작은 값인지 검증
        if(personCount <= 0) {
            throw new ReservationException(INVALID_PERSON_COUNT_REQUEST);
        }

        // 요청된 인원 수 및 테이블 수 검증
        // table count 가 요청으로 전달되지 않았을 경우, (예약 인원 수 / 테이블 당 앉을 수 있는 사람 수)의 올림 값을 설정
        StoreReservationInfo storeReservationInfo = store.getStoreReservationInfo();
        Integer defaultTableCount = personCount / storeReservationInfo.getSeatingCapacityPerTable()
                + (personCount % storeReservationInfo.getSeatingCapacityPerTable() != 0 ? 1 : 0);

        // table count 가 요청으로 전달되었을 때 값 검증
        if(tableCount != null) {

            // 예약 테이블 수가 0 보다 큰 값인지 검증
            if(tableCount <= 0) {
                throw new ReservationException(INVALID_TABLE_COUNT_REQUEST);
            }

            // 예약 테이블 수가 요청 인원 대비 최소 값보다 크거나 같은 값인지 검증
            if(tableCount < defaultTableCount) {
                throw new ReservationException(MISMATCH_TABLE_COUNT_PER_CAPACITY);
            }
        }

        Integer finalTableCount = tableCount != null ? tableCount : defaultTableCount;

        return ReservationAvailableResponse.builder()
                .isAvailable(validateAvailableReservationTime(store, reservationTime, personCount, finalTableCount))
                .build();
    }

    /**
     * 실제 예약을 진행하는 메서드
     * store, member, 예약 가능 시간 여부 검증 후 등록
     *
     * @param request - 매장 정보, 예약 희망 날짜
     * @return "dto/reservation/response/ReservationReserveResponse"
     * @exception StoreException
     * @exception MemberException
     */
    @Override
    @Transactional
    public ReservationReserveResponse registerReservation(ReservationReserveRequest request, Authentication authentication) {

        // authentication 으로 member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member 의 status 검증
        memberService.validateMemberSignedStatus(member);

        // store id 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        // store signed status 검증
        storeService.validateStoreSignedStatus(store);

        // 영업시간에 맞는 예약 시간인지 검증
        validateReservationWithinOpeningHoursAndTerm(store, request.getReservationDateTime());

        // 예약 인원 수가 0 보다 작거나 같은 값인지 검증
        if(request.getReservationPersonCount() <= 0) {
            throw new ReservationException(INVALID_PERSON_COUNT_REQUEST);
        }
        
        // 요청된 인원 수 및 테이블 수 검증
        // table count 가 요청으로 전달되지 않았을 경우, (예약 인원 수 / 테이블 당 앉을 수 있는 사람 수)의 올림 값을 설정
        // TODO : modify 부와 중복되는 로직이므로 추후 private 메서드로 extract
        StoreReservationInfo storeReservationInfo = store.getStoreReservationInfo();
        Integer defaultTableCount = request.getReservationPersonCount() / storeReservationInfo.getSeatingCapacityPerTable()
                + (request.getReservationPersonCount() % storeReservationInfo.getSeatingCapacityPerTable() != 0 ? 1 : 0);

        // table count 가 요청으로 전달되었을 때 값 검증
        if(request.getReservationTableCount() != null) {
            
            // 예약 테이블 수가 0 보다 큰 값인지 검증
            if(request.getReservationTableCount() <= 0) {
                throw new ReservationException(INVALID_TABLE_COUNT_REQUEST);
            }

            // 예약 테이블 수가 요청 인원 대비 최소 값보다 크거나 같은 값인지 검증
            if(request.getReservationTableCount() < defaultTableCount) {
                throw new ReservationException(MISMATCH_TABLE_COUNT_PER_CAPACITY);
            }
        }

        // 해당 시간에 예약이 가능한지 검증
        Integer finalTableCount = request.getReservationTableCount() != null ? request.getReservationTableCount() : defaultTableCount;
        if(!validateAvailableReservationTime(store, request.getReservationDateTime(), request.getReservationPersonCount(), finalTableCount)) {
            throw new ReservationException(ALREADY_FULL_RESERVATION_TIME);
        }

        // 검증을 마쳤다면 예약
        Reservation reservation = reservationRepository.save(
                Reservation.builder()
                .reservationDateTime(request.getReservationDateTime())
                .reservationDate(request.getReservationDateTime().toLocalDate())
                .reservationPersonCount(request.getReservationPersonCount())
                .reservationTableCount(finalTableCount)
                .acceptedStatus(ReservationAcceptedType.WAITING)
                .visitedStatus(ReservationVisitedType.UNVISITED)
                .member(member)
                .store(store)
                .build()
        );

        return ReservationReserveResponse.builder()
                .reservationId(reservation.getId())
                .memberId(reservation.getMember().getId())
                .storeId(reservation.getStore().getId())
                .reservedDateTime(reservation.getReservationDateTime())
                .reservationPersonCount(reservation.getReservationPersonCount())
                .reservationTableCount(reservation.getReservationTableCount())
                .build();
    }

    /**
     * 매장 점주가 특정 예약에 대해 승인/거절하는 메서드 (cancel 은 불가능, 예약을 등록한 당사자 회원만 가능)
     * store, reservation, member 에 대한 검증 후,
     * 기존 reservation 의 accepted 정보를 request 로 전달된 정보로 갱신
     * 승인할 예약이라면 해당 시간에 예약이 가능한지 검증 후 진행
     *
     * @param request - 예약 정보, 가게 정보, 승인/거절 정보
     * @param authentication - 토큰을 활용한 이용자(매장 주인) 검증
     * @return "dto/reservation/response/ReservationAcceptResponse"
     * @exception StoreException
     * @exception MemberException
     * @exception ReservationException
     */
    @Override
    @Transactional
    public ReservationAcceptResponse acceptReservationByStoreOwner(ReservationAcceptRequest request, Authentication authentication) {

        // reservation id 검증
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION_ID));

        // store id 검증
        Store store = storeRepository.findById(reservation.getStore().getId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // authentication (토큰) 으로 member 추출
        Member member = memberService.getMemberByAuthentication(authentication);
        
        // member status 검증
        memberService.validateMemberSignedStatus(member);

        // member role (가게 점장이 맞는지 여부) 검증
        if(member.getRole() != MemberRoleType.STORE_OWNER) {
            throw new MemberException(MISMATCH_ROLE);
        }

        // 전달된 store id가 추출한 이용자(점주) 소유 매장인지 여부 검증
        if(!member.getStores()
                .stream()
                .map(ownStore -> ownStore.getId())
                .collect(Collectors.toList())
                .contains(store.getId())) {
            throw new StoreException(NOT_OWNED_STORE_ID);
        }

        // store status 검증
        storeService.validateStoreStatus(store);

        // store signed status 검증
        storeService.validateStoreSignedStatus(store);
        
        // 예약 취소 검증 (점주는 예약 취소 불가능)
        if(request.getAccepted() == ReservationAcceptedType.CANCELED) {
            throw new MemberException(MISMATCH_ROLE);
        }

        // 해당 예약을 승인했을 시, 해당 시간에 자리가 남아 있는지 검증
        if(request.getAccepted() != ReservationAcceptedType.ACCEPTED) {
            validateAvailableReservationTime(
                    store,
                    reservation.getReservationDateTime(),
                    reservation.getReservationPersonCount(),
                    reservation.getReservationTableCount()
            );
        }

        Reservation updateReservation = reservationRepository.save(
                reservation.modifyAccepted(request.getAccepted()));

        return ReservationAcceptResponse.builder()
                .reservationId(updateReservation.getId())
                .accepted(updateReservation.getAcceptedStatus())
                .build();
    }

    /**
     * 이용자가 본인이 등록한 예약에 대해 수정하는 메서드
     * request 중 수정을 원하지 않는 정보(필드)는 null 로 전달
     * (프론트 쪽에서 변하지 않은 정보를 null 이 아닌 원래 값으로 넣어줄 수도 있기에, 해당 검증 추가)
     * 
     * @param reservationId
     * @param request - 수정할 예약 날짜, 사람 수, 테이블 수
     * @param authentication
     * @return "dto/reservation/response/ReservationAcceptResponse"
     */
    // TODO : reserve 와 modify 겹치는 로직 및 메서드 리팩토링
    @Override
    @Transactional
    public ReservationModifyResponse modifyReservation(Long reservationId, ReservationModifyRequest request, Authentication authentication) {

        // reservation id 검증
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION_ID));

        // authentication (토큰) 으로 member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // 예약을 등록한 당사자가 맞는지 검증
        if(!member.getReservations()
                .stream()
                .map(reservationByMember -> reservationByMember.getId())
                .collect(Collectors.toList())
                .contains(reservationId)) {
            throw new ReservationException(NOT_OWNED_RESERVATION_ID);
        }

        // 수정할 필드들을 기존의 필드로 먼저 초기화하고,
        // request 의 값이 null 이 아니라면 (수정 요청이 왔다면) 로직 내에서 검증 후 해당 요청 값으로 수정 필드들을 변경
        LocalDateTime targetReservationDateTime = reservation.getReservationDateTime();
        Integer targetReservationPersonCount = reservation.getReservationPersonCount();
        Integer targetReservationTableCount = reservation.getReservationTableCount();
        
        // reservation date time 수정 요청 시 검증
        if(request.getReservationDateTime() != null) {

            targetReservationDateTime = request.getReservationDateTime();

            // 운영 시간에 맞는 시간인지 검증
            validateReservationWithinOpeningHoursAndTerm(reservation.getStore(), targetReservationDateTime);
        }

        // reservation person count 수정 요청 시 검증
        if(request.getReservationPersonCount() != null) {

            targetReservationPersonCount = request.getReservationPersonCount();

            // 예약 인원 수가 0 보다 작거나 같은 값인지 검증
            if(targetReservationPersonCount <= 0) {
                throw new ReservationException(INVALID_PERSON_COUNT_REQUEST);
            }
        }

        // reservation table count 수정 요청 시 검증
        if(request.getReservationTableCount() != null) {

            targetReservationTableCount = reservation.getReservationTableCount();

            // 예약 테이블 수가 0 보다 큰 값인지 검증
            if(targetReservationTableCount <= 0) {
                throw new ReservationException(INVALID_TABLE_COUNT_REQUEST);
            }

            Integer seatingCapacityPerTable = reservation.getStore().getStoreReservationInfo().getSeatingCapacityPerTable();
            Integer minimumTableCount = targetReservationPersonCount / seatingCapacityPerTable
                    + (targetReservationPersonCount % seatingCapacityPerTable != 0 ? 1 : 0);

            // 예약 테이블 수가 요청 인원 대비 최소 값보다 크거나 같은 값인지 검증
            if(request.getReservationTableCount() < minimumTableCount) {
                throw new ReservationException(MISMATCH_TABLE_COUNT_PER_CAPACITY);
            }
        }

        // 해당 날짜, 인원 수, 테이블 수가 예약 가능한지 검증
        if(!validateAvailableReservationTime(reservation.getStore(), targetReservationDateTime, targetReservationPersonCount, targetReservationTableCount)) {
            throw new ReservationException(ALREADY_FULL_RESERVATION_TIME);
        }

        // 검증 후 최종 값으로 수정
        reservation.modifyReservationDateTime(targetReservationDateTime);
        reservation.modifyReservationPersonCount(targetReservationPersonCount);
        reservation.modifyReservationTableCount(targetReservationTableCount);

        reservation = reservationRepository.save(reservation);

        return ReservationModifyResponse.builder()
                .reservationId(reservation.getId())
                .build();
    }

    /**
     * 이용자가 등록했던 예약을 취소하는 메서드
     *
     * @param reservationId
     * @param authentication - 예약을 진행한 이용자
     * @return "dto/reservation/response/ReservationCanceledResponse"
     * @exception ReservationException
     */
    @Override
    @Transactional
    public ReservationCanceledResponse cancelReservationByMember(Long reservationId, Authentication authentication) {

        // reservation id 검증
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION_ID));

        // authentication (토큰) 으로 member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // 예약을 등록한 당사자가 맞는지 검증
        if(!member.getReservations()
                .stream()
                .map(reservationByMember -> reservationByMember.getId())
                .collect(Collectors.toList())
                .contains(reservationId)) {
            throw new ReservationException(NOT_OWNED_RESERVATION_ID);
        }

        // 지난 예약은 취소 불가
        if(reservation.getReservationDateTime().isBefore(LocalDateTime.now())) {
            throw new ReservationException(INVALID_RESERVATION_CANCELED_TIME);
        }

        // 이미 취소한 예약은 취소 불가
        if(reservation.getAcceptedStatus() == ReservationAcceptedType.CANCELED) {
            throw new ReservationException(ALREADY_RESERVATION_CANCELED);
        }

        // 방문한 예약은 취소 불가
        if(reservation.getVisitedStatus() == ReservationVisitedType.VISITED) {
            throw new ReservationException(ALREADY_RESERVATION_CHECKED);
        }

        reservationRepository.save(
                reservation.modifyAccepted(ReservationAcceptedType.CANCELED)
        );

        return ReservationCanceledResponse.builder()
                .reservationId(reservation.getId())
                .build();
    }

    /**
     * store 엔티티와 target time 으로 매장 운영시간 및 텀에 알맞은 예약인지 검증하는 메서드
     *
     * @param store
     * @param targetDateTime - LocalDateTime
     * @return
     * @exception ReservationException
     */
    private void validateReservationWithinOpeningHoursAndTerm(Store store, LocalDateTime targetDateTime) {

        // 현재 시점보다 과거 날짜 및 시간으로의 요청인지 검증
        LocalDateTime now = LocalDateTime.now();
        if(targetDateTime.isBefore(now)) {
            throw new ReservationException(INVALID_RESERVATION_TIME);
        }
        
        LocalTime openHours = store.getOpenHours();
        LocalTime closedHours = store.getClosedHours();

        StoreReservationInfo storeReservationInfo = store.getStoreReservationInfo();
        LocalTime reservationTerm = storeReservationInfo.getReservationTerm();

        LocalTime targetTime = targetDateTime.toLocalTime();

        // 매장 운영 시간에 맞는 시간인지 검증
        if(!validateReservationTime(openHours, closedHours, targetTime)
            || !validateReservationTimeMatchesTerm(openHours, closedHours, reservationTerm, targetTime)) {
            throw new ReservationException(INVALID_RESERVATION_TIME);
        }
    }

    /**
     * 특정 매장의 특정 시간에 이미 예약이 차 있는지 검증하는 메서드
     *
     * @param store - 예약 확인하려는 매장
     * @param targetDateTime - 예약 확인/등록하려는 날짜, 시간
     * @param personCount - 예약하려는 사람 수
     * @param tableCount - 예약하려는 테이블 수
     * @return
     */
    @Transactional(readOnly = true)
    private Boolean validateAvailableReservationTime(Store store, LocalDateTime targetDateTime, Integer personCount, Integer tableCount) {

        // 특정 store id의 특정 날짜의 모든 예약을 가져오고 각 시간을 추출
        // 단, 점주가 거절한 예약의 경우 필터링
        LocalTime targetTime = targetDateTime.toLocalTime();

        List<Reservation> reservations = reservationRepository.findAllReservationByStoreIdAndReservationDate(store.getId(), targetDateTime.toLocalDate())
                .stream()
                .filter(reservation -> reservation.getAcceptedStatus() == ReservationAcceptedType.ACCEPTED
                                        && reservation.getReservationDateTime().toLocalTime().equals(targetTime))
                .collect(Collectors.toList());
        
        // 원하는 날짜와 시간에 점장이 허용한 예약으로 가득 찼다면 false 반환
        // max 로 가능한 테이블 수 및 예약 사람 수 계산 (테이블 수 * 테이블 당 앉을 수 있는 사람 수)
        StoreReservationInfo reservationInfo = store.getStoreReservationInfo();
        Integer maxAvailableTableCount = reservationInfo.getTableCount();
        Integer maxAvailablePersonCount = maxAvailableTableCount * reservationInfo.getSeatingCapacityPerTable();

        // 예약 완료된 사람 수 계산
        Integer alreadyReservedPersonCount = 0;
        Integer alreadyReservedTableCount = 0;
        for(Reservation reservation : reservations) {

            alreadyReservedPersonCount += reservation.getReservationPersonCount();
            alreadyReservedTableCount += reservation.getReservationTableCount();
        }

        // 요청한 값이 매장에서 설정한 최대 테이블 수 및 테이블 수에 맞는 최대 인원 수보다 작거나 같은지 검증
        return alreadyReservedPersonCount + personCount <= maxAvailablePersonCount
                && alreadyReservedTableCount + tableCount <= maxAvailableTableCount;
    }

    /**
     * 특정 매장의 운영 시간에 맞는 시간으로 예약 요청을 한 것인지 검증하는 메서드
     * open 전 (open 시간에는 예약 가능) 혹은 closed 후의 예약인지 검증
     *
     * @param open - 매장 오픈 시간
     * @param closed - 매장 종료 시간
     * @param target - 검증하고자 하는 시간
     * @return
     */
    public static Boolean validateReservationTime(LocalTime open, LocalTime closed, LocalTime target) {

        return target.isBefore(closed) && !target.isBefore(open);
    }

    /**
     * 예약하려는 시간이 해당 매장의 예약 텀에 맞는 시간인지 검증하는 메서드
     *
     * @param open - 영업 시작 시간
     * @param closed - 영업 종료 시간
     * @param term - 해당 매장의 점주가 설정한 예약 텀
     * @param target - 예약하려는 시간
     * @return 해당 target 시간이 알맞은 시간인지 여부
     */
    public static Boolean validateReservationTimeMatchesTerm(LocalTime open, LocalTime closed, LocalTime term, LocalTime target) {

        LocalTime cur = open;
        while(cur.isBefore(closed)) {

            if(target.isAfter(cur) &&
                    target.isBefore(cur.plusHours(term.getHour()).plusMinutes(term.getMinute()))) {

                return false;
            }

            cur = cur.plusHours(term.getHour()).plusMinutes(term.getMinute());
        }

        return true;
    }

    /**
     * 예약의 승인/거절 상태를 검증하는 메서드
     *
     * @param reservation - reservation 의 ReservationAcceptedType 검증
     * @return
     * @exception ReservationException
     */
    public void validateReservationAccepted(Reservation reservation) {

        switch(reservation.getAcceptedStatus()) {
            case REJECTED:
                throw new ReservationException(REJECTED_RESERVATION);
            case WAITING:
                throw new ReservationException(WAITING_RESERVATION);
            case CANCELED:
                throw new ReservationException(CANCELED_RESERVATION);
            default:
                break;
        }
    }
}
