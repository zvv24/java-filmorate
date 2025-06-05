package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Создание нового фильма {}", film);
        if (film.getName() == null || film.getName().isBlank()) {
            String error = "Название не может быть пустым";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (film.getDescription().length() > 200) {
            String error = "Максимальная длина описания — 200 символов";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            String error = "Дата релиза должна быть не раньше 28 декабря 1895 года";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (film.getDuration() <= 0) {
            String error = "Продолжительность фильма должна быть положительным числом";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Обновление фильма с ID {}", newFilm.getId());
        if (newFilm.getId() == null) {
            String error = "Id должен быть указан";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (newFilm.getDescription().length() > 200) {
            String error = "Максимальная длина описания — 200 символов";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (newFilm.getReleaseDate().isBefore(minReleaseDate)) {
            String error = "Дата релиза должна быть не раньше 28 декабря 1895 года";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (newFilm.getDuration() <= 0) {
            String error = "Продолжительность фильма должна быть положительным числом";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм с ID = {} обновлен", newFilm.getId());
            return oldFilm;
        }
        throw new ValidationException("Фильм с ID = " + newFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    public Integer getNextId() {
        int maxid = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++maxid;
    }
}
