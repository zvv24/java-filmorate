package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    public void createAndGetUser() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userDbStorage.create(user);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getLogin()).isEqualTo("login");

        User retrievedUser = userDbStorage.getById(createdUser.getId());
        assertThat(retrievedUser).isEqualTo(createdUser);
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@email.com");
        user1.setLogin("user1");
        user1.setName("name1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@email.com");
        user2.setLogin("user2");
        user2.setName("name2");
        user2.setBirthday(LocalDate.of(2001, 1, 1));

        userDbStorage.create(user1);
        userDbStorage.create(user2);

        Collection<User> users = userDbStorage.getAll();
        assertThat(users).hasSize(2);
    }
}