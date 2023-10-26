package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import zeobase.ZB_technical.challenges.dto.KioskPhone;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.exception.KioskException;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.exception.ReservationException;
import zeobase.ZB_technical.challenges.repository.MemberRepository;
import zeobase.ZB_technical.challenges.repository.ReservationRepository;
import zeobase.ZB_technical.challenges.service.KioskService;
import zeobase.ZB_technical.challenges.type.ErrorCode;
import zeobase.ZB_technical.challenges.type.MemberStatusType;
import zeobase.ZB_technical.challenges.type.ReservationAcceptedType;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

@Service
@RequiredArgsConstructor
public class KioskServiceImpl implements KioskService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;


    // 핸드폰 번호로 멤버를 찾고, 예약 상태를 방문한 상태로 교체.
    @Override
    public KioskPhone.Response checkReservationByPhone(KioskPhone.Request request) {

        // 존재하는 핸드폰 번호인지 검증
        Member member = memberRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new KioskException(ErrorCode.MEMBER_PHONE_NOT_FOUND));

        // 탈퇴한 회원인지 검증
        // TODO : security가 막아줄 듯. 추후 체크 후 제거.
        if(member.getStatus() == MemberStatusType.WITHDRAW){
            throw new MemberException(ErrorCode.MEMBER_WITHDRAW);
        }

        // 이미 예약 확인한 회원인지 검증
        Reservation reservation = reservationRepository.findByMemberId(member.getId()).get();
        if(reservation.getVisited() == ReservationVisitedType.VISITED) {
            throw new ReservationException(ErrorCode.RESERVATION_ALREADY_CHECKED);
        }

        // 점주가 거절한 예약인지 검증 (점주의 선택을 기다리는 중이라면 예외 발생 x)
        if(reservation.getAccepted() == ReservationAcceptedType.REJECTED) {
            throw new ReservationException(ErrorCode.RESERVATION_ACCEPTED_REJECTED);
        }

        reservationRepository.save(reservation.updateVisited(ReservationVisitedType.VISITED));


        return KioskPhone.Response.builder()
                .success(true)
                .message("방문 처리가 완료되었습니다.")
                .build();
    }
}
