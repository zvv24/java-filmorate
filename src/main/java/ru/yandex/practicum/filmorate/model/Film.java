package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotEmpty(message = "Описание не может быть пустым")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull(message = "Дата не может быть пустой")
    private LocalDate releaseDate;
    @NotNull(message = "Продолжительность не может быть пустой")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
    private Set<Integer> likes = new HashSet<>();
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
}
