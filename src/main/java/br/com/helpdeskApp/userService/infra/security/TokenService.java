package br.com.helpdeskApp.userService.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.helpdeskApp.userService.model.User;

@Service 
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    private static final String ISSUER = "userService";

    public String generateToken(User user) {
        try{
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withExpiresAt(dateExpiration())
                .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    private Instant dateExpiration() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String token) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }


}
