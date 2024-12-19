package me.maartenmarx.userservice.model;

import me.maartenmarx.common.data.Achievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(value = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    private String username;
    private List<Achievement> achievements;
}
