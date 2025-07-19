package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private final LikeDao likeDao;
    private final GenreDao genreDao;
    private final MpaService mpaService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, LikeDao likeDao, GenreDao genreDao, MpaService mpaService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDao = likeDao;
        this.genreDao = genreDao;
        this.mpaService = mpaService;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь может поставить только 1 лайк на фильм");
        }

        likeDao.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);

        likeDao.removeLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        List<Film> films = ((FilmDbStorage) filmStorage).getMostPopularFilms(count);
        films.forEach(this::loadGenresAndLikes);
        return films;
    }

    private void loadGenresAndLikes(Film film) {
        Set<Genre> genres = new HashSet<>(genreDao.getGenresForFilms(Collections.singleton(film.getId()))
                .getOrDefault(film.getId(), Collections.emptySet()));
        film.setGenres(genres);

        Set<Integer> likes = likeDao.getLikesForFilms(Collections.singleton(film.getId()))
                .getOrDefault(film.getId(), Collections.emptySet());
        film.setLikes(likes);
    }

    public void validateFilms(Film film) {
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        try {
            mpaService.getById(film.getMpa().getId());
        } catch (ValidationException e) {
            throw new NotFoundException("MPA с id=" + film.getMpa().getId() + " не существует");
        }
        genreDao.validateGenres(film.getGenres());
    }

    public Film create(Film film) {
        validateFilms(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        validateFilms(newFilm);
        return filmStorage.update(newFilm);
    }

    public Film getById(Integer id) {
        Film film = filmStorage.getById(id);
        loadGenresAndLikes(film);
        return film;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }
}