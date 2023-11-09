package zeobase.zbtechnical.challenges.Kiosk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.dto.request.KioskPhoneRequest;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.repository.ReservationRepository;
import zeobase.zbtechnical.challenges.service.KioskService;
import zeobase.zbtechnical.challenges.type.ReservationVisitedType;

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