package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.dto.kiosk.KioskPhoneDto;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.exception.KioskException;
import zeobase.ZB_technical.challenges.exception.ReservationException;
import zeobase.ZB_technical.challenges.repository.MemberRepository;
import zeobase.ZB_technical.challenges.repository.ReservationRepository;
import zeobase.ZB_technical.challenges.service.KioskService;
import zeobase.ZB_technical.challenges.type.ErrorCode;
import zeobase.ZB_technical.challenges.type.ReservationAcceptedType;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

@Service
@RequiredArgsConstructor
public class KioskServiceImpl implements KioskService {

    private final MemberServiceImpl memberService;

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;


    @Override
    @Transactional
    public KioskPhoneDto.Response checkReservationByPhone(KioskPhoneDto.Request request) {

        // 존재하는 핸드폰 번호인지 검증
        Member member = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new KioskException(ErrorCode.NOT_FOUND_MEMBER_PHONE));

        // 회원 status 검증
        memberService.validateMemberStatus(member);

        // 이미 예약 확인한 회원인지 검증
        Reservation reservation = reservationRepository.findByMemberId(member.getId()).get();
        if(reservation.getVisitedStatus() == ReservationVisitedType.VISITED) {
            throw new ReservationException(ErrorCode.RESERVATION_ALREADY_CHECKED);
        }

        // 점주가 거절한 예약인지 검증 (점주의 선택을 기다리는 중이라면 예외 발생 x)
        if(reservation.getAcceptedStatus() == ReservationAcceptedType.REJECTED) {
            throw new ReservationException(ErrorCode.RESERVATION_ACCEPTED_REJECTED);
        }

        // 해당 예약을 방문 완료 상태로 변경 후 저장
        reservationRepository.save(reservation.updateVisited(ReservationVisitedType.VISITED));

        return KioskPhoneDto.Response.builder()
                .message("방문 처리가 완료되었습니다.")
                .build();
    }
}
