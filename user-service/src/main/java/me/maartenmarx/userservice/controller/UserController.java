package me.maartenmarx.userservice.controller;

import dto.ProfileResponse;
import dto.UserRequest;
import dto.UserResponse;
import dto.UsersResponse;
import lombok.RequiredArgsConstructor;
import me.maartenmarx.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UsersResponse getAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponse createUser(@RequestBody UserRequest request) {
        var userId = userService.createUser(request);

        return userService.getById(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable String id) {
        return userService.getById(id);
    }

    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @GetMapping("/{id}/profile")
    @ResponseStatus(HttpStatus.OK)
    public ProfileResponse getProfile(@PathVariable String id) {
        return userService.getProfileById(id);
    }
}
