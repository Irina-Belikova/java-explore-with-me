package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Поле email не может быть пустым.")
    @Email(message = "Некорректный формат email-адреса.")
    @Size(min = 6, max = 254, message = "Email должен быть в пределах 6-254 символов.")
    private String email;

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Size(min = 2, max = 250, message = "Имя пользователя должно быть в пределах 2-250 символов.")
    private String name;
}
