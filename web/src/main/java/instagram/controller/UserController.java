package instagram.controller;

import instagram.model.User;
import instagram.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity avatar(@PathVariable int id,
                                 @RequestParam("file") MultipartFile file) throws Exception {
        userService.setAvatar(id, file);
        return ResponseEntity.ok().build();
    }
}
