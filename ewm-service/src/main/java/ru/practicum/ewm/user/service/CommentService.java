package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.*;

import java.util.List;

public interface CommentService {

    CommentShortResponse addComment(long authorId, long eventId, CommentDto dto);

    ReplyCommentResponse replyToComment(long authorId, long commentId, CommentDto dto);

    CommentShortResponse updateComment(long commentId, CommentDto dto);

    void deleteOwnComment(long commentId);

    List<ReplyCommentResponse> getReplyComments(long userId, long commentId, String sort, int from, int size);

    CommentAdminResponse cancelComment(long commentId);

    List<CommentFullResponse> getCommentsByEventId(Long userId, Long eventId, String sort, int from, int size);
    }
