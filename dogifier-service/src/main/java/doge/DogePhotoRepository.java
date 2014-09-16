package doge;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource
public interface DogePhotoRepository extends MongoRepository<DogePhoto, String> {
    @RestResource(rel = "by-user-id")
    List<DogePhoto> findByUserId(  @Param("userId") String userId);

    DogePhoto findOneByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
}
