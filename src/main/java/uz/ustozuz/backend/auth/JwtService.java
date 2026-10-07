package uz.ustozuz.backend.auth;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import uz.ustozuz.backend.config.JwtProperties;
import uz.ustozuz.backend.user.User;

@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    public String generateToken(User user) {
        try {
            Instant now = Instant.now();
            Instant expiresAt = now.plus(properties.getExpirationMinutes(), ChronoUnit.MINUTES);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .claim("userId", user.getId())
                    .claim("role", user.getRole().name())
                    .claim("name", user.getName())
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiresAt))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claims
            );

            signedJWT.sign(new MACSigner(properties.getSecret().getBytes(StandardCharsets.UTF_8)));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Token yaratib bo'lmadi", e);
        }
    }

    public String extractEmail(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new IllegalStateException("Token noto'g'ri", e);
        }
    }
}