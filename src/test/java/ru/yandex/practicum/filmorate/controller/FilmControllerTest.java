package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    FilmController filmController;

    @Test
    public void creatingFilmWithEmptyNameThrowsException() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2025, 6, 5));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void maxDescriptionIs200Symbol() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("D".repeat(201));
        film.setReleaseDate(LocalDate.of(2025, 6, 5));
        film.setDuration(60);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    public void minDateIs_28_12_1895() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(60);

        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void durationCannotBeNegativeOrZero() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2025, 6, 5));
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }
}
