package itmo.info.security.lab.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    private long jwtExpirationMs = 30L * 24 * 60 * 60 * 1000;

    public String generateToken(String sub) {
        return Jwts.builder().subject(sub).issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs)).signWith(getSigningKey()).compact();
    }

    public String extractSub(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return claims.getSubject();
    }

    public boolean verify(String jwt) {
        try {
            Claims claims = extractAllClaims(jwt);

            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
