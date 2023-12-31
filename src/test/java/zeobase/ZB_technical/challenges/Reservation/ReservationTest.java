package zeobase.ZB_technical.challenges.Reservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.repository.ReservationRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
                .reservedDateTime(reservedDate)
                .build();

        reservation = reservationRepository.save(reservation);

        //then
        LocalDateTime date = LocalDateTime.of(2023, 10, 18, 12, 30);
        assertEquals(date, reservationRepository.findById(reservation.getId()).get().getReservedDateTime());
    }
}