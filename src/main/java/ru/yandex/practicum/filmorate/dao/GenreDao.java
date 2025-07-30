package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
public class GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<Integer, Set<Genre>> getGenresForFilms(Collection<Integer> filmIds) {
        String sql = String.format(
                "SELECT fg.film_id, g.genre_id, g.name FROM film_genre AS fg " +
                        "JOIN genres AS g ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id IN (%s)",
                String.join(",", Collections.nCopies(filmIds.size(), "?"))
        );

        Map<Integer, Set<Genre>> genresMap = new HashMap<>();
        jdbcTemplate.query(sql, filmIds.toArray(), resultSet -> {
            Integer filmId = resultSet.getInt("film_id");
            Genre genre = new Genre();
            genre.setId(resultSet.getInt("genre_id"));
            genre.setName(resultSet.getString("name"));
            genresMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        });

        return genresMap;
    }

    public void addGenresToFilm(Integer filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        List<Genre> genreList = new ArrayList<>(genres);

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genreList.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    public void removeGenresFromFilm(Integer filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public void validateGenres(Set<Genre> genres) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        for (Genre genre : genres) {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genre.getId());
            if (count == null || count == 0) {
                throw new NotFoundException("Жанр с ID = " + genre.getId() + " не найден");
            }
        }
    }
}