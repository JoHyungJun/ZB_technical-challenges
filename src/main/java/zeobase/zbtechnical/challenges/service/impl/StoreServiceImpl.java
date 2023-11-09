package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.store.StoreInfoDto;
import zeobase.zbtechnical.challenges.dto.store.StoreRegistrationDto;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.repository.StoreRepository;
import zeobase.zbtechnical.challenges.service.StoreService;
import zeobase.zbtechnical.challenges.type.MemberRoleType;
import zeobase.zbtechnical.challenges.type.StoreSortedType;
import zeobase.zbtechnical.challenges.type.StoreStatusType;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static zeobase.zbtechnical.challenges.type.ErrorCode.*;

/**
 * 매장 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    // 최소 예약 텀은 30분으로 설정 -> 추가 요구사항 때 바뀔 수 있음
    private static final LocalTime MINIMUM_RESERVATION_TERM = LocalTime.of(0, 29);

    private final MemberServiceImpl memberService;

    private final StoreRepository storeRepository;


    /**
     * 개별 매장의 정보를 전달하는 메서드
     * storeId (Store 의 PK) 검증
     *
     * @param storeId
     * @return "dto/store/StoreInfoDto.Response"
     * @exception StoreException
     */
    @Override
    @Transactional(readOnly = true)
    public StoreInfoDto.Response getStoreInfo(Long storeId) {

        // 유효한 storeId인지 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        return StoreInfoDto.Response.fromEntity(store);
    }

    /**
     * 이용자(점주)가 자신의 매장을 등록하는 메서드
     * 이용자에 관한 검증 후
     * 이용자 권한(일반 회원인지 점주 회원인지), 설정한 운영 시간, 예약 텀을 검증
     *
     * @param request - 매장 정보, 매장 위치, 매장 운영 시간, 매장 영업 상태
     * @param authentication - 토큰을 활용한 이용자(점주) 검증
     * @return "dto/store/StoreRegistrationDto.Response"
     * @exception MemberException
     * @exception StoreException
     */
    @Override
    @Transactional
    public StoreRegistrationDto.Response registerStore(
            StoreRegistrationDto.Request request,
            Authentication authentication) {
        
        // 토큰(authentication)을 통해 이용자 추출
        Member member = memberService.getMemberByAuthentication(authentication);
        
        // 회원 status 검증
        memberService.validateMemberStatus(member);

        // 회원이 점주가 맞는지 검증
        if(MemberRoleType.STORE_OWNER != member.getRole()) {
            throw new MemberException(MISMATCH_ROLE);
        }

        // 영업 종료 시간이 영업 시작 시간보다 빠른지 검증
        if(!request.getClosedHours().isAfter(request.getOpenHours())) {
            throw new StoreException(INVALID_OPENING_HOURS);
        }

        // 예약 텀 시간을 너무 빠르게 설정했는지 검증
        if(!request.getReservationTerm().isAfter(MINIMUM_RESERVATION_TERM)) {
            throw new StoreException(INVALID_RESERVATION_TERM);
        }

        Store savedStore = storeRepository.save(
                Store.builder()
                    .name(request.getName())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .explanation(request.getExplanation())
                    .status(request.getStatus())
                    .openHours(request.getOpenHours())
                    .closedHours(request.getClosedHours())
                    .reservationTerm(request.getReservationTerm())
                    .member(member)
                    .build()
        );

        return StoreRegistrationDto.Response.builder()
                .storeId(savedStore.getId())
                .build();
    }

    /**
     * 모든 매장 목록을 정렬에 따라 정보를 전달하는 메서드
     * 전달된 인자에 대한 검증 후 요청값에 따라 정렬을 다르게 하여 반환
     *
     * @param sortBy - "type/StoreSortedType" 거리(DISTANCE), 이름(ALPHABET), 별점(STAR_RATING)
     * @param latitude - 위도
     * @param longitude - 경도
     * @return List "dto/store/StoreInfoDto.Response"
     * @exception StoreException
     */
    // TODO : 리팩토링 필요. JPA 로 정렬 방법 적용 + 지저분한 코드 정리
    // TODO : 페이징
    @Override
    @Transactional(readOnly = true)
    public List<StoreInfoDto.Response> getAllSortedStoresInfo(String sortBy, Double latitude, Double longitude) {

        // 전달 받은 sortBy 인자 검증
        StoreSortedType sortedType = null;
        try {
            sortedType = Enum.valueOf(StoreSortedType.class, sortBy);
        }catch (IllegalArgumentException e) {
            throw new StoreException(INVALID_SORTED_TYPE);
        }catch (NullPointerException e) {
            throw new StoreException(INVALID_SORTED_TYPE);
        }

        // 전달 받은 위도, 경도 인자 검증 (둘 중 하나만 왔을 때 에러 발생)
        if(latitude == null ^ longitude == null) {
            throw new StoreException(INVALID_LOCATION_TYPE);
        }

        // 거리순 정렬일 때 위도, 경도 둘 다 전달 되었는지 검증 (default == 거리순)
        if((sortedType == StoreSortedType.DISTANCE)
            && (latitude == null || longitude == null)) {

            throw new StoreException(INVALID_LOCATION_TYPE);
        }

        // 매장 정보 전부 받기
        List<StoreInfoDto.Response> stores = storeRepository.findAll()
                .stream()
                .map(store -> (latitude!=null && longitude!=null) ?
                        StoreInfoDto.Response.fromEntity(store, latitude, longitude) :
                        StoreInfoDto.Response.fromEntity(store))
                .collect(Collectors.toList());

        // 정렬 값에 따라 다르게 sort
        StoreSortedType finalSortedType = sortedType;
        Collections.sort(stores, new Comparator<StoreInfoDto.Response>() {
            @Override
            public int compare(StoreInfoDto.Response o1, StoreInfoDto.Response o2) {
                switch (finalSortedType) {
                    case DISTANCE:
                        return o1.getDistanceDiff().compareTo(o2.getDistanceDiff());
                    case ALPHABET:
                        return o1.getName().compareTo(o2.getName());
                    case STAR_RATING:
                        return o2.getAverageStarRating().compareTo(o1.getAverageStarRating());
                    default:
                        return o1.getName().compareTo(o2.getName());
                }
            }
        });

        return stores;
    }

    /**
     * 가게의 운영 상태를 검증하는 메서드
     *
     * @param store - 이용자의 Entity 객체
     * @return
     * @exception StoreException
     */
    public void validateStoreStatus(Store store) {

        StoreStatusType status = store.getStatus();

        if(StoreStatusType.SHUT_DOWN == status) {
            throw new StoreException(STORE_SHUT_DOWN);
        }else if(StoreStatusType.OPEN_PREPARING == status) {
            throw new StoreException(STORE_OPEN_PREPARING);
        }
    }
}
