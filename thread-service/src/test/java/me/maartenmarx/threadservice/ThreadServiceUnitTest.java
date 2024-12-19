package me.maartenmarx.threadservice;

import me.maartenmarx.common.dto.*;
import me.maartenmarx.common.service.JwtService;
import me.maartenmarx.threadservice.model.Thread;
import me.maartenmarx.threadservice.repository.ThreadRepository;
import me.maartenmarx.threadservice.service.ThreadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThreadServiceUnitTest {
    @InjectMocks
    private ThreadService threadService;

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(threadService, "userServiceBaseUrl", "http://localhost:8083");
        ReflectionTestUtils.setField(threadService, "interactionServiceBaseUrl", "http://localhost:8082");
    }

    @Test
    public void testGetAllThreads() {
        var thread1 = new Thread(1L, "Thread 1", "Content of thread 1.", "1");
        var thread2 = new Thread(2L, "Thread 2", "Content of thread 2.", "1");

        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(threadRepository.findAll()).thenReturn(Arrays.asList(thread1, thread2));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = threadService.getAllThreads();

        assertEquals(2, result.getThreads().size());

        verify(threadRepository, times(1)).findAll();
    }

    @Test
    public void testGetById() {
        var thread = new Thread(1L, "Thread 1", "Content of thread 1.", "1");

        var user1 = new UserResponse("1", "User 1");
        var user2 = new UserResponse("1", "User 1");

        var comment1 = CommentDto.builder()
                .user(user1)
                .content("Content of comment 1.")
                .build();
        var comment2 = CommentDto.builder()
                .user(user2)
                .content("Content of thread 2.")
                .build();

        var reaction1 = ReactionDto.builder()
                .user(user1)
                .emoji("❤️️")
                .build();
        var reaction2 = ReactionDto.builder()
                .user(user2)
                .emoji("🎉")
                .build();

        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(threadRepository.findById(1L)).thenReturn(Optional.of(thread));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CommentsResponse.class)).thenReturn(Mono.just(new CommentsResponse(Arrays.asList(comment1, comment2))));
        when(responseSpec.bodyToMono(ReactionsResponse.class)).thenReturn(Mono.just(new ReactionsResponse(Arrays.asList(reaction1, reaction2))));
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = threadService.getById(1L);

        assertEquals("Thread 1", result.getTitle());
        assertEquals("Content of thread 1.", result.getContent());
        assertEquals(Arrays.asList(comment1, comment2), result.getComments());
        assertEquals(Arrays.asList(reaction1, reaction2), result.getReactions());
        assertEquals(userResponse, result.getUser());

        verify(threadRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetByUser() {
        var thread1 = new Thread(1L, "Thread 1", "Content of thread 1.", "1");
        var thread2 = new Thread(2L, "Thread 2", "Content of thread 2.", "1");

        when(threadRepository.findByUserId("1")).thenReturn(Arrays.asList(thread1, thread2));

        var result = threadService.getByUser("1");

        assertEquals(2, result.getThreads().size());

        verify(threadRepository, times(1)).findByUserId("1");
    }

    @Test
    public void testCreateThread() {
        var thread = new Thread(1L, "Thread 1", "Content of thread 1.", "1");
        var threadRequest = new ThreadRequest("Thread 1", "Content of thread 1.");

        var user = new JwtService.UserData("new.user@example.com", "New User");
        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(threadRepository.save(any(Thread.class))).thenReturn(thread);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = threadService.createThread(threadRequest, user);

        assertEquals(1L, result);

        verify(threadRepository, times(1)).save(any(Thread.class));
    }

    @Test
    public void testUpdateThread() {
        var thread = new Thread(1L, "Thread 1", "Modified content.", "1");
        var threadRequest = new ThreadRequest("Thread 1", "Modified content.");

        var user = new JwtService.UserData("new.user@example.com", "New User");
        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(threadRepository.findById(1L)).thenReturn(Optional.of(thread));
        when(threadRepository.save(any(Thread.class))).thenReturn(thread);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = threadService.updateThread(1L, threadRequest, user);

        assertTrue(result);

        verify(threadRepository, times(1)).save(any(Thread.class));
    }

    @Test
    public void testDeleteThread() {
        var thread = new Thread(1L, "Thread 1", "Content of thread 1.", "1");

        var user = new JwtService.UserData("new.user@example.com", "New User");
        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(threadRepository.findById(1L)).thenReturn(Optional.of(thread));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = threadService.deleteThread(1L, user);

        assertTrue(result);

        verify(threadRepository, times(1)).delete(any(Thread.class));
    }
}
