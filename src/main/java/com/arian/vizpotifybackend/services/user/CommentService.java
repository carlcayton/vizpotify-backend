package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.CommentDTO;
import com.arian.vizpotifybackend.mapper.CommentMapper;
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
    private final CommentMapper commentMapper;
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

        return commentMapper.toDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByDashboardUserId(String dashboardUserId) {
        return commentRepository.findByDashboardSpotifyId(dashboardUserId)
                .stream()
                .map(this::convertAndEnrichComment)
                .collect(Collectors.toList());
    }

    protected CommentDTO convertAndEnrichComment(Comment comment) {
        CommentDTO dto = commentMapper.toDTO(comment);
        enrichDTOWithAuthorImage(comment, dto);
        return dto;
    }

    private void enrichDTOWithAuthorImage(Comment comment, CommentDTO dto) {
        UserDetail userDetail = userService.loadUserDetailBySpotifyId(comment.getAuthorSpotifyId());
        if (userDetail != null) {
            dto.setAuthorImageUrl(userDetail.getProfilePictureUrl());
        }
    }

}
