package com.rifushigi.epsilon.controller;

import com.rifushigi.epsilon.dto.AuthResponse;
import com.rifushigi.epsilon.dto.LoginRequest;
import com.rifushigi.epsilon.dto.RegistrationRequest;
import com.rifushigi.epsilon.service.AuthService;
import com.rifushigi.epsilon.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {


    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService){
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
            userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
            AuthResponse response = authService.loginUser(request);
            return ResponseEntity.ok(response);

    }
}