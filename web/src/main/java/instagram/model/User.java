package instagram.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;

    private String url;

    private String avatar_key;

    public User() {}

    public User(Integer id, String username, String password, String url, String avatar_key) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.url = url;
        this.avatar_key = avatar_key;
    }
}
