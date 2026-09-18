package itmo.info.security.lab.service;

import org.springframework.stereotype.Service;

import itmo.info.security.lab.exception.ForbiddenException;
import itmo.info.security.lab.exception.NotFoundException;
import itmo.info.security.lab.model.dto.UserLoginRequest;
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

    public String login(UserLoginRequest request) {
        User user = userRepo.findById(request.getLogin()).orElseThrow(() -> new NotFoundException("User not found"));

        if (hashService.matches(request.getPassword(), user.getPassword())) {
            return jwtService.generateToken(user.getLogin());
        } else
            throw new ForbiddenException("Wrong password");
    }

    public String register(UserLoginRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(hashService.getHashFromText(request.getPassword()));

        userRepo.save(user);
        return jwtService.generateToken(user.getLogin());
    }
}
