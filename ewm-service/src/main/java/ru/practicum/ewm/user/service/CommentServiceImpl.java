package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.*;
import ru.practicum.ewm.user.mapper.CommentMapper;
import ru.practicum.ewm.user.model.Comment;
import ru.practicum.ewm.user.model.CommentStatus;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.CommentRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper mapper;

    @Override
    @Transactional
    public CommentShortResponse addComment(long authorId, long eventId, CommentDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", authorId)));
        Comment comment = new Comment();
        comment.setComment(dto.getComment());
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setCreatedOn(LocalDateTime.now());
        comment.setStatus(CommentStatus.PUBLISHED);

        comment = commentRepository.save(comment);
        return mapper.mapToCommentShortResponse(comment);
    }

    @Override
    @Transactional
    public ReplyCommentResponse replyToComment(long authorId, long commentId, CommentDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментария с таким id - %d не существует.", commentId)));
        User replicator = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", authorId)));
        Comment reply = new Comment();
        reply.setComment(dto.getComment());
        reply.setEvent(comment.getEvent());
        reply.setAuthor(replicator);
        reply.setCreatedOn(LocalDateTime.now());
        reply.setStatus(CommentStatus.PUBLISHED);
        reply.setParentComment(comment);

        reply = commentRepository.save(reply);
        return mapper.mapToReplyCommentResponse(reply);
    }

    @Override
    @Transactional
    public CommentShortResponse updateComment(long commentId, CommentDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментария с таким id - %d не существует.", commentId)));
        comment.setComment(dto.getComment());
        return mapper.mapToCommentShortResponse(comment);
    }

    @Override
    @Transactional
    public void deleteOwnComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<ReplyCommentResponse> getReplyComments(long userId, long commentId, String sort, int from, int size) {
        Pageable page = PageRequest.of(0, 10, Sort.by("createdOn").ascending());

        if (sort.equals("DESC")) {
            page = PageRequest.of(0, 10, Sort.by("createdOn").descending());
        }

        List<Comment> replies = commentRepository.findReplyComments(commentId, page).getContent();
        return mapper.mapToReplyCommentResponseList(replies);
    }

    @Override
    @Transactional
    public CommentAdminResponse cancelComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментария с таким id - %d не существует.", commentId)));
        comment.setStatus(CommentStatus.CANCELED);

        List<Comment> replies = commentRepository.findByParentCommentId(commentId);
        replies.forEach(reply -> reply.setStatus(CommentStatus.CANCELED));

        return mapper.mapToCommentAdminResponse(comment);
    }

    @Override
    public List<CommentFullResponse> getCommentsByEventId(Long userId, Long eventId, String sort, int from, int size) {
        Pageable page = PageRequest.of(0, 10, Sort.by("createdOn").ascending());

        if (sort.equals("DESC")) {
            page = PageRequest.of(0, 10, Sort.by("createdOn").descending());
        }

        List<Comment> comments = commentRepository.findByEventId(eventId, page).getContent();

        List<Comment> parentComments = comments.stream()
                .filter(comment -> comment.getParentComment() == null)
                .toList();

        return parentComments.stream()
                .map(parentComment -> {
                    CommentFullResponse fullResponse = mapper.mapToCommentFullResponse(parentComment);

                    List<CommentShortResponse> replies = comments.stream()
                            .filter(comment -> comment.getParentComment() != null &&
                                               comment.getParentComment().getId().equals(parentComment.getId()))
                            .sorted(Comparator.comparing(Comment::getCreatedOn,
                                    sort.equals("DESC") ? Comparator.reverseOrder() : Comparator.naturalOrder()))
                            .map(mapper::mapToCommentShortResponse)
                            .collect(Collectors.toList());

                    fullResponse.setReplyComments(replies);
                    return fullResponse;
                })
                .collect(Collectors.toList());
    }
}
