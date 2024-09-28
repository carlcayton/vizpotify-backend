package com.arian.vizpotifybackend.comment;

import com.arian.vizpotifybackend.user.core.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;
    @PostMapping("/{userId}/comments")
    public ResponseEntity<CommentDto> postComment(@PathVariable String userId,
                                                  @RequestBody CommentDto commentDto,
                                                  Authentication authentication) {

        CommentDto createdComment = commentService.createComment(commentDto, (UserDetail) authentication.getPrincipal());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable String userId) {
        List<CommentDto> comments = commentService.getCommentsByDashboardUserId(userId);
        return ResponseEntity.ok(comments);
    }
}
