package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.user.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.author " +
           "LEFT JOIN FETCH c.event " +
           "WHERE c.parentComment.id = :commentId AND c.status = PUBLISHED")
    Page<Comment> findReplyComments(long commentId, Pageable pageable);

    List<Comment> findByParentCommentId(long commentId);

    Page<Comment> findByEventId(long eventId, Pageable pageable);
}
