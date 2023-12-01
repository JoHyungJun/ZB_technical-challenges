package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.kiosk.request.*;
import zeobase.zbtechnical.challenges.dto.kiosk.response.*;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.exception.KioskException;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.exception.ReservationException;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.repository.ReservationRepository;
import zeobase.zbtechnical.challenges.repository.StoreRepository;
import zeobase.zbtechnical.challenges.service.KioskService;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;
import zeobase.zbtechnical.challenges.type.reservation.ReservationVisitedType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.*;

/**
 * 키오스크 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class KioskServiceImpl implements KioskService {

    private final PasswordEncoder passwordEncoder;

    private final MemberServiceImpl memberService;
    private final ReservationServiceImpl reservationService;

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;


    /**
     * 핸드폰 번호로 키오스크에서 방문 확인을 진행하는 메서드
     * request 로 전달된 정보와, 매장, 예약, 이용자에 대한 검증 후
     * 해당 예약 정보를 '이용자가 방문했음'으로 갱신
     * 
     * @param request - 핸드폰 번호, 매장 정보, 예약 정보
     * @return "dto/kiosk/response/KioskPhoneResponse"
     * @exception KioskException
     * @exception StoreException
     * @exception MemberException
     */
    @Override
    @Transactional
    public KioskPhoneResponse checkReservationByPhone(KioskPhoneRequest request) {

        // 존재하는 핸드폰 번호인지 검증
        Member member = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new KioskException(ErrorCode.NOT_FOUND_MEMBER_PHONE));

        // 이용자 status 검증
        memberService.validateMemberSignedStatus(member);

        // 존재하는 매장 id 인지 검증
        if(!storeRepository.existsById(request.getStoreId())) {
            throw new StoreException(NOT_FOUND_STORE_ID);
        }

        // 키오스크 도달 시점 날짜와 요청 정보인 member id, store id, 시간에 해당하는 예약 여부 검증
        LocalDateTime reservedDate = LocalDateTime.of(LocalDate.now(), request.getReservedTime());

        Reservation reservation = reservationRepository.findByMemberIdAndStoreIdAndReservationDateTime(
                member.getId(), request.getStoreId(), reservedDate)
                .orElseThrow(() -> new KioskException(NOT_FOUND_RESERVED_MEMBER));

        // 이미 방문한 회원인지 검증
        if(reservation.getVisitedStatus() == ReservationVisitedType.VISITED) {
            throw new ReservationException(ErrorCode.RESERVATION_ALREADY_CHECKED);
        }

        // 점주가 수락한 예약인지 검증
        reservationService.validateReservationAccepted(reservation);

        // 해당 예약을 방문 완료 상태로 변경 후 저장
        reservationRepository.save(reservation.modifyVisited(ReservationVisitedType.VISITED));

        return KioskPhoneResponse.builder()
                .reservationChecked(true)
                .build();
    }

    /**
     * 이용자 id, password로 키오스크에서 방문 확인을 진행하는 메서드
     * request 로 전달된 정보와, 매장, 예약, 이용자에 대한 검증 후
     * 해당 예약 정보를 '이용자가 방문했음'으로 갱신
     *
     * @param request - id, password, 매장 정보, 예약 정보
     * @return "dto/kiosk/response/KioskSigninResponse"
     * @exception KioskException
     * @exception StoreException
     * @exception MemberException
     */
    @Override
    @Transactional
    public KioskSigninResponse checkReservationByMember(KioskSigninRequest request) {

        // 이용자 아이디 검증
        Member member = memberRepository.findByUID(request.getUID())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_UID));

        // 이용자 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(MISMATCH_PASSWORD);
        }

        // 회원 status 검증
        memberService.validateMemberSignedStatus(member);

        // 존재하는 매장 id 인지 검증
        if(!storeRepository.existsById(request.getStoreId())) {
            throw new StoreException(NOT_FOUND_STORE_ID);
        }

        // 키오스크 도달 시점 날짜와 요청 정보인 member id, store id, 시간에 해당하는 예약 여부 검증
        LocalDateTime reservedDate = LocalDateTime.of(LocalDate.now(), request.getReservedTime());

        Reservation reservation = reservationRepository.findByMemberIdAndStoreIdAndReservationDateTime(
                        member.getId(), request.getStoreId(), reservedDate)
                .orElseThrow(() -> new KioskException(NOT_FOUND_RESERVED_MEMBER));

        // 이미 방문한 회원인지 검증
        if(reservation.getVisitedStatus() == ReservationVisitedType.VISITED) {
            throw new ReservationException(ErrorCode.RESERVATION_ALREADY_CHECKED);
        }

        // 점주가 수락한 예약인지 검증
        reservationService.validateReservationAccepted(reservation);

        // 해당 예약을 방문 완료 상태로 변경 후 저장
        reservationRepository.save(reservation.modifyVisited(ReservationVisitedType.VISITED));

        return KioskSigninResponse.builder()
                .reservationChecked(true)
                .build();
    }
}

