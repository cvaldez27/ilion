package mx.edu.uaq.fif.ilion.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mx.edu.uaq.fif.ilion.entity.User;
import mx.edu.uaq.fif.ilion.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("roles", new String[]{"DOCTOR", "PATIENT"});
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role) {

        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/register?error=exists";
        }

        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf(role.toUpperCase()));

        userRepository.save(user);

        return "redirect:/login";
    }
}