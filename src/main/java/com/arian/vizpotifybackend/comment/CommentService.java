package com.arian.vizpotifybackend.comment;

import com.arian.vizpotifybackend.common.mapper.CommentMapper;
import com.arian.vizpotifybackend.user.core.UserDetail;
import com.arian.vizpotifybackend.user.core.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CommentDto commentDto, UserDetail userDetail) {
        Comment comment = Comment.builder()
                .userName(userDetail.getDisplayName())
                .authorSpotifyId(userDetail.getSpotifyId())
                .dashboardSpotifyId(commentDto.getDashboardSpotifyId())
                .content(commentDto.getContent())
                .createdAt(LocalDateTime.now())
                .likeCount(0)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toDto(savedComment);
    }

    public List<CommentDto> getCommentsByDashboardUserId(String dashboardUserId) {
        return commentRepository.findByDashboardSpotifyId(dashboardUserId)
                .stream()
                .map(this::convertAndEnrichComment)
                .collect(Collectors.toList());
    }

    public CommentDto convertAndEnrichComment(Comment comment) {
        CommentDto dto = commentMapper.toDto(comment);
        enrichDtoWithAuthorImage(comment, dto);
        return dto;
    }

    private void enrichDtoWithAuthorImage(Comment comment, CommentDto dto) {
        UserDetail userDetail = userService.loadUserDetailBySpotifyId(comment.getAuthorSpotifyId());
        if (userDetail != null) {
            dto.setAuthorImageUrl(userDetail.getProfilePictureUrl());
        }
    }

}
