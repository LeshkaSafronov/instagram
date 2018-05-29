package instagram.dao;

import instagram.model.Photo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@Repository
public class PhotoDao {
    private final String CREATE_PHOTO = "INSERT INTO photos(user_id, text) VALUES (:user_id, :text)";
    private final String GET_PHOTO_BY_ID = "select id, user_id, created_at, likes, text, is_ready, key, url from photos where id = ?";
    private final String GET_PHOTO_IMAGE_KEY = "select key from photos where id = ?";
    private final String SET_PHOTO_IMAGE_KEY = "UPDATE photos SET key = :key, url = :url WHERE id = :photo_id";
    private final String LIKE_PHOTO = "UPDATE photos SET likes = likes + 1 WHERE id = :photo_id";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public PhotoDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                    JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Photo createPhoto(Photo photo) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", photo.getUserId())
                .addValue("text", photo.getText());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(CREATE_PHOTO, parameters, keyHolder, new String[] { "id" });
        photo.setId(keyHolder.getKey().intValue());
        return photo;
    }

    public Photo getPhoto(int id) {
        Object[] params = new Object[]{ id };
        try {
            return jdbcTemplate.queryForObject(GET_PHOTO_BY_ID, params, new PhotoDao.PhotoMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getImageKey(int photoId) {
        try {
            return jdbcTemplate.queryForObject(GET_PHOTO_IMAGE_KEY, new Object[]{photoId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void setImageKey(int photoId, String key) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("photo_id", photoId)
                .addValue("key", key)
                .addValue("url", String.format("/api/v1/photos/%d/image", photoId));
        namedParameterJdbcTemplate.update(SET_PHOTO_IMAGE_KEY, parameters);
    }

    private class PhotoMapper implements RowMapper<Photo> {

        @Override
        public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            int userId = rs.getInt("user_id");
            Timestamp createdAt = rs.getTimestamp("created_at");
            int likes = rs.getInt("likes");
            String text = rs.getString("text");
            boolean isReady = rs.getBoolean("is_ready");
            String key = rs.getString("key");
            String url = rs.getString("url");
            return new Photo(id, userId, createdAt.toInstant(), likes, text, isReady, key, url);
        }
    }

    public void likePhoto(int id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("photo_id", id);
        namedParameterJdbcTemplate.update(LIKE_PHOTO, parameters);
    }
}
