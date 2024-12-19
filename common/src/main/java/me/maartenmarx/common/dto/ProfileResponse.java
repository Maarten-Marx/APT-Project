package me.maartenmarx.common.dto;

import me.maartenmarx.common.data.Achievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String username;
    private List<Achievement> achievements;
    private List<ThreadResponse> threads;
}
