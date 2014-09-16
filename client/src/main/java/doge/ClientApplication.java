package doge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * the client uses <A href="https://github.com/Netflix/zuul">Netflix's Zuul proxy</A>
 * and is example of the  <A href = "http://microservices.io/patterns/apigateway.html">
 * API Gateway pattern</A>.
 *
 * @author Josh Long
 */
@Configuration
@EnableZuulProxy
@EnableAutoConfiguration
@ComponentScan
public class ClientApplication {

    public static void main(String args[]) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
