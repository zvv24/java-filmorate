package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getById(Integer id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowMpa, id);
        } catch (Exception e) {
            throw new NotFoundException("Рейтинг MPA с ID = " + id + " не найден");
        }
    }

    private Mpa mapRowMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("mpa_id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
}
