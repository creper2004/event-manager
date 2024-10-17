package ru.gorohov.eventmanager.secured.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component

public class JwtManager {

    private final String secretKey;

    private final Duration duration;

    public JwtManager(@Value("${jwt.key}") String secretKey,
                      @Value("${jwt.working.time}") Long duration) {
        this.secretKey = secretKey;
        this.duration = Duration.ofHours(duration);
    }


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        var role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("role", role);
        Date issuedTime = new Date();
        Date expirationTime = new Date(issuedTime.getTime() + duration.toMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedTime)
                .expiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parse(token);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public String getLoginFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        List<String> roles = (List<String>) claims.get("role");
        return roles.get(0);
    }

}