package me.maartenmarx.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadResponse {
    private long id;
    private String title;
    private String content;
    private UserResponse user;
    private List<CommentDto> comments;
    private List<ReactionDto> reactions;
}
