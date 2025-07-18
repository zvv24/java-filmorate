package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private final LikeDao likeDao;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, LikeDao likeDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeDao = likeDao;
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
        List<Integer> popularFilms = likeDao.getMostPopularFilms(count);
        return popularFilms.stream()
                .map(filmStorage::getById)
                .toList();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film getById(Integer id) {
        return filmStorage.getById(id);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }
}