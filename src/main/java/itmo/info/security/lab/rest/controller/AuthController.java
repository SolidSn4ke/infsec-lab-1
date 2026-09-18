package itmo.info.security.lab.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import itmo.info.security.lab.model.dto.request.UserLoginRequest;
import itmo.info.security.lab.model.dto.response.AuthResponse;
import itmo.info.security.lab.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserLoginRequest request) {
        authService.validateUserLoginRequest(request);
        if (authService.checkIfPresent(request.getLogin())) {
            return authService.login(request);
        } else
            return authService.register(request);
    }
}
