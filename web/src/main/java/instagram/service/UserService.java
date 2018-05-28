package instagram.service;

import instagram.dao.UserDao;
import instagram.model.User;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    private MinioClient minioClient;
    private UserDao userDao;
    private String photoBucket;

    public UserService(UserDao userDao,
                       MinioClient minioClient,
                       @Value("${minio.photo-bucket}") String photoBucket) {
        this.userDao = userDao;
        this.minioClient = minioClient;
        this.photoBucket = photoBucket;
    }

    public User createUser(User user) {
        return userDao.createUser(user);
    }

    public void setAvatar(int id, MultipartFile file) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, XmlPullParserException {
        ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
        minioClient.putObject(photoBucket, file.getOriginalFilename(), bais, bais.available(), "application/octet-stream");
        userDao.setAvatarKey(id, file.getOriginalFilename());
    }

    public byte[] getAvatar(int userId) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, XmlPullParserException {
        String key = userDao.getAvatarKey(userId);
        return key != null ? IOUtils.toByteArray(minioClient.getObject(photoBucket, key)) : null;
    }
}
