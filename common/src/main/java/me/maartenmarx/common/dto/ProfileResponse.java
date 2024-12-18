package me.maartenmarx.common.dto;

import me.maartenmarx.common.data.Achievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String username;
    private Iterable<Achievement> achievements;
    private Iterable<ThreadResponse> threads;
}
