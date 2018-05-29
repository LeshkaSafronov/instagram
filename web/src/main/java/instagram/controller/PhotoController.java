package instagram.controller;

import instagram.model.Photo;
import instagram.service.PhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {
    private PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("")
    public Photo createPhoto(@RequestBody Photo photo) {
        return photoService.createPhoto(photo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhoto(@PathVariable int id) {
        Photo photo = photoService.getPhoto(id);
        if (photo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(photo);
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable int id) throws Exception {
        byte[] body = photoService.getPhotoImage(id);
        if (body == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity setImage(@PathVariable int id,
                                   @RequestParam("file") MultipartFile file) throws Exception {
        photoService.setPhotoImage(id, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity likePhoto(@PathVariable int id) {
        photoService.likePhoto(id);
        return ResponseEntity.ok().build();
    }
}
