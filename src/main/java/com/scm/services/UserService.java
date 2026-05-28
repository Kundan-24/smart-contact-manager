package com.scm.services;

import java.util.List;
import java.util.Optional;

import com.scm.entities.User;

public interface UserService {

    User saveUser(User user);

    Optional<User> getByUserId(Long userId);

    Optional<User> updateUser(User newData);

    void deleteById(Long userId);

    List<User> getAllUsers();

    User getUserByEmail(String email);

    boolean isUserExist(Long userId);

    boolean isUserExistByEmail(String email);
}
