package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.CommentDTO;
import com.arian.vizpotifybackend.mapper.CommentMapper;
import com.arian.vizpotifybackend.model.Comment;
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
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

}
