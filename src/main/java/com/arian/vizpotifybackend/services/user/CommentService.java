package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.CommentDTO;
import com.arian.vizpotifybackend.model.Comment;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.repository.CommentRepository;
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

    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = Comment.builder()
                .userName(commentDTO.getUserName())
                .authorSpotifyId(commentDTO.getAuthorSpotifyId())
                .dashboardSpotifyId(commentDTO.getDashboardSpotifyId())
                .content(commentDTO.getContent())
                .createdAt(LocalDateTime.now())
                .likeCount(0)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return mapToDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByDashboardUserId(String dashboardUserId) {
        return commentRepository.findByDashboardSpotifyId(dashboardUserId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CommentDTO mapToDTO(Comment comment) {
        UserDetail userDetail = userService.loadUserDetailBySpotifyId(comment.getAuthorSpotifyId());
        String authorImageUrl = userDetail.getProfilePictureUrl();
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userName(comment.getUserName())
                .authorImageUrl(authorImageUrl)
                .authorSpotifyId(comment.getAuthorSpotifyId())
                .dashboardSpotifyId(comment.getDashboardSpotifyId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }
}
