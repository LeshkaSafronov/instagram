package instagram.dao;

import instagram.model.User;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private final String CREATE_USER = "INSERT INTO users(username, password) VALUES (:username, :password)";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public User createUser(User user) {
        System.out.println(user);
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("password", new BCryptPasswordEncoder().encode(user.getPassword()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(CREATE_USER, parameters, keyHolder, new String[] { "id" });
        user.setId(keyHolder.getKey().intValue());
        return user;
    }
}
