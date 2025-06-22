package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() != null) {
                if (newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                    throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'");
                } else {
                    oldUser.setEmail(newUser.getEmail());
                }
            }
            if (newUser.getLogin() != null) {
                if (newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                    throw new ValidationException("Логин не может быть пустым и содержать пробелы");
                } else {
                    oldUser.setLogin(newUser.getLogin());
                }
            }
            if (newUser.getName() != null) {
                if (newUser.getName().isBlank()) {
                    newUser.setName(newUser.getLogin());
                } else {
                    oldUser.setName(newUser.getName());
                }
            }
            if (newUser.getBirthday() != null) {
                if (newUser.getBirthday().isAfter(LocalDate.now())) {
                    throw new ValidationException("Дата рождения не может быть в будущем");
                } else {
                    oldUser.setBirthday(newUser.getBirthday());
                }
            }
            return oldUser;
        }
        throw new ValidationException("Пользователь с ID = " + newUser.getId() + " не найден");
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getById(Integer id) {
        if (users.get(id) == null || id <= 0) {
            throw new IllegalArgumentException("Пользователя с ID: " + id + " не существует");
        }
        return users.get(id);
    }

    public Integer getNextId() {
        int maxid = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++maxid;
    }
}
