package doge;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

/**
 * Load this app up 127.0.0.1:8081/accounts and you should also see properties in
 * github refelcted under 127.0.0.1:8080/env.
 * <p>
 * You can change the github repository properties and then refresh the endpoints
 * ({@code curl  http://localhost:8081/refresh -d ''})
 * and see the github repository configuration reflected in http://localhost:8081/env.
 * <p>
 * In theory, you should also see beans refreshed.
 * <p>
 * The ReliabelService demonstrates making a call to soemthing that might fail and then
 * getting a sane default value.
 * Run the Hystrix Dashboard application and then enter
 * http://localhost:7980
 * <p>
 * Then tell Hystrix to monitor the stream from accounts-service using the URI
 * http://127.0.0.1:8081/hystrix.stream
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties
@EnableHystrix
@EnableEurekaClient
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}

@RefreshScope
@RestController
@RequestMapping("/accounts")
class AccountRestController {

    @Value("${accounts.message}")
    String message;

    @Autowired
    Environment environment;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ReliableService reliableService;

    @RequestMapping("/reliable-message")
    String reliableMessage() {
        return this.reliableService.reliableMessage();
    }

    @RequestMapping("/message")
    String message() {
        return this.message;
    }

    @RequestMapping
    List<Account> accountList() {
        return this.accountRepository.findAll();
    }

    @RequestMapping("/{username}")
    Account username(@PathVariable String username) {
        return this.accountRepository.findByUsername(username);
    }
}

interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
}

@Service
class ReliableService {

    private String defaultMessage = "something by default";

    @Autowired
    private DiscoveryClient discoveryClient;

    public String defaultMessage() {
        return this.defaultMessage;
    }

    /**
     * Call this 20 times in a second and if it fails enough itll poen the circuiot.
     *
     * if not, itll fallback.
     *
     * @return
     */
    @HystrixCommand(fallbackMethod = "defaultMessage")
    public String reliableMessage() {
        try {
            InstanceInfo instance = discoveryClient.getNextServerFromEureka(
                    "accounts", false);
            System.out.println(instance.getHomePageUrl());
        } catch (RuntimeException e) {
            // Eureka not available
        }

        if (Math.random() > .5)
            return "Reliable message";
        throw new RuntimeException("NUUUUUUU");
    }


    /*//TODO: add hystrix caching
    @HystrixCommand(fallbackMethod = "defaultLink")
    public Link getStoresByLocationLink(Map<String, Object> parameters) {
        URI storesUri = URI.create(uri);

        try {
            InstanceInfo instance = discoveryClient.getNextServerFromEureka("stores", false);
            storesUri = URI.create(instance.getHomePageUrl());
        }
        catch (RuntimeException e) {
            // Eureka not available
        }


        Traverson traverson = new Traverson(storesUri, MediaTypes.HAL_JSON);
        Link link = traverson.follow("stores", "search", "by-location").withTemplateParameters(parameters).asLink();


        return link;
    }

    public Link defaultLink(Map<String, Object> parameters) {
        return null;
    }
*/

}

@Entity
class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    public Account() {
    }

    public Account(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
