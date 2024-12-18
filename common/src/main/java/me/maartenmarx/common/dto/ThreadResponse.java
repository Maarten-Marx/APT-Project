package me.maartenmarx.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadResponse {
    private long id;
    private String title;
    private String content;
    private UserResponse user;
    private Iterable<CommentDto> comments;
}