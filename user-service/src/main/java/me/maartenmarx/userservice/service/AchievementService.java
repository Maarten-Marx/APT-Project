package me.maartenmarx.userservice.service;

import lombok.RequiredArgsConstructor;
import me.maartenmarx.common.data.Achievement;
import me.maartenmarx.common.dto.CommentsResponse;
import me.maartenmarx.common.dto.ReactionsResponse;
import me.maartenmarx.common.dto.ThreadsResponse;
import me.maartenmarx.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final WebClient webClient;
    private final UserRepository userRepository;

    @Value("${services.threads.baseurl}")
    private String threadServiceBaseUrl;
    @Value("${services.interactions.baseurl}")
    private String interactionServiceBaseUrl;

    private final Map<Integer, Achievement> threadAchievements = Map.of(
            1, new Achievement("Poster", "Start your first thread."),
            10, new Achievement("Forum Resident", "Start 10 threads."),
            100, new Achievement("Forum Celebrity", "Start 100 threads.")
    );

    private void checkThreads(String userId) {
        var threads = webClient.get()
                .uri("http://" + threadServiceBaseUrl + "/api/threads/user/" + userId)
                .retrieve()
                .bodyToMono(ThreadsResponse.class)
                .blockOptional()
                .orElse(new ThreadsResponse())
                .getThreads();

        var achievement = threadAchievements.getOrDefault(threads.size(), null);

        if (achievement != null) {
            addAchievement(achievement, userId);
        }
    }

    private final Map<Integer, Achievement> commentAchievements = Map.of(
            1, new Achievement("Commenter", "Place your first comment."),
            10, new Achievement("Correspondant", "Place 10 comments."),
            100, new Achievement("Responsive", "Place 100 comments.")
    );

    private void checkComments(String userId) {
        var comments = webClient.get()
                .uri("http://" + interactionServiceBaseUrl + "/api/comments/user/" + userId)
                .retrieve()
                .bodyToMono(CommentsResponse.class)
                .blockOptional()
                .orElse(new CommentsResponse())
                .getComments();

        var achievement = commentAchievements.getOrDefault(comments.size(), null);

        if (achievement != null) {
            addAchievement(achievement, userId);
        }
    }

    private final Map<Integer, Achievement> reactionAchievements = Map.of(
            1, new Achievement("Reactor", "Add your first reaction."),
            10, new Achievement("Catalyst", "Add a reaction to 10 threads."),
            100, new Achievement("Highly Reactive", "Add a reaction to 100 threads.")
    );

    private void checkReactions(String userId) {
        var reactions = webClient.get()
                .uri("http://" + interactionServiceBaseUrl + "/api/reactions/user/" + userId)
                .retrieve()
                .bodyToMono(ReactionsResponse.class)
                .blockOptional()
                .orElse(new ReactionsResponse())
                .getReactions();

        var achievement = reactionAchievements.getOrDefault(reactions.size(), null);

        if (achievement != null) {
            addAchievement(achievement, userId);
        }
    }

    private void addAchievement(Achievement achievement, String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        var achievements = user.getAchievements();
        if (achievements == null) achievements = new ArrayList<>();
        achievements.add(achievement);

        user.setAchievements(achievements);

        userRepository.save(user);
    }

    @KafkaListener(topics = "thread", groupId = "listeners")
    public void listenToThreads(String message) {
        checkThreads(message);
    }

    @KafkaListener(topics = "comment", groupId = "listeners")
    public void listenToComments(String message) {
        checkComments(message);
    }

    @KafkaListener(topics = "reaction", groupId = "listeners")
    public void listenToReactions(String message) {
        checkReactions(message);
    }
}
