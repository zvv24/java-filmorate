package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        userStorage.getById(friendId);

        user.getFriends().add(friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        userStorage.getById(friendId);

        user.getFriends().remove(friendId);
    }

    public List<User> getAllFriends(Integer userId) {
        User user = userStorage.getById(userId);

        return user.getFriends().stream()
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherId);

        Set<Integer> mutualFriendIds = new HashSet<>(user.getFriends());
        mutualFriendIds.retainAll(otherUser.getFriends());

        return mutualFriendIds.stream()
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .toList();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Integer id) {
        return userStorage.getById(id);
    }
}
