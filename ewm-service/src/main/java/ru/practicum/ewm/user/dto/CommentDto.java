package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotBlank(message = "Комментарий не может быть пустым.")
    @Size(min = 20, max = 2000, message = "Комментарий должен быть в пределах 20 - 2000 символов.")
    private String comment;
}
