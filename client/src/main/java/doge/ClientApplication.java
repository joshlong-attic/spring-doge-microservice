package doge;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.jms.ConnectionFactory;

/**
 * the client uses <A href="https://github.com/Netflix/zuul">Netflix's Zuul proxy</A>
 * and is example of the  <A href = "http://microservices.io/patterns/apigateway.html">
 * API Gateway pattern</A>.
 * <p>
 * This is just a proxy to other services. It is also where we expose websocket
 * services. So, we need a way to have REST calls serviced by Sprng MVC instances running on other machines to deliver websockets to
 * clients on any and all nodes. We need some sort of persistence that lets the messages escape the VM.
 * <p>
 * We can use ActiveMQ or RabbitMQ here. <A href="https://www.instapaper.com/read/513816120"> Here's an example</a>
 *
 * @author Josh Long
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableZuulProxy
@EnableWebSocketMessageBroker
public class ClientApplication extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/doge").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
     registry.enableSimpleBroker("/topic/");
 /*       registry.enableStompBrokerRelay("/topic,/queue")
                .setRelayHost("127.0.0.1")
                .setRelayPort(61613)
                .setSystemHeartbeatReceiveInterval(20 * 1000)
                .setSystemHeartbeatSendInterval(20 * 1000);
 */       //sometimes in a cloud you need to also setVirtualHost
    }

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

/*
    @Bean
    ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616?jms.useAsyncSend=true");
    }

    @Bean
    JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }
*/

    public static void main(String args[]) {
        SpringApplication.run(ClientApplication.class, args);
    }

}
