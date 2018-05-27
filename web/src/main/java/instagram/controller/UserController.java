package instagram.controller;

import instagram.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping("/register")
    public String register(UserDto userDto) {
        return "users/register";
    }

    @PostMapping("/register")
    public void registerUser(UserDto userDto) {
        System.out.println(userDto);
    }

    @GetMapping("/login")
    public String login(UserDto userDto) {
        return "users/login";
    }
}
