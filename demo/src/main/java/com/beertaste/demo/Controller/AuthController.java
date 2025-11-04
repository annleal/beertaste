package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Country;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.Services.UserService;
import com.beertaste.demo.Services.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final CountryService countryService;

    public AuthController(UserService userService, CountryService countryService) {
        this.userService = userService;
        this.countryService = countryService;
    }

    /**
     * Muestra la página de registro
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        User user = new User();
        user.setCountry(new Country()); // inicializa country para evitar null
        model.addAttribute("user", user); // usa el mismo user
        model.addAttribute("countries", countryService.getAllCountries());
        return "register"; // Thymeleaf: register.html
    }

    /**
     * Maneja el envío del formulario de registro
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.saveUser(user);
            model.addAttribute("successMessage", "Usuario registrado correctamente. Puedes iniciar sesión.");
            return "login"; // Redirige a login
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("countries", countryService.getAllCountries());
            model.addAttribute("user", user);
            return "register"; // Mantener en registro si hay error
        }
    }

    /**
     * Muestra la página de "Olvidé mi contraseña"
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Thymeleaf: forgot-password.html
    }

    /**
     * Maneja el envío del formulario de recuperación
     */
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        boolean found = userService.recoverPassword(email); // Método que envía correo o devuelve flag
        if (found) {
            model.addAttribute("successMessage", "Hemos enviado instrucciones a tu correo.");
        } else {
            model.addAttribute("errorMessage", "No se encontró un usuario con ese correo.");
        }
        return "forgot-password";
    }
}
