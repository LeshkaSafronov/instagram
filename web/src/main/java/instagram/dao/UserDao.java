package instagram.dao;

import instagram.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {
    private final String CREATE_USER = "INSERT INTO users(username, password) VALUES (:username, :password)";
    private final String GET_USERS = "select id, username, password, avatar_key, url from users";
    private final String GET_USER_BY_ID = "select id, username, password, avatar_key, url from users where id = ?";
    private final String GET_USER_AVATAR_KEY = "select avatar_key from users where id = ?";
    private final String SET_USER_AVATAR_KEY = "UPDATE users SET avatar_key = :avatar_key, url = :url WHERE id = :user_id";

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
                .addValue("avatar_key", avatarKey)
                .addValue("url", String.format("/api/v1/users/%d/avatar", userId));
        namedParameterJdbcTemplate.update(SET_USER_AVATAR_KEY, parameters);
    }


    public List<User> getUsers() {
        try {
            return jdbcTemplate.query(GET_USERS, new UserMapper());
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }


    public User getUser(int id) {
        Object[] params = new Object[]{ id };
        try {
            return jdbcTemplate.queryForObject(GET_USER_BY_ID, params, new UserMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String avatarKey = rs.getString("avatar_key");
            String url = rs.getString("url");
            return new User(id, username, null, url, avatarKey);
        }
    }
}
