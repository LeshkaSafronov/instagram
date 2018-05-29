package instagram.service;

import instagram.dao.PhotoDao;
import instagram.model.Photo;
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
public class PhotoService {

    private PhotoDao photoDao;
    private MinioClient minioClient;
    private String photoBucket;

    public PhotoService(PhotoDao photoDao,
                        MinioClient minioClient,
                        @Value("${minio.photo-bucket}") String photoBucket) {
        this.photoDao = photoDao;
        this.minioClient = minioClient;
        this.photoBucket = photoBucket;
    }

    public Photo createPhoto(Photo photo) {
        return photoDao.createPhoto(photo);
    }

    public Photo getPhoto(int id) {
        return photoDao.getPhoto(id);
    }

    public byte[] getPhotoImage(int photoId) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, XmlPullParserException {
        String key = photoDao.getImageKey(photoId);
        return key != null ? IOUtils.toByteArray(minioClient.getObject(photoBucket, key)) : null;
    }

    public void setPhotoImage(int id, MultipartFile file) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, XmlPullParserException {
        ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
        minioClient.putObject(photoBucket, file.getOriginalFilename(), bais, bais.available(), "application/octet-stream");
        photoDao.setImageKey(id, file.getOriginalFilename());
    }

    public void likePhoto(int id) {
        photoDao.likePhoto(id);
    }



}
