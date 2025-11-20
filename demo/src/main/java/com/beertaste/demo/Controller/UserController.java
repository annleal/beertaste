package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Country;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.Services.CountryService;
import com.beertaste.demo.Services.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final CountryService countryService;

    public UserController(UserService userService, CountryService countryService) {
        this.userService = userService;
        this.countryService = countryService;
    }

    // Vista de perfil
    @GetMapping("/perfil")
    public String perfilPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        model.addAttribute("user", user);

        // Cargar lista de países
        List<Country> countries = countryService.getAllCountries();
        model.addAttribute("countries", countries);

        return "perfil"; // Thymeleaf template
    }

    // Actualizar perfil
    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String name,
                                   @RequestParam String surname,
                                   @RequestParam String country,
                                   @RequestParam(required = false) String password,
                                   Authentication authentication,
                                   Model model) {

        String email = authentication.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        user.setName(name);
        user.setSurname(surname);

        // Cambiar país
        Long countryId = Long.parseLong(country);
        countryService.getCountryById(countryId).ifPresent(user::setCountry);

        // Cambiar contraseña si se proporciona
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(java.util.Base64.getEncoder().encodeToString(password.getBytes()));
        }

        userService.saveUser(user);

        model.addAttribute("user", user);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("successMessage", "Perfil actualizado correctamente.");

        return "perfil";
    }
}
