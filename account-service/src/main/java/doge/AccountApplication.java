package doge;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties
@EnableHystrix
@EnableEurekaClient
public class AccountApplication extends RepositoryRestMvcConfiguration {

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Account.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
}

@Component
class AccountResourceProcessor implements ResourceProcessor<Resource<Account>> {

    private final DogeClient dogeClient;

    @Autowired
    public AccountResourceProcessor(DogeClient dogeClient) {
        this.dogeClient = dogeClient;
    }

    @Override
    public Resource<Account> process(Resource<Account> accountResource) {
        Link dogeLink = this.dogeClient.buildDogeLink(
                accountResource.getContent());
        if (null != dogeLink)
            accountResource.add(dogeLink);
        return accountResource;
    }
}

@Component
class DogeClient {

    private String dogeServiceName = "doges";

    private final DiscoveryClient discoveryClient;

    @Autowired
    public DogeClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public Link defaultDogeLink(Account account) {
        return null;
    }

    @HystrixCommand(fallbackMethod = "defaultDogeLink")
    public Link buildDogeLink(Account account) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(dogeServiceName, false);
        String url = UriComponentsBuilder.fromHttpUrl(instance.getHomePageUrl() + "/doges/{key}/photos")
                .buildAndExpand(Long.toString(account.getId())).toUriString();
        return new Link(url, "doges");
    }


}

@RepositoryRestResource
interface AccountRepository extends JpaRepository<Account, Long> {
}

@Entity
class Account {

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }


    @Id
    @GeneratedValue
    private Long id;

    private String username;

    Account() { // JPA only
    }

    public Account(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
