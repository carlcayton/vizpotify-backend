package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.CommentDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.user.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
// inject a property value instead for the origin $frontendURl


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    @PostMapping("/{userId}/comments")
    public ResponseEntity<CommentDTO> postComment(@PathVariable String userId,
                                                  @RequestBody CommentDTO commentDTO,
                                                  Authentication authentication) {

        CommentDTO createdComment = commentService.createComment(commentDTO, (UserDetail) authentication.getPrincipal());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
    @GetMapping("/{userId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable String userId) {
        List<CommentDTO> comments = commentService.getCommentsByDashboardUserId(userId);
        return ResponseEntity.ok(comments);
    }
}
