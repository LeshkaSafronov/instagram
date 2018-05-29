package instagram.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Photo {
    private Integer id;
    private Integer userId;
    private Instant createdAt;
    private Integer likes;
    private String text;
    private Boolean isReady;
    private String key;
    private String url;

    public Photo() {}

    public Photo(Integer id,
                 Integer userId,
                 Instant createdAt,
                 Integer likes,
                 String text,
                 Boolean isReady,
                 String key,
                 String url) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.likes = likes;
        this.text = text;
        this.isReady = isReady;
        this.key = key;
        this.url = url;
    }
}
