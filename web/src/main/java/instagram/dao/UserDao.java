package instagram.dao;

import instagram.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao {
    private final String CREATE_USER = "INSERT INTO users(username, password) VALUES (:username, :password)";
    private final String GET_USER_AVATAR_KEY = "select avatar_key from users where id = ?";
    private final String SET_USER_AVATAR_KEY = "UPDATE users SET avatar_key = :avatar_key WHERE id = :user_id";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public UserDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                   JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User createUser(User user) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("password", new BCryptPasswordEncoder().encode(user.getPassword()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(CREATE_USER, parameters, keyHolder, new String[] { "id" });
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    public String getAvatarKey(int userId) {
        try {
            return jdbcTemplate.queryForObject(GET_USER_AVATAR_KEY, new Object[]{userId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void setAvatarKey(int userId, String avatarKey) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("avatar_key", avatarKey);
        namedParameterJdbcTemplate.update(SET_USER_AVATAR_KEY, parameters);
    }

}
