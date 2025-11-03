package ru.practicum.ewm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentFullResponse {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String comment;
    private String createdOn;
    private Long eventId;
    private Long authorId;

    @Builder.Default
    private List<CommentShortResponse> replyComments = new ArrayList<>();
}
