package ru.infinitesynergy.yampolskiy.restapiserver.service;

import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.UserAlreadyExistException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.UserNotFoundException;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createNewUser(User user) {
        User existUser = userRepository.getUserByUsername(user.getLogin());
        if(existUser != null){
            throw new UserAlreadyExistException("Пользователь с таким именем существует");
        }
        User newUser = userRepository.createUser(user);
        return newUser;
    }

    public User getUserById(Long id) {
        User user = userRepository.readUser(id);
        if(user == null) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не существует");
        }
        return user;
    }

    public void updateUserById(User user) {
        User existUser = userRepository.readUser(user.getId());
        if(existUser != null){
            throw new UserAlreadyExistException("Пользователь с таким именем существует");
        }
        userRepository.updateUser(user);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.readUser(id);
        if(user == null) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не существует");
        }
        userRepository.deleteUser(id);
    }
}
