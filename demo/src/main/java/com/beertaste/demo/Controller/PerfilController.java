package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Country;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.repository.CountryRepository;
import com.beertaste.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Controller
public class PerfilController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    // GET: mostrar perfil
    @GetMapping("/miperfil")
    public String perfil(Model model, Authentication auth) {

        if (auth == null || auth.getName() == null) {
            return "redirect:/login";
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        List<Country> countries = countryRepository.findAll();

        model.addAttribute("user", user);      // se usará en Thymeleaf
        model.addAttribute("countries", countries);

        return "perfil";
    }

    // POST: actualizar perfil
    @PostMapping("/miperfil")
    public String updatePerfil(@ModelAttribute User userForm, Authentication auth) {

        if (auth == null || auth.getName() == null) {
            return "redirect:/login";
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Actualizar datos
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setCountry(userForm.getCountry());

        // Actualizar password solo si se ingresa
        if (userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
            String encodedPassword = Base64.getEncoder()
                                           .encodeToString(userForm.getPassword().getBytes(StandardCharsets.UTF_8));
            user.setPassword(encodedPassword);
        }

        userRepository.save(user);

        return "redirect:/miperfil?success";
    }
}
