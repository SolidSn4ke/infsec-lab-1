package itmo.info.security.lab.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashService {
    final PasswordEncoder encoder;

    public String getHashFromText(String s) {
        return encoder.encode(s);
    }

    public boolean matches(String raw, String hash) {
        return encoder.matches(raw, hash);
    }
}
