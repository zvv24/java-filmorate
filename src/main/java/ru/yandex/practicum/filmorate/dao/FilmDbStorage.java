package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbcTemplate;
    private final LikeDao likeDao;
    private final GenreDao genreDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, LikeDao likeDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.likeDao = likeDao;
        this.genreDao = genreDao;
    }

    @Override
    public Film create(Film film) {
        MpaService mpaService = new MpaService(jdbcTemplate);
        try {
            mpaService.getById(film.getMpa().getId());
        } catch (ValidationException e) {
            throw new NotFoundException("MPA с id=" + film.getMpa().getId() + " не существует");
        }
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        genreDao.validateGenres(film.getGenres());

        film.setId(getNextId());
        String sql = "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre -> {
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            });
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Фильм с ID = " + film.getId() + " не найден");
        }

        return film;
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, this::mapRowFilm);
    }

    @Override
    public Film getById(Integer id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films AS f JOIN mpa AS m ON f.mpa_id = " +
                "m.mpa_id WHERE f.film_id = ?";
        Film film = jdbcTemplate.queryForObject(sql, this::mapRowFilm, id);

        film.setGenres(new HashSet<>(genreDao.getGenresForFilms(Collections.singleton(id))
                .getOrDefault(id, Collections.emptySet())));
        film.setLikes(likeDao.getLikesForFilms(Collections.singleton(id))
                .getOrDefault(id, Collections.emptySet()));

        return film;
    }

    private Film mapRowFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private Integer getNextId() {
        String sql = "SELECT COALESCE(MAX(film_id), 0) FROM films";
        Integer maxId = jdbcTemplate.queryForObject(sql, Integer.class);
        return maxId + 1;
    }
}
