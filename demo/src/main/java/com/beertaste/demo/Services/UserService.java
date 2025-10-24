package com.beertaste.demo.Services;

import com.beertaste.demo.entity.User;
import com.beertaste.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        if (!StringUtils.hasText(user.getName())) {
            throw new IllegalArgumentException("El campo 'name' no puede estar vacío");
        }
        if (!StringUtils.hasText(user.getSurname())) {
            throw new IllegalArgumentException("El campo 'surname' no puede estar vacío");
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("El campo 'email' no puede estar vacío");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("El campo 'password' no puede estar vacío");
        }
        if (user.getCountry() == null) {
            throw new IllegalArgumentException("Debe asignarse un 'country' al usuario");
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
