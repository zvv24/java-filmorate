package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController userController = new UserController();

    @Test
    public void emailCannotBeEmpty() {
        User user = new User();
        user.setEmail(" ");
        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'",
                exception.getMessage());
    }

    @Test
    public void emailMustBeContainSymbolAT() {
        User user = new User();
        user.setEmail("email");
        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'",
                exception.getMessage());
    }

    @Test
    public void loginCannotBeEmptyOrContainSpaces() {
        User user = new User();
        user.setEmail("email@.com");
        user.setName("Name");
        user.setLogin(" ");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void ifNameIsEmptyUsedLogin() {
        User user = new User();
        user.setEmail("email@.com");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        User user1 = userController.create(user);
        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    public void birthdayCannotBeInTheFuture() {
        User user = new User();
        user.setEmail("email@.com");
        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2026, 6, 24));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
