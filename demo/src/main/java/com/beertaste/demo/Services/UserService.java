package com.beertaste.demo.Services;

import com.beertaste.demo.entity.Country;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private CountryService countryService;

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
        if (user.getCountry() != null && user.getCountry().getIdCountry() != null) {
            Country country = countryService.getCountryById(user.getCountry().getIdCountry())
                    .orElseThrow(() -> new IllegalArgumentException("País no válido"));
            user.setCountry(country);
        } else {
            throw new IllegalArgumentException("Debe seleccionar un país");
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Dentro de UserService
    public boolean recoverPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent();
    }

    public boolean registerUser(User user) {
        // Validaciones que ya tienes en saveUser
        if (!StringUtils.hasText(user.getName()) ||
                !StringUtils.hasText(user.getSurname()) ||
                !StringUtils.hasText(user.getEmail()) ||
                !StringUtils.hasText(user.getPassword()) ||
                user.getCountry() == null) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

}
