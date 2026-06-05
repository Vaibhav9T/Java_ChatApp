package com.chatapp.chat_backend.controllers;

import com.chatapp.chat_backend.config.JwtUtil;
import com.chatapp.chat_backend.models.User;
import com.chatapp.chat_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") 

public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Inject the BCrypt encoder we defined in SecurityConfig
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        
        // HASH THE PASSWORD BEFORE SAVING
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
        }

        User user = userOpt.get();
        user.setOnline(true);
        userRepository.save(user);

        // Generate the JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Return a JSON response with the token
        return ResponseEntity.ok(java.util.Map.of(
            "token", token,
            "username", user.getUsername()
        ));
    }
}