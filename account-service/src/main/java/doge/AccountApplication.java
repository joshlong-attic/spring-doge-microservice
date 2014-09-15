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
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URI;
import java.util.Optional;

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
class AccountResourceProcessor
        implements ResourceProcessor<Resource<Account>> {

    private final DogeIntegration dogeIntegration;

    @Autowired
    public AccountResourceProcessor(DogeIntegration dogeIntegration) {
        this.dogeIntegration = dogeIntegration;
    }

    @Override
    public Resource<Account> process(Resource<Account> accountResource) {
        Link dogeLink = this.dogeIntegration.buildDogeLink(accountResource.getContent());
        Optional.of(dogeLink).ifPresent(accountResource::add);
        return accountResource;
    }
}

@Component
class DogeIntegration {

    private String dogeServiceName = "doges";

    private final DiscoveryClient discoveryClient;

    @Autowired
    public DogeIntegration(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    Link defaultDogeLink(Account account){
        return null;
    }

    @HystrixCommand (fallbackMethod = "defaultDogeLink")
    public Link buildDogeLink(Account account) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(dogeServiceName, false);
        URI storesUri = URI.create(instance.getHomePageUrl());
        Link nLine = doWithUrI(storesUri , dogeServiceName);
        return  new Link(storesUri.toString(), dogeServiceName);
    }

    private Link doWithUrI(URI storesUri , String rel ) {

      //  Traverson traverson  = new Traverson( storesUri,  MediaTypes.HAL_JSON);

         return null ;

    }

}

/*
@Component
class UserResourceProcessor implements ResourceProcessor<Resource<User>> {

    private final DiscoveryClient discoveryClient;

    @Autowired
    public UserResourceProcessor(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    // todo this is the part of the demo that's unreliable
    // todo and thus needs Hystrix
    protected Link dogeLink(String userId) {

        URI storesUri = URI.create(uri);

        try {
            InstanceInfo instance = discoveryClient
                    .getNextServerFromEureka("stores", false);
            storesUri = URI.create(instance.getHomePageUrl());
        } catch (RuntimeException e) {
            // Eureka not available
        }

        System.out.println("Trying to access the stores system at " + storesUri);

        Traverson traverson = new Traverson(storesUri, MediaTypes.HAL_JSON);
        Link link = traverson.follow("stores", "search", "by-location")
                .withTemplateParameters(parameters).asLink();

        return null;
    }

    @Override
    public Resource<User> process(Resource<User> userResource) {
        User user = userResource.getContent();
        String idForUser = Long.toString(user.getId());

        return null;
    }
}*/
/*


@Component
   class CustomerResourceProcessor implements ResourceProcessor<Resource<Customer>> {

    private final StoreIntegration storeIntegration;

    @Override
    public Resource<Customer> process(Resource<Customer> resource) {

        Customer customer = resource.getContent();
        Location location = customer.getAddress().getLocation();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("location", String.format("%s,%s", location.getLatitude(),
                location.getLongitude()));
        parameters.put("distance", "50km");
        Link link = storeIntegration.getStoresByLocationLink(parameters);
        if (link != null) {
            resource.add(link.withRel("stores-nearby"));
        }

        return resource;
    }
}
*/

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
