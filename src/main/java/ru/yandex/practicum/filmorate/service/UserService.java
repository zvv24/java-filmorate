package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private UserStorage userStorage;
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!friends.containsKey(userId)) {
            friends.put(userId, new HashSet<>());
        }
        friends.get(userId).add(friendId);

        if (!friends.containsKey(friendId)) {
            friends.put(friendId, new HashSet<>());
        }
        friends.get(friendId).add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).remove(friendId);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).remove(userId);
        }
    }

    public List<User> getAllFriends(Integer userId) {
        return friends.getOrDefault(userId, Collections.emptySet()).stream()
                .map(userStorage::getById)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        Set<Integer> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> otherFriends = friends.getOrDefault(otherId, Collections.emptySet());

        return userFriends.stream()
                .filter(otherFriends::contains)
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
