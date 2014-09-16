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
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints like : http://joshs-macbook-pro.local:8082/dogePhotos/search/findOneByIdAndUserId?id=541772453004963ddb67dc77&userId=1
 * add files like :  curl -F "file=@/Users/jlong/Desktop/img.png" http://joshs-macbook-pro.local:8082/dogePhotos/{dogePhotoId}/photo
 *
 * Or, simply use the dogifier endpoint directly:
 *
 * curl -F "file=@/Users/jlong/Desktop/img.jpg" http://joshs-macbook-pro.local:8082/dogifier/1
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableEurekaClient
public class DogeApplication extends RepositoryRestMvcConfiguration {

    @Bean
    CommandLineRunner init ( DogePhotoRepository dogePhotoRepository ){
        return a->{
            dogePhotoRepository.deleteAll();
        } ;
    }

    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(DogePhoto.class);
    }

    @Bean
    @RefreshScope
    DogePhotoManipulator dogePhotoManipulator(@Value("${very-so-much-count}") int countOfExclamations,
                                              Environment environment) {

        DogePhotoManipulator dogePhotoManipulator = new DogePhotoManipulator();

        for (int i = 0; i <countOfExclamations ; i++) {
            String[] e = environment.getProperty("very-so-much-" + (1 + i)).split(" ");
            dogePhotoManipulator.addTextOverlay(e[0], e[1], e[2]);
        }

        return dogePhotoManipulator;
    }

    public static void main(String[] args) {
        SpringApplication.run(DogeApplication.class, args);
    }
}


@Component
class DogePhotoResourceProcessor implements ResourceProcessor<Resource<DogePhoto>> {

    @Override
    public Resource<DogePhoto> process(Resource<DogePhoto> dogePhotoResource) {
        Link self = dogePhotoResource.getLink("self");
        URI photoURI = UriComponentsBuilder.fromHttpUrl(self.getHref())
                .path("/photo")
                .build()
                .toUri();
        Link photoLink = new Link(photoURI.toString(), "photo");
        dogePhotoResource.getLinks().add(photoLink);
        return dogePhotoResource;
    }
}

@RestController
class DogeRestController {

    private void writeDogePhoto(String id, Photo photo) throws IOException {
        photo = this.dogePhotoManipulator.manipulate(photo);
        String fileRef = this.dogePhotoRepository.findOne(id).getFileRef();
        try (InputStream inputStream = photo.getInputStream()) {
            this.fs.store(inputStream, fileRef);
        }
    }

    private Photo findDogePhoto(String dogePhotoId) throws IOException {
        DogePhoto dogePhoto = this.dogePhotoRepository.findOne(dogePhotoId);
        return () -> this.fs.getResource(dogePhoto.getFileRef()).getInputStream();
    }

    // todo make this show up in the root resource collection
    @RequestMapping(value = "/dogifier/{userId}", method = RequestMethod.POST)
    ResponseEntity<?> dogifier(
            @PathVariable String userId,
            @RequestParam MultipartFile file,
            UriComponentsBuilder uriComponentsBuilder)
            throws IOException {

        String fileRef = UUID.randomUUID() + ".jpg";
        DogePhoto dogePhoto = new DogePhoto(userId, fileRef);
        dogePhotoRepository.save(dogePhoto);

        return insertPhoto(dogePhoto.getId(), file, uriComponentsBuilder);
    }

    @RequestMapping(value = "/dogePhotos/{id}/photo", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<PhotoResource> read(@PathVariable String id) throws IOException {
        Photo photo = findDogePhoto(id);

        //todo response body?
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);

        PhotoResource p = new PhotoResource(photo);
        return new ResponseEntity<>(p, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/dogePhotos/{id}/photo", method = RequestMethod.POST)
    ResponseEntity<?> insertPhoto(@PathVariable String id, @RequestParam MultipartFile file, UriComponentsBuilder uriBuilder) throws IOException {
        Photo photo = file::getInputStream;
        writeDogePhoto(id, photo);

        URI uri = uriBuilder.path("/dogePhotos/{id}/").buildAndExpand(id).toUri();

        Map<String, String> msg = new HashMap<>();
        msg.put("dogePhotoUri", uri.toString());
        msg.put("id", id);
        msg.put("uploadDate", java.time.Clock.systemUTC().instant().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);
    }

    private final DogePhotoRepository dogePhotoRepository;
    private final GridFsTemplate fs;
    private final DogePhotoManipulator dogePhotoManipulator;

    @Autowired
    public DogeRestController(
            DogePhotoRepository dogePhotoRepository,
            GridFsTemplate fs,
            DogePhotoManipulator dogePhotoManipulator) {

        this.dogePhotoManipulator = dogePhotoManipulator;
        this.dogePhotoRepository = dogePhotoRepository;
        this.fs = fs;
    }

}

