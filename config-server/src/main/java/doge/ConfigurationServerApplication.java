package doge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This will look in the .git repository configured in {@code application.yml}
 * and attempt to resolve configuration files in the remote .git repository
 * matching the {@code spring.application.name}.properties. Thus
 * {@code spring.application.name} of {@code account-service} would yeild
 * all the values in {@code account-service.properties} being available
 * through the {@link org.springframework.core.env.Environment}.
 */
@Configuration
@EnableAutoConfiguration
@EnableEurekaClient
@EnableConfigServer
public class ConfigurationServerApplication {

		public static void main(String[] args) {

				SpringApplication.run(ConfigurationServerApplication.class, args);
				System.out.println(
						"from REST:" + new RestTemplate()
								.getForEntity("http://127.0.0.1:8888/accounts/master/",
										String.class));
		}
}

