package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        String sql = "INSERT INTO users (user_id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователь с ID = " + user.getId() + " не найден");
        }

        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowUser);
    }

    @Override
    public User getById(Integer id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден");
        }
    }

    public List<User> getUsersById(Collection<Integer> ids) {
        String sql = String.format(
                "SELECT * FROM users WHERE user_id IN (%s)",
                ids.stream().map(String::valueOf).collect(Collectors.joining(","))
        );

        return jdbcTemplate.query(sql, this::mapRowUser);
    }

    private User mapRowUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());

        return user;
    }

    private Integer getNextId() {
        String sql = "SELECT COALESCE(MAX(user_id), 0) FROM users";
        Integer maxId = jdbcTemplate.queryForObject(sql, Integer.class);
        return maxId + 1;
    }
}
