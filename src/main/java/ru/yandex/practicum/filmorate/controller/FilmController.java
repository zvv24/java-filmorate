package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
    public Film create(@Valid @RequestBody Film film) {
        log.info("Создание нового фильма {}", film);

        if (film.getDescription() != null && film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма с ID {}", newFilm.getId());
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null) {
                if (newFilm.getName().isBlank()) {
                    throw new ValidationException("Название не может быть пустым");
                } else {
                    oldFilm.setName(newFilm.getName());
                }
            }
            if (newFilm.getDescription() != null) {
                if (newFilm.getDescription().length() > 200) {
                    throw new ValidationException("Максимальная длина описания — 200 символов");
                } else {
                    oldFilm.setDescription(newFilm.getDescription());
                }
            }
            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(minReleaseDate)) {
                    throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
                } else {
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                }
            }
            if (newFilm.getDuration() != null) {
                if (newFilm.getDuration() <= 0) {
                    throw new ValidationException("Продолжительность фильма должна быть положительным числом");
                } else {
                    oldFilm.setDuration(newFilm.getDuration());
                }
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
