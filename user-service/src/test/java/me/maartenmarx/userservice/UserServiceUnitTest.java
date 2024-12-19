package me.maartenmarx.userservice;

import me.maartenmarx.common.dto.ThreadResponse;
import me.maartenmarx.common.dto.ThreadsResponse;
import me.maartenmarx.common.dto.UserRequest;
import me.maartenmarx.userservice.model.User;
import me.maartenmarx.userservice.repository.UserRepository;
import me.maartenmarx.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

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
        ReflectionTestUtils.setField(userService, "threadServiceBaseUrl", "http://localhost:8081");
    }

    @Test
    public void testGetAllUsers() {
        var user1 = new User("1", "test@example.com", "test", new ArrayList<>());
        var user2 = new User("2", "admin@example.com", "admin", new ArrayList<>());

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        var result = userService.getAllUsers();

        assertEquals(2, result.getUsers().size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetById() {
        var user1 = new User("1", "test@example.com", "test", new ArrayList<>());

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));

        var result = userService.getById("1");

        assertEquals("1", result.getId());
        assertEquals("test", result.getUsername());

        verify(userRepository, times(1)).findById("1");
    }

    @Test
    public void testGetByEmail() {
        var user = new User("1", "test@example.com", "test", new ArrayList<>());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        var result = userService.getByEmail("test@example.com");

        assertEquals("1", result.getId());
        assertEquals("test", result.getUsername());

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testGetProfileById() {
        var user = new User("1", "test@example.com", "test", new ArrayList<>());

        var thread1 = ThreadResponse.builder()
                .id(1)
                .title("Thread 1")
                .content("Content of thread 1.")
                .build();
        var thread2 = ThreadResponse.builder()
                .id(2)
                .title("Thread 2")
                .content("Content of thread 2.")
                .build();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ThreadsResponse.class)).thenReturn(Mono.just(new ThreadsResponse(Arrays.asList(thread1, thread2))));

        var result = userService.getProfileById("1");

        assertEquals("test", result.getUsername());
        assertEquals(Arrays.asList(thread1, thread2), result.getThreads());

        verify(userRepository, times(1)).findById("1");
    }

    @Test
    public void testCreateUser() {
        var user = new User("1", "test@example.com", "test", new ArrayList<>());
        var userRequest = new UserRequest(user.getEmail(), user.getUsername());

        when(userRepository.save(any(User.class))).thenReturn(user);

        var result = userService.createUser(userRequest);

        assertEquals("1", result);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
