package me.maartenmarx.threadservice.service;

import me.maartenmarx.common.dto.*;
import lombok.RequiredArgsConstructor;
import me.maartenmarx.threadservice.model.Thread;
import me.maartenmarx.threadservice.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import me.maartenmarx.common.service.JwtService;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final WebClient webClient;

    @Value("${services.users.baseurl}")
    private String userServiceBaseUrl;
    @Value("${services.interactions.baseurl}")
    private String interactionServiceBaseUrl;

    public ThreadsResponse getAllThreads() {
        var threads = threadRepository.findAll().stream().map(t -> {
            var userResponse = webClient.get()
                    .uri(userServiceBaseUrl + "/api/users/" + t.getUserId())
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .blockOptional()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

            return new ThreadResponse(
                    t.getId(),
                    t.getTitle(),
                    t.getContent(),
                    userResponse,
                    null
            );
        }).toList();

        return new ThreadsResponse(threads);
    }

    public ThreadResponse getById(Long id) {
        var thread = threadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var commentResponse = webClient.get()
                .uri( interactionServiceBaseUrl + "/api/comments/" + thread.getId())
                .retrieve()
                .bodyToMono(CommentsResponse.class)
                .blockOptional();

        var userResponse = webClient.get()
                .uri(userServiceBaseUrl + "/api/users/" + thread.getUserId())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .blockOptional()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        return new ThreadResponse(
                thread.getId(),
                thread.getTitle(),
                thread.getContent(),
                userResponse,
                commentResponse.map(CommentsResponse::getComments).orElseGet(ArrayList::new)
        );
    }

    public ThreadsResponse getByUser(String id) {
        var threads = threadRepository.findByUserId(id).stream().map(t ->
                ThreadResponse.builder()
                        .title(t.getTitle())
                        .content(t.getContent())
                        .build()
        ).toList();

        return new ThreadsResponse(threads);
    }

    public void createThread(ThreadRequest request, JwtService.UserData userData) {
        var user = getOrCreateUser(userData);

        var thread = Thread.builder()
                .userId(user.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        threadRepository.save(thread);
    }

    public void updateThread(Long threadId, ThreadRequest request, JwtService.UserData userData) {
        var user = getOrCreateUser(userData);

        var thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!thread.getUserId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        thread.setTitle(request.getTitle());
        thread.setContent(request.getContent());

        threadRepository.save(thread);
    }

    public void deleteThread(Long threadId, JwtService.UserData userData) {
        var user = getOrCreateUser(userData);
        var thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!thread.getUserId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        threadRepository.delete(thread);
    }

    private UserResponse getOrCreateUser(JwtService.UserData userData) {
        var user = webClient.get()
                .uri(userServiceBaseUrl + "/api/users/email/" + userData.getEmail())
                .retrieve()
                .onStatus(
                        s -> s.equals(HttpStatus.NOT_FOUND),
                        res -> Mono.empty()
                )
                .bodyToMono(UserResponse.class)
                .block();

        if (user != null && user.getId() != null) {
            return user;
        } else {
            var req = UserRequest.builder()
                    .email(userData.getEmail())
                    .username(userData.getName())
                    .build();

            return webClient.post()
                    .uri(userServiceBaseUrl + "/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(req), UserRequest.class)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();
        }
    }
}