package doge;

import doge.photo.DogePhotoManipulator;
import doge.photo.Photo;
import doge.photo.PhotoResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Upload new photos using  {@code curl -F "file=@/Users/jlong/Desktop/img.jpg" http://joshs-macbook-pro.local:8082/doges/24232/photos}.
 *
 * @author Josh Long
 * @author Phillip Webb
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableEurekaClient
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class DogeApplication {

    @Bean
    CommandLineRunner init(DogePhotoRepository dogePhotoRepository) {
        return a -> dogePhotoRepository.deleteAll();
    }

    @Bean
    @RefreshScope
    DogePhotoManipulator dogePhotoManipulator(
            @Value("${very-so-much-count}") int countOfExclamations,
            Environment environment) {
        DogePhotoManipulator dogePhotoManipulator = new DogePhotoManipulator();
        for (int i = 0; i < countOfExclamations; i++) {
            String[] e = environment.getProperty("very-so-much-" + (1 + i)).split(" ");
            dogePhotoManipulator.addTextOverlay(e[0], e[1], e[2]);
        }
        return dogePhotoManipulator;
    }

    public static void main(String[] args) {
        SpringApplication.run(DogeApplication.class, args);
    }
}

interface DogePhotoRepository extends MongoRepository<DogePhoto, String> {

    Collection<DogePhoto> findByKey(String key);

    DogePhoto findByKeyAndId(String key, String id);
}

class DogePhoto {

    public DogePhoto() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DogePhoto(String key) {
        this.key = key;
    }

    @Id
    private String id;
    private String key;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }
}

@Component
class DogePhotoResourceProcessor implements ResourceProcessor<Resource<DogePhoto>> {

    @Override
    public Resource<DogePhoto> process(Resource<DogePhoto> resource) {

        DogePhoto dogePhoto = resource.getContent();
        String key = dogePhoto.getKey();
        String id = dogePhoto.getId();
        resource.add(new Link(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/doges/{key}/photos/{id}/photo").buildAndExpand(key, id).toString(), "photo"));

        return resource;
    }
}

@RestController
@RequestMapping(value = "/doges/{key}/photos", produces = MediaType.APPLICATION_JSON_VALUE)
class DogePhotoController {

    private final DogePhotoRepository dogePhotoRepository;
    private final GridFsTemplate fs;
    private final DogePhotoManipulator dogePhotoManipulator;

    @RequestMapping(method = RequestMethod.GET)
    Collection<Resource<DogePhoto>> readPhotos(@PathVariable String key) {
        return this.dogePhotoRepository.findByKey(key)
                .stream()
                .map(dp -> new Resource<DogePhoto>(dp))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Resource<DogePhoto> readPhoto(@PathVariable String key, @PathVariable String id) {
        DogePhoto dogePhoto = this.dogePhotoRepository.findByKeyAndId(key, id);
        return new Resource<DogePhoto>(dogePhoto);
    }

    @RequestMapping(value = "/{id}/photo", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<PhotoResource> readPhotoResource(@PathVariable String key, @PathVariable String id) {
        DogePhoto dogePhoto = this.dogePhotoRepository.findByKeyAndId(key, id);
        Photo photo = () -> this.fs.getResource(dogePhoto.getId()).getInputStream();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(new PhotoResource(photo), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    ResponseEntity<Resource<DogePhoto>> insertPhoto(@PathVariable String key,
                                                    @RequestParam MultipartFile file,
                                                    UriComponentsBuilder uriBuilder) throws IOException {
        Photo photo = this.dogePhotoManipulator.manipulate(file::getInputStream);
        DogePhoto dogePhoto = this.dogePhotoRepository.save(new DogePhoto(key));
        String id = dogePhoto.getId();
        try (InputStream inputStream = photo.getInputStream()) {
            this.fs.store(inputStream, id);
        }
        URI uri = uriBuilder.path("/doges/{key}/photos/{id}/photo").buildAndExpand(key, id).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<>(
                this.readPhoto(key, id), headers, HttpStatus.CREATED);
    }

    @Autowired
    public DogePhotoController(DogePhotoRepository dogeRepos, GridFsTemplate gridFileSystem, DogePhotoManipulator dogeManipulator) {
        this.dogePhotoManipulator = dogeManipulator;
        this.dogePhotoRepository = dogeRepos;
        this.fs = gridFileSystem;
    }

}