package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Integer id;
    @NotBlank(message = "Электронная почта не может быть пустой и должна содержать символ '@'")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ '@'")
    private String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "\\S+", message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}