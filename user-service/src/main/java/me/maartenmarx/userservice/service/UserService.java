package me.maartenmarx.userservice.service;

import me.maartenmarx.common.dto.*;
import lombok.RequiredArgsConstructor;
import me.maartenmarx.userservice.model.User;
import me.maartenmarx.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient webClient;
    private final UserRepository userRepository;

    @Value("${services.threads.baseurl}")
    private String threadServiceBaseUrl;

    public UsersResponse getAllUsers() {
        return new UsersResponse(
                userRepository.findAll().stream().map(u -> new UserResponse(
                        u.getId(),
                        u.getUsername()
                )).toList()
        );
    }

    public UserResponse getById(String id) {
        var user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    public UserResponse getByEmail(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    public ProfileResponse getProfileById(String id) {
        var user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var threads = webClient.get()
                .uri(threadServiceBaseUrl + "/api/threads/user/" + user.getId())
                .retrieve()
                .bodyToMono(ThreadsResponse.class)
                .blockOptional()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .getThreads();

        return ProfileResponse.builder()
                .username(user.getUsername())
                .achievements(user.getAchievements())
                .threads(threads)
                .build();
    }

    public String createUser(UserRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();

        return userRepository.save(user).getId();
    }
}
