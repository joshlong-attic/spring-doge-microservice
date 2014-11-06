package doge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This will look in the .git repository configured in {@code application.yml}
 * and attempt to resolve configuration files in the remote .git repository
 * matching the {@code spring.application.name}.properties. Thus
 * {@code spring.application.name} of {@code account-service} would yeild
 * all the values in {@code account-service.properties} being available
 * through the {@link org.springframework.core.env.Environment}.
 * <p>
 * You can confirm that this works by visiting $URI/$APP_NAME/default. This will show you for example the values in
 * {@code doges.yml} if your {@code $APP_NAME} is 'doges'
 * <p>
 *
 * @author Josh Long
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigServer
public class ConfigurationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigurationServerApplication.class, args);
    }
}

