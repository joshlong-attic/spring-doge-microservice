package doge;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by sgibb on 7/11/14.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@Controller
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
