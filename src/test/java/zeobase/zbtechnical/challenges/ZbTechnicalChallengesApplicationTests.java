package zeobase.zbtechnical.challenges;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZbTechnicalChallengesApplicationTests {

	@Test
	void ymlTest(@Value("${spring.scheduler.reviewAvailabilityVisitedReservation.cron}") String cron) {
		System.out.println(cron);
	}
}
