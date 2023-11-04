package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationAvailableDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationReserveDto;
import zeobase.ZB_technical.challenges.dto.reservation.ReservationInfoDto;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.entity.Store;
import zeobase.ZB_technical.challenges.exception.ReservationException;
import zeobase.ZB_technical.challenges.exception.StoreException;
import zeobase.ZB_technical.challenges.repository.ReservationRepository;
import zeobase.ZB_technical.challenges.repository.StoreRepository;
import zeobase.ZB_technical.challenges.service.ReservationService;
import zeobase.ZB_technical.challenges.type.ReservationAcceptedType;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static zeobase.ZB_technical.challenges.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final MemberServiceImpl memberService;
    private final StoreServiceImpl storeService;

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;


    // 특정 store id를 통해 전체 예약 불러오기
    @Override
    @Transactional(readOnly = true)
    public List<ReservationInfoDto> getReservationsInfoByStoreId(Long storeId) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        List<Reservation> reservations = reservationRepository.findAllByStoreId(storeId, Sort.by(
                    Sort.Order.asc("reservedDate")));

        return reservations.stream()
                .map(reservation -> ReservationInfoDto.fromEntity(reservation))
                .collect(Collectors.toList());
    }

    // 특정 가게에 대한 특정 시간이 예약 가능 시간인지 검증
    @Override
    @Transactional(readOnly = true)
    public ReservationAvailableDto existsAvailableReservationTime(Long storeId, LocalDateTime reservationTime) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        return ReservationAvailableDto.builder()
                .isAvailable(validateAvailableReservationTime(store, reservationTime))
                .build();
    }

    @Override
    @Transactional
    public ReservationReserveDto.Response reserve(ReservationReserveDto.Request request, Authentication authentication) {

        // authentication으로 member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member의 status 검증
        memberService.validateMemberStatus(member);

        // store id 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        // 예약 가능한 시간인지 검증
        if(!validateAvailableReservationTime(store, request.getReservationDateTime())) {
            throw new StoreException(ALREADY_RESERVED_TIME);
        }

        // 검증을 마쳤다면 예약
        Reservation reservation = reservationRepository.save(Reservation.builder()
                .reservedDateTime(request.getReservationDateTime())
                .reservedDate(request.getReservationDateTime().toLocalDate())
                .acceptedStatus(ReservationAcceptedType.WAITING)
                .visitedStatus(ReservationVisitedType.UNVISITED)
                .member(member)
                .store(store)
                .build()
        );

        store.getReservations().add(reservation);

        return ReservationReserveDto.Response.builder()
                .reservationId(reservation.getId())
                .memberId(reservation.getMember().getId())
                .storeId(reservation.getStore().getId())
                .reservedDateTime(reservation.getReservedDateTime())
                .build();
    }

    private Boolean validateAvailableReservationTime(Store store, LocalDateTime time) {

        // 특정 store id의 특정 날짜의 모든 예약 시간을 가져옴
        // 단, 점주가 거절한 예약의 경우 필터링
        List<LocalTime> reservedTimes = reservationRepository.findAllReservationByStoreIdAndReservedDate(store.getId(), time.toLocalDate())
                .stream()
                .filter(reservation -> reservation.getAcceptedStatus() != ReservationAcceptedType.REJECTED)
                .map(reservation -> reservation.getReservedDateTime().toLocalTime())
                .collect(Collectors.toList());

        LocalTime targetTime = time.toLocalTime();

        LocalTime term = store.getReservationTerm();
        LocalTime closed = store.getClosedHours();
        LocalTime open = store.getOpenHours();

        // open 시간 전, 혹은 closed 시간 후의 예약은 에러
        if(targetTime.isBefore(open) || targetTime.isAfter(closed)) {
            throw new StoreException(INVALID_RESERVATION_TIME);
        }

        // 점주가 설정한 term에 맞게 예약 시간을 요청한 것인지 검증
        LocalTime cur = store.getOpenHours();
        while(!cur.isAfter(closed)) {

            if(targetTime.isAfter(cur) &&
                targetTime.isBefore(cur.plusHours(term.getHour()).plusMinutes(term.getMinute()))) {

                throw new StoreException(INVALID_RESERVATION_TIME);
            }

            cur = cur.plusHours(term.getHour()).plusMinutes(term.getMinute());
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
