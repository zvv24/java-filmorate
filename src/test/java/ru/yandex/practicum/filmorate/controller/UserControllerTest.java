package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    UserController userController = new UserController();

    @Test
    public void emailCannotBeEmpty() {
        User user = new User();
        user.setEmail(" ");
        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'",
                violations.iterator().next().getMessage());
    }

    @Test
    public void emailMustBeContainSymbolAT() {
        User user = new User();
        user.setEmail("email");
        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'",
                violations.iterator().next().getMessage());
    }

    @Test
    public void loginCannotBeEmptyOrContainSpaces() {
        User user = new User();
        user.setEmail("qwe@email.com");
        user.setName("Name");
        user.setLogin(" ");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals("Логин не может быть пустым и содержать пробелы",
                violations.iterator().next().getMessage());
    }

    @Test
    public void ifNameIsEmptyUsedLogin() {
        User user = new User();
        user.setEmail("qwe@email.com");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2005, 6, 24));

        User user1 = userController.create(user);
        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    public void birthdayCannotBeInTheFuture() {
        User user = new User();
        user.setEmail("qwe@email.com");
        user.setName("Name");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2026, 6, 24));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }
}
