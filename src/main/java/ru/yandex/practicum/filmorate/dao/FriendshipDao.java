package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FriendshipDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<Integer> getAllFriends(Integer userId) {
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId);
    }

    public List<Integer> getMutualFriends(Integer userId, Integer otherId) {
        String sql = "SELECT DISTINCT f1.friend_id " +
                "FROM friendship AS f1 " +
                "JOIN friendship AS f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, userId, otherId);
    }
}
