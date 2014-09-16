package doge;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This will be available at <A href="://127.0.0.1:7980/">localhost:7980</a>.
 * <p>
 * When it opens, add the Hystrix stream for your service, like http://localhost:8081/hystrix.stream for the Accounts endpoint.
 */
@Configuration
@ComponentScan
@Controller
@EnableAutoConfiguration
@EnableHystrixDashboard
public class HystrixDashboardApplication {

    @RequestMapping("/")
    public String home() {
        return "forward:/hystrix/index.html";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixDashboardApplication.class)
                .web(true)
                .run(args);
    }

}
