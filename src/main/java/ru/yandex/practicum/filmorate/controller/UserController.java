package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание нового пользователя {}", user);
        if ((user.getEmail() == null || user.getEmail().isBlank()) || !user.getEmail().contains("@")) {
            String error = "Электронная почта не может быть пустой и должна содержать символ '@'";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if ((user.getLogin() == null || user.getLogin().isBlank()) && user.getLogin().contains(" ")) {
            String error = "Логин не может быть пустым и содержать пробелы";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя было пустым, использован логин {}", user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String error = "Дата рождения не может быть в будущем";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            String error = "Id должен быть указан";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if ((newUser.getEmail() == null || newUser.getEmail().isBlank()) || !newUser.getEmail().contains("@")) {
            String error = "Электронная почта не может быть пустой и должна содержать символ '@'";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if ((newUser.getLogin() == null || newUser.getLogin().isBlank()) && newUser.getLogin().contains(" ")) {
            String error = "Логин не может быть пустым и содержать пробелы";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("Имя было пустым, использован логин {}", newUser.getLogin());
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            String error = "Дата рождения не может быть в будущем";
            log.error("Ошибка валидации {}", error);
            throw new ValidationException(error);
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() != null) {
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Пользователь с ID = {} обновлен", newUser.getId());
            return oldUser;
        }
        throw new ValidationException("Пользователь с ID = " + newUser.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
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
