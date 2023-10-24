package zeobase.ZB_technical.challenges.Kiosk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.dto.request.KioskPhoneRequest;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.repository.MemberRepository;
import zeobase.ZB_technical.challenges.repository.ReservationRepository;
import zeobase.ZB_technical.challenges.service.KioskService;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KioskTest {

    @Autowired
    private KioskService kioskService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @Transactional
    void 예약방문확인() {
        //given
        Member member = Member.builder()
                .phone("010-1234-5678")
                .build();

        member = memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .build();

        reservation = reservationRepository.save(reservation);

        //when
        KioskPhoneRequest kioskPhoneRequest = KioskPhoneRequest.builder()
                .phone("010-1234-5678")
                .build();

        kioskService.checkReservationByPhone(kioskPhoneRequest);

        //then
        //reservation = reservationRepository.findById(reservation.getId()).get();
        assertEquals(ReservationVisitedType.VISITED, reservation.getVisited());
    }
}