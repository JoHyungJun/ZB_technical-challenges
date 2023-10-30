package zeobase.ZB_technical.challenges;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ZbTechnicalChallengesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZbTechnicalChallengesApplication.class, args);
	}

}
