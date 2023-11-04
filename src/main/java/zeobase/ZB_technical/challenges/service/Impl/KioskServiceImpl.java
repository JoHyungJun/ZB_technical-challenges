package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.dto.kiosk.KioskPhoneDto;
import zeobase.ZB_technical.challenges.dto.kiosk.KioskSigninDto;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.exception.KioskException;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.exception.ReservationException;
import zeobase.ZB_technical.challenges.exception.StoreException;
import zeobase.ZB_technical.challenges.repository.MemberRepository;
import zeobase.ZB_technical.challenges.repository.ReservationRepository;
import zeobase.ZB_technical.challenges.repository.StoreRepository;
import zeobase.ZB_technical.challenges.service.KioskService;
import zeobase.ZB_technical.challenges.type.ErrorCode;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static zeobase.ZB_technical.challenges.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class KioskServiceImpl implements KioskService {

    private final PasswordEncoder passwordEncoder;

    private final MemberServiceImpl memberService;
    private final ReservationServiceImpl reservationService;

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;


    @Override
    @Transactional
    public KioskPhoneDto.Response checkReservationByPhone(KioskPhoneDto.Request request) {

        // 존재하는 핸드폰 번호인지 검증
        Member member = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new KioskException(ErrorCode.NOT_FOUND_MEMBER_PHONE));

        // 회원 status 검증
        memberService.validateMemberStatus(member);

        // 존재하는 매장 id 인지 검증
        if(!storeRepository.existsById(request.getStoreId())) {
            throw new StoreException(NOT_FOUND_STORE_ID);
        }

        // 키오스크 도달 시점 날짜와 요청 정보인 member id, store id, 시간에 해당하는 예약 여부 검증
        LocalDateTime reservedDate = LocalDateTime.of(LocalDate.now(), request.getReservedTime());

        Reservation reservation = reservationRepository.findByMemberIdAndStoreIdAndReservedDateTime(
                member.getId(), request.getStoreId(), reservedDate)
                .orElseThrow(() -> new KioskException(NOT_FOUND_RESERVED_MEMBER));

        // 이미 방문한 회원인지 검증
        if(reservation.getVisitedStatus() == ReservationVisitedType.VISITED) {
            throw new ReservationException(ErrorCode.RESERVATION_ALREADY_CHECKED);
        }

        // 점주가 수락한 예약인지 검증
        reservationService.validateReservationAccepted(reservation);

        // 해당 예약을 방문 완료 상태로 변경 후 저장
        reservationRepository.save(reservation.updateVisited(ReservationVisitedType.VISITED));

        return KioskPhoneDto.Response.builder()
                .reservationChecked(true)
                .build();
    }

    @Override
    @Transactional
    public KioskSigninDto.Response checkReservationByMember(KioskSigninDto.Request request) {

        // 이용자 아이디 검증
        Member member = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_UID));

        // 이용자 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(MISMATCH_PASSWORD);
        }

        // 회원 status 검증
        memberService.validateMemberStatus(member);

        // 존재하는 매장 id 인지 검증
        if(!storeRepository.existsById(request.getStoreId())) {
            throw new StoreException(NOT_FOUND_STORE_ID);
        }

        // 키오스크 도달 시점 날짜와 요청 정보인 member id, store id, 시간에 해당하는 예약 여부 검증
        LocalDateTime reservedDate = LocalDateTime.of(LocalDate.now(), request.getReservedTime());

        Reservation reservation = reservationRepository.findByMemberIdAndStoreIdAndReservedDateTime(
                        member.getId(), request.getStoreId(), reservedDate)
                .orElseThrow(() -> new KioskException(NOT_FOUND_RESERVED_MEMBER));

        // 이미 방문한 회원인지 검증
        if(reservation.getVisitedStatus() == ReservationVisitedType.VISITED) {
            throw new ReservationException(ErrorCode.RESERVATION_ALREADY_CHECKED);
        }

        // 점주가 수락한 예약인지 검증
        reservationService.validateReservationAccepted(reservation);

        // 해당 예약을 방문 완료 상태로 변경 후 저장
        reservationRepository.save(reservation.updateVisited(ReservationVisitedType.VISITED));

        return KioskSigninDto.Response.builder()
                .reservationChecked(true)
                .build();
    }
}

