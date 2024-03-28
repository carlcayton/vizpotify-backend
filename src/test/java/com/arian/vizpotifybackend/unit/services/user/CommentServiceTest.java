package com.arian.vizpotifybackend.unit.services.user;

import com.arian.vizpotifybackend.dto.CommentDTO;
import com.arian.vizpotifybackend.mapper.CommentMapper;
import com.arian.vizpotifybackend.model.Comment;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.repository.CommentRepository;
import com.arian.vizpotifybackend.services.user.CommentService;
import com.arian.vizpotifybackend.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private UserDetail userDetail;
    private CommentDTO commentDTO;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userDetail = UserDetail.builder()
                .spotifyId("user123")
                .displayName("John Doe")
                .profilePictureUrl("profile_url")
                .build();

        commentDTO = CommentDTO.builder()
                .dashboardSpotifyId("dashboard123")
                .content("Test comment")
                .build();

        comment = Comment.builder()
                .commentId(1L)
                .userName("John Doe")
                .authorSpotifyId("user123")
                .dashboardSpotifyId("dashboard123")
                .content("Test comment")
                .createdAt(LocalDateTime.now())
                .likeCount(0)
                .build();
    }

    @Test
    void createComment_shouldSaveAndReturnCommentDTO() {
        when(commentMapper.toDTO(any(Comment.class))).thenReturn(commentDTO);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO result = commentService.createComment(commentDTO, userDetail);

        verify(commentRepository).save(commentCaptor.capture());
        Comment capturedComment = commentCaptor.getValue();
        assertEquals(userDetail.getDisplayName(), capturedComment.getUserName());
        assertEquals(userDetail.getSpotifyId(), capturedComment.getAuthorSpotifyId());
        assertEquals(commentDTO.getDashboardSpotifyId(), capturedComment.getDashboardSpotifyId());
        assertEquals(commentDTO.getContent(), capturedComment.getContent());
        assertEquals(0, capturedComment.getLikeCount());

        assertEquals(commentDTO, result);
    }

    @Test
    void getCommentsByDashboardUserId_shouldReturnListOfCommentDTOs() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        when(commentRepository.findByDashboardSpotifyId("dashboard123")).thenReturn(comments);
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);
        when(userService.loadUserDetailBySpotifyId("user123")).thenReturn(userDetail);

        List<CommentDTO> result = commentService.getCommentsByDashboardUserId("dashboard123");

        verify(commentRepository).findByDashboardSpotifyId("dashboard123");
        verify(commentMapper).toDTO(comment);
        verify(userService).loadUserDetailBySpotifyId("user123");

        assertEquals(1, result.size());
        assertEquals(commentDTO, result.get(0));
        assertEquals(userDetail.getProfilePictureUrl(), result.get(0).getAuthorImageUrl());
    }

    @Test
    void convertAndEnrichComment_shouldConvertAndEnrichCommentDTO() {
        when(commentMapper.toDTO(comment)).thenReturn(commentDTO);
        when(userService.loadUserDetailBySpotifyId("user123")).thenReturn(userDetail);

        CommentDTO result = commentService.convertAndEnrichComment(comment);

        verify(commentMapper).toDTO(comment);
        verify(userService).loadUserDetailBySpotifyId("user123");

        assertEquals(commentDTO, result);
        assertEquals(userDetail.getProfilePictureUrl(), result.getAuthorImageUrl());
    }
}