package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private UserStorage userStorage;
    private FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;

    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);

        friendshipDao.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);

        friendshipDao.removeFriend(userId, friendId);
    }

    public List<User> getAllFriends(Integer userId) {
        userStorage.getById(userId);
        List<Integer> friends = friendshipDao.getAllFriends(userId);
        return ((UserDbStorage) userStorage).getUsersById(friends);
    }

    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        userStorage.getById(userId);
        userStorage.getById(otherId);
        List<Integer> mutualFriends = friendshipDao.getMutualFriends(userId, otherId);
        return ((UserDbStorage) userStorage).getUsersById(mutualFriends);
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
