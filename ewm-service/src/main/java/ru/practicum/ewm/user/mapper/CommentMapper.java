package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.user.dto.CommentAdminResponse;
import ru.practicum.ewm.user.dto.CommentFullResponse;
import ru.practicum.ewm.user.dto.CommentShortResponse;
import ru.practicum.ewm.user.dto.ReplyCommentResponse;
import ru.practicum.ewm.user.model.Comment;
import ru.practicum.ewm.utils.DateFormatterUtil;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateFormatterUtil.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "formatDateToString")
    CommentShortResponse mapToCommentShortResponse(Comment comment);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "formatDateToString")
    ReplyCommentResponse mapToReplyCommentResponse(Comment comment);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "formatDateToString")
    List<ReplyCommentResponse> mapToReplyCommentResponseList(List<Comment> comment);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "formatDateToString")
    CommentAdminResponse mapToCommentAdminResponse(Comment comment);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "formatDateToString")
    @Mapping(target = "replyComments", ignore = true)
    CommentFullResponse mapToCommentFullResponse(Comment comment);
}
