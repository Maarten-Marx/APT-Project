package me.maartenmarx.interactionservice;

import me.maartenmarx.common.dto.CommentRequest;
import me.maartenmarx.common.dto.ReactionRequest;
import me.maartenmarx.common.dto.UserResponse;
import me.maartenmarx.common.service.JwtService;
import me.maartenmarx.interactionservice.model.Comment;
import me.maartenmarx.interactionservice.model.Reaction;
import me.maartenmarx.interactionservice.repository.CommentRepository;
import me.maartenmarx.interactionservice.repository.ReactionRepository;
import me.maartenmarx.interactionservice.service.CommentService;
import me.maartenmarx.interactionservice.service.InteractionService;
import me.maartenmarx.interactionservice.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InteractionServiceUnitTest {
    @InjectMocks
    private InteractionService interactionService;

    @InjectMocks
    private CommentService commentService;

    @InjectMocks
    private ReactionService reactionService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReactionRepository reactionRepository;

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
        ReflectionTestUtils.setField(interactionService, "userServiceBaseUrl", "http://localhost:8083");
    }

    @Test
    public void testGetCommentsByThread() {
        var comment1 = new Comment(1L, "Content of comment 1.", 1L, "1");
        var comment2 = new Comment(2L, "Content of comment 2.", 1L, "2");

        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(commentRepository.findByThreadId(1L)).thenReturn(Arrays.asList(comment1, comment2));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = commentService.getByThread(1L);

        assertEquals(2, result.getComments().size());

        verify(commentRepository, times(1)).findByThreadId(anyLong());
    }

    @Test
    public void testCreateComment() {
        var comment = new Comment(1L, "Content of comment 1.", 1L, "1");
        var commentRequest = new CommentRequest(1L, "Content of comment 1.");

        var user = new JwtService.UserData("new.user@example.com", "New User");
        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = commentService.createComment(commentRequest, user);

        assertEquals(1L, result);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testGetReactionsByThread() {
        var reaction1 = new Reaction(1L, "❤️", 1L, "1");
        var reaction2 = new Reaction(2L, "🎉", 1L, "2");

        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(reactionRepository.findByThreadId(1L)).thenReturn(Arrays.asList(reaction1, reaction2));

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = reactionService.getByThread(1L);

        assertEquals(2, result.getReactions().size());

        verify(reactionRepository, times(1)).findByThreadId(anyLong());
    }

    @Test
    public void testCreateReaction() {
        var reaction = new Reaction(1L, "❤️", 1L, "1");
        var reactionRequest = new ReactionRequest(1L, "❤️");

        var user = new JwtService.UserData("new.user@example.com", "New User");
        var userResponse = UserResponse.builder()
                .id("1")
                .username("User 1")
                .build();

        when(reactionRepository.save(any(Reaction.class))).thenReturn(reaction);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));

        var result = reactionService.createReaction(reactionRequest, user);

        assertEquals(1L, result);

        verify(reactionRepository, times(1)).save(any(Reaction.class));
    }
}
