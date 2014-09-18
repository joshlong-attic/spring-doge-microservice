package doge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * the client uses <A href="https://github.com/Netflix/zuul">Netflix's Zuul proxy</A>
 * and is example of the  <A href = "http://microservices.io/patterns/apigateway.html">
 * API Gateway pattern</A>.
 *
 * @author Josh Long
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableZuulProxy
public class ClientApplication {

    @Bean
    WebMvcConfigurerAdapter mvcViewConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("client");
                registry.addViewController("/monitor").setViewName("monitor");
            }
        };
    }

 /*   @Configuration
    @EnableWebSocketMessageBroker
    static class WebSocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/doge").withSockJS();
        }

        @Override
        public void configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/queue/", "/topic/");
            registry.setApplicationDestinationPrefixes("/app");
        }
    }*/

    public static void main(String args[]) {
        SpringApplication.run(ClientApplication.class, args);
    }

}
