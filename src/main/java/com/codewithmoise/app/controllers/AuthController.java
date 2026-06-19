package com.codewithmoise.app.controllers;

import com.codewithmoise.app.dto.LoginRequest;
import com.codewithmoise.app.dto.RegisterRequest;
import com.codewithmoise.app.models.Role;
import com.codewithmoise.app.models.User;
import com.codewithmoise.app.repository.UserRepository;
import com.codewithmoise.app.util.EmailService;
import com.codewithmoise.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }
        // validate role
        Role role;
        try {
            role = Role.valueOf(request.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + request.role());
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        userRepository.save(user);
        emailService.sendSimpleMessage(user.getEmail(), "Welcome", "Your account has been created.");
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        // authentication successful, generate token
        var savedUser = userRepository.findByEmail(request.email()).orElseThrow(() -> new BadRequestException("User not found"));
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        return ResponseEntity.ok(token);
    }
}
