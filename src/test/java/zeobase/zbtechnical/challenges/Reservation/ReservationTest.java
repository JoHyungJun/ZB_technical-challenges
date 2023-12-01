package zeobase.zbtechnical.challenges.Reservation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.repository.ReservationRepository;

import java.time.LocalDateTime;

@SpringBootTest
class ReservationTest {

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void 날짜() {
        //given
        LocalDateTime reservedDate = LocalDateTime.of(2023, 10, 18, 12, 30, 30);

        //when
        Reservation reservation = Reservation.builder()
                .reservationDateTime(reservedDate)
                .build();

        reservation = reservationRepository.save(reservation);

        //then
        LocalDateTime date = LocalDateTime.of(2023, 10, 18, 12, 30);
        Assertions.assertEquals(date, reservationRepository.findById(reservation.getId()).get().getReservationDateTime());
    }
}