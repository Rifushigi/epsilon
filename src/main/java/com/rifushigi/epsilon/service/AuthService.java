package com.rifushigi.epsilon.service;

import com.rifushigi.epsilon.dto.AuthResponse;
import com.rifushigi.epsilon.dto.LoginRequest;
import com.rifushigi.epsilon.security.JwtService;
import com.rifushigi.epsilon.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService){

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse loginUser(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return new AuthResponse(token, userPrincipal.getUsername(), userPrincipal.getEmail());
    }
}
