package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.reservation.response.*;
import zeobase.zbtechnical.challenges.dto.reservation.request.*;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.entity.Store;
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

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.*;

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
     *
     * @param storeId
     * @return List "dto/reservation/response/ReservationInfoResponse"
     * @exception StoreException
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReservationInfoResponse> getReservationsInfoByStoreId(Long storeId) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        // 예약 날짜 기준 정렬
        List<Reservation> reservations = reservationRepository.findAllByStoreId(storeId, Sort.by(
                    Sort.Order.asc("reservationDateTime")));

        return reservations.stream()
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
    public ReservationAvailableResponse existsAvailableReservationTime(Long storeId, LocalDateTime reservationTime) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        return ReservationAvailableResponse.builder()
                .isAvailable(validateAvailableReservationTime(store, reservationTime))
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
    public ReservationReserveResponse reserve(ReservationReserveRequest request, Authentication authentication) {

        // authentication 으로 member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member 의 status 검증
        memberService.validateMemberSignedStatus(member);

        // store id 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        // 가게 운영 시간에 맞는 시간인지 검증
        if(!validateReservationTime(store.getOpenHours(), store.getClosedHours(), request.getReservationDateTime().toLocalTime())) {
            throw new StoreException(INVALID_RESERVATION_TIME);
        }

        // 해당 시간이 term 에 맞는 적절한 시간이며, 해당 시간에 예약이 비어 있는지 검증
        if(!validateAvailableReservationTime(store, request.getReservationDateTime())) {
            throw new StoreException(ALREADY_RESERVED_TIME);
        }

        // 검증을 마쳤다면 예약
        Reservation reservation = reservationRepository.save(
                Reservation.builder()
                .reservationDateTime(request.getReservationDateTime())
                .reservationDate(request.getReservationDateTime().toLocalDate())
                .reservationPersonCount(request.getReservationPersonCount())
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
                .build();
    }

    /**
     * 매장 점주가 특정 예약에 대해 승인/거절하는 메서드
     * store, reservation, member 에 대한 검증 후,
     * 기존 reservation 의 accepted 정보를 request 로 전달된 정보로 갱신
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

        // member (authentication) 검증
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

        Reservation updateReservation = reservationRepository.save(
                reservation.modifyAccepted(request.getAccepted()));

        return ReservationAcceptResponse.builder()
                .reservationId(updateReservation.getId())
                .accepted(updateReservation.getAcceptedStatus())
                .build();
    }

    /**
     * 특정 매장의 특정 시간에 이미 예약이 차 있는지 검증하는 메서드
     * 점주가 설정한 term (예약과 예약 사이의 시간) 을 통하여
     * 매장의 open 시간부터 closed 시간까지 순회,
     * 만약 term 과 매장 운영 시간에 맞지 않은 시간에 예약했을 경우 에러 발생
     * 예약 시간이 검증에 성공하고, 해당 시간에 예약된 이력이 없다면 예약 등록
     *
     * @param store - 예약 확인하려는 매장
     * @param time - 예약 확인/등록하려는 시간
     * @return
     */
    @Transactional
    private Boolean validateAvailableReservationTime(Store store, LocalDateTime time) {

        // 특정 store id의 특정 날짜의 모든 예약 시간을 가져옴
        // 단, 점주가 거절한 예약의 경우 필터링
        List<LocalTime> reservedTimes = reservationRepository.findAllReservationByStoreIdAndReservationDate(store.getId(), time.toLocalDate())
                .stream()
                .filter(reservation -> reservation.getAcceptedStatus() == ReservationAcceptedType.ACCEPTED)
                .map(reservation -> reservation.getReservationDateTime().toLocalTime())
                .collect(Collectors.toList());

        LocalTime targetTime = time.toLocalTime();

        LocalTime term = store.getStoreReservationInfo().getReservationTerm();
        LocalTime closed = store.getClosedHours();
        LocalTime open = store.getOpenHours();

        // 점주가 설정한 운영 시간 및 term 에 맞게 예약 시간을 요청한 것인지 검증
        if(!validateReservationTimeMatchesTerm(open, closed, term, targetTime)) {
            return false;
        }

        // 당일에 예약이 하나도 없다면 true (예약 가능) 반환
        if(reservedTimes.isEmpty()) {
            return true;
        }

        // 원하는 예약 시간이 예약된 시간들 중 포함돼 있다면 false 반환
        if(reservedTimes.contains(targetTime)) {
            return false;
        }

        return true;
    }

    /**
     * 특정 매장의 운영 시간에 맞는 시간으로 예약 요청을 한 것인지 검증하는 메서드
     *
     * @param open - 가게 오픈 시간
     * @param closed - 가게 종료 시간
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
    public static boolean validateReservationTimeMatchesTerm(LocalTime open, LocalTime closed, LocalTime term, LocalTime target) {

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
     * @param reservation
     * @return
     */
    public void validateReservationAccepted(Reservation reservation) {

        ReservationAcceptedType reservationStatus = reservation.getAcceptedStatus();

        if(ReservationAcceptedType.REJECTED == reservationStatus) {
            throw new ReservationException(RESERVATION_ACCEPTED_REJECTED);
        }else if(ReservationAcceptedType.WAITING == reservationStatus) {
            throw new ReservationException(RESERVATION_ACCEPTED_WAITING);
        }else if(ReservationAcceptedType.CANCELED == reservationStatus) {
            throw new ReservationException(RESERVATION_CANCELED);
        }
    }
}
