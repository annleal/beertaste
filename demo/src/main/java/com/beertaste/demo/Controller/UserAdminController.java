package com.beertaste.demo.Controller;

import com.beertaste.demo.Services.CountryService;
import com.beertaste.demo.Services.UserService;
import com.beertaste.demo.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserAdminController {

    private final UserService userService;
    private final CountryService countryService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserAdminController(UserService userService, CountryService countryService) {
        this.userService = userService;
        this.countryService = countryService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // -----------------------
    // HTML PAGE
    // -----------------------
    @GetMapping("/users")
    public String usersPage() {
        return "admin/users"; // templates/admin/users.html
    }

    // -----------------------
    // REST API - LIST USERS WITH PAGINATION
    // -----------------------
    @GetMapping("/api/users")
    @ResponseBody
    public Map<String, Object> listUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userService.getUsers(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", usersPage.getContent());
        response.put("currentPage", usersPage.getNumber());
        response.put("totalPages", usersPage.getTotalPages());
        response.put("totalElements", usersPage.getTotalElements());

        return response;
    }

    // -----------------------
    // GET USER BY ID
    // -----------------------
@GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(u -> ResponseEntity.ok().body((Object) u))
                .orElse(ResponseEntity.badRequest().body("Usuario no encontrado"));
    }

    // -----------------------
    // CREATE USER
    // -----------------------
    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestParam String name,
                                        @RequestParam String surname,
                                        @RequestParam String email,
                                        @RequestParam String password,
                                        @RequestParam Long country,
                                        @RequestParam(defaultValue = "USER") String role) {
        try {
            if(userService.getUserByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("Email ya registrado");
            }

            User user = new User();
            user.setName(name);
            user.setSurname(surname);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password)); // BCrypt
            user.setRole(role);
            user.setCountry(countryService.getCountryById(country).orElseThrow(
                    () -> new IllegalArgumentException("País no válido")
            ));

            return ResponseEntity.ok(userService.saveUser(user));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // -----------------------
    // UPDATE USER
    // -----------------------
    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestParam String name,
                                        @RequestParam String surname,
                                        @RequestParam Long country,
                                        @RequestParam(required = false) String password,
                                        @RequestParam String role,
                                        @RequestParam String email) {
        try {
            User existing = userService.getUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Validar email único si cambió
            if(!existing.getEmail().equals(email) && userService.getUserByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("Email ya registrado");
            }

            existing.setName(name);
            existing.setSurname(surname);
            existing.setRole(role);
            existing.setEmail(email);
            existing.setCountry(countryService.getCountryById(country)
                    .orElseThrow(() -> new IllegalArgumentException("País no válido")));

            if(password != null && !password.isBlank()) {
                existing.setPassword(passwordEncoder.encode(password));
            }

            return ResponseEntity.ok(userService.saveUser(existing));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // -----------------------
    // DELETE USER
    // -----------------------
    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado.");
    }
}
