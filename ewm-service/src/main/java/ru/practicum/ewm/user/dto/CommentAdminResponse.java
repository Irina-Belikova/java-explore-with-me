package ru.practicum.ewm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.user.model.CommentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentAdminResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String comment;
    private String createdOn;
    private Long eventId;
    private Long authorId;
    private Long parentCommentId;
    private CommentStatus status;
}
