package doge;

import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;

public class DogePhoto {

    @Id
    private String id;

    private String userId;

    private String fileRef;

    DogePhoto() {
    }

    public DogePhoto(String userId, String fileRef) {
        Assert.notNull(userId, "User must not be null");
        Assert.notNull(fileRef, "FileRef must not be null");
        this.userId = userId;
        this.fileRef = fileRef;
    }

    public String getId() {
        return this.id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFileRef() {
        return this.fileRef;
    }

}
