package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(Integer filmId, Integer userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public Map<Integer, Set<Integer>> getLikesForFilms(Collection<Integer> filmIds) {
        String sql = String.format(
                "SELECT film_id, user_id FROM likes WHERE film_id IN (%s)",
                String.join(",", Collections.nCopies(filmIds.size(), "?"))
        );

        Map<Integer, Set<Integer>> likesMap = new HashMap<>();
        jdbcTemplate.query(sql, filmIds.toArray(), resultSet -> {
            Integer filmId = resultSet.getInt("film_id");
            Integer userId = resultSet.getInt("user_id");
            likesMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        return likesMap;
    }

    public List<Integer> getMostPopularFilms(Integer count) {
        String sql = "SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Integer.class, count);
    }
}