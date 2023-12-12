package zeobase.zbtechnical.challenges;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZbTechnicalChallengesApplicationTests {

	@Test
	void ymlCronTest(@Value("${spring.scheduler.reviewAvailabilityVisitedReservation.cron}") String cron) {
		System.out.println(cron);
	}

	@Test
	void ymlRedisTest(@Value("${spring.redis.host}") String redisHost,
				 	  @Value("${spring.redis.port}") Integer redisPort) {
		System.out.println("redis host : " + redisHost + ", port : " + redisPort);
	}
}
