package doge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableEurekaClient
public class DogeServiceApplication {

		public static void main(String[] args) {

				ConfigurableApplicationContext applicationContext =
						SpringApplication.run(DogeServiceApplication.class, args);

				System.out
						.println(applicationContext.getEnvironment().getProperty("foo"));

		}
}

@RequestMapping("/doges")
@RestController
class DogeRestController {

		@Autowired
		DogificationRepository accountRepository;

		@RequestMapping
		List<Dogification> dogesList() {
				return this.accountRepository.findAll();
		}

		@RequestMapping("/{id}")
		Dogification username(@PathVariable Long id) {
				return this.accountRepository.findOne(id);
		}
}

interface DogificationRepository extends JpaRepository<Dogification, Long> {
}

@Entity
class Dogification {

		@Id
		@GeneratedValue
		private Long id;

		private String very;

		private String so;

		private String such;

		private Dogification() {
		} // JPA

		public Dogification(String very, String so, String such) {
				this.very = very;
				this.so = so;
				this.such = such;
		}

		public Long getId() {

				return id;
		}

		public String getVery() {
				return very;
		}

		public String getSo() {
				return so;
		}

		public String getSuch() {
				return such;
		}
}
