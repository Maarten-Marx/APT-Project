package me.maartenmarx.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionsResponse {
    private Iterable<ReactionDto> reactions;
}
