package com.arian.vizpotifybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long commentId;
    private String authorImageUrl;
    private String userName;
    private String authorSpotifyId;
    private String dashboardSpotifyId;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
}
