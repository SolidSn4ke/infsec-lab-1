package itmo.info.security.lab.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import itmo.info.security.lab.exception.ForbiddenException;
import itmo.info.security.lab.exception.NotFoundException;
import itmo.info.security.lab.model.dto.request.UserLoginRequest;
import itmo.info.security.lab.model.dto.response.AuthResponse;
import itmo.info.security.lab.model.entity.User;
import itmo.info.security.lab.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    final UserRepository userRepo;

    final HashService hashService;

    final JwtService jwtService;

    public boolean checkIfPresent(String login) {
        return userRepo.existsById(login);
    }

    public AuthResponse login(UserLoginRequest request) {
        User user = userRepo.findById(request.getLogin()).orElseThrow(() -> new NotFoundException("User not found"));

        if (hashService.matches(request.getPassword(), user.getPassword())) {
            return AuthResponse.builder()
                    .jwt(jwtService.generateToken(user.getLogin()))
                    .expirationDate(
                            new Date(new Date().getTime() + jwtService.getJwtExpirationMs()))
                    .tokenType(JwtService.TOKEN_TYPE)
                    .build();
        } else
            throw new ForbiddenException("Wrong password");
    }

    public AuthResponse register(UserLoginRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(hashService.getHashFromText(request.getPassword()));

        userRepo.save(user);
        return AuthResponse.builder()
                .jwt(jwtService.generateToken(user.getLogin()))
                .expirationDate(
                        new Date(new Date().getTime() + jwtService.getJwtExpirationMs()))
                .tokenType(JwtService.TOKEN_TYPE)
                .build();
    }

    public void validateUserLoginRequest(UserLoginRequest request) {
        if (request.getLogin() == null || !request.getLogin().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid login");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6
                || request.getPassword().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }
    }
}
