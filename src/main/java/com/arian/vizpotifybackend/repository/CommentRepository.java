package com.arian.vizpotifybackend.repository;

import com.arian.vizpotifybackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDashboardSpotifyId(String dashboardSpotifyId);

}
