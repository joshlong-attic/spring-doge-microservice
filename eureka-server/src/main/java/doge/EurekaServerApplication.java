package doge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <A href="https://github.com/Netflix/eureka">Eureka</A> is the Netflix service registry.
 * It handles  service registration and discovery of services in a cluster.
 * Clients need simply place {@link org.springframework.cloud.netflix.eureka.EnableEurekaClient} on their classes
 * and they'll be automatically <EM>registered</EM> with Eureka at runtime.
 * <p> Start this and then check out <A href="http://localhost:8761/v2/apps">XML representation
 * of all the services that have been registered with Eureka</A>.
 * <p> Open up <A href="http://127.0.0.1:8761">localhost:8761</A> to see all the registered services. Think of this as
 * a <EM>cluster</EM> start page.
 *
 * @author Josh Long
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
