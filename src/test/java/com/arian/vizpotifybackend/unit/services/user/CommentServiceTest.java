package com.arian.vizpotifybackend.unit.services.user;

import com.arian.vizpotifybackend.comment.Comment;
import com.arian.vizpotifybackend.comment.CommentDto;
import com.arian.vizpotifybackend.comment.CommentRepository;
import com.arian.vizpotifybackend.comment.CommentService;
import com.arian.vizpotifybackend.common.mapper.CommentMapper;
import com.arian.vizpotifybackend.user.core.UserDetail;
import com.arian.vizpotifybackend.user.core.UserService;
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
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userDetail = UserDetail.builder()
                .spotifyId("user123")
                .displayName("John Doe")
                .profilePictureUrl("profile_url")
                .build();

        commentDto = CommentDto.builder()
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
    void createComment_shouldSaveAndReturnCommentDto() {
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.createComment(commentDto, userDetail);

        verify(commentRepository).save(commentCaptor.capture());
        Comment capturedComment = commentCaptor.getValue();
        assertEquals(userDetail.getDisplayName(), capturedComment.getUserName());
        assertEquals(userDetail.getSpotifyId(), capturedComment.getAuthorSpotifyId());
        assertEquals(commentDto.getDashboardSpotifyId(), capturedComment.getDashboardSpotifyId());
        assertEquals(commentDto.getContent(), capturedComment.getContent());
        assertEquals(0, capturedComment.getLikeCount());

        assertEquals(commentDto, result);
    }

    @Test
    void getCommentsByDashboardUserId_shouldReturnListOfCommentDtos() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        when(commentRepository.findByDashboardSpotifyId("dashboard123")).thenReturn(comments);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(userService.loadUserDetailBySpotifyId("user123")).thenReturn(userDetail);

        List<CommentDto> result = commentService.getCommentsByDashboardUserId("dashboard123");

        verify(commentRepository).findByDashboardSpotifyId("dashboard123");
        verify(commentMapper).toDto(comment);
        verify(userService).loadUserDetailBySpotifyId("user123");

        assertEquals(1, result.size());
        assertEquals(commentDto, result.get(0));
        assertEquals(userDetail.getProfilePictureUrl(), result.get(0).getAuthorImageUrl());
    }

    @Test
    void convertAndEnrichComment_shouldConvertAndEnrichCommentDto() {
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(userService.loadUserDetailBySpotifyId("user123")).thenReturn(userDetail);

        CommentDto result = commentService.convertAndEnrichComment(comment);

        verify(commentMapper).toDto(comment);
        verify(userService).loadUserDetailBySpotifyId("user123");

        assertEquals(commentDto, result);
        assertEquals(userDetail.getProfilePictureUrl(), result.getAuthorImageUrl());
    }
}