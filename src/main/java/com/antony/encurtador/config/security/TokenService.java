package com.antony.encurtador.config.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String Secret_Key;

    public String generateToken(String email){
        return Jwts.builder().subject(email).issuedAt(new Date())
                .expiration(Date.from(LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"))))
                .signWith(Keys.hmacShaKeyFor(Secret_Key.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Secret_Key.getBytes(StandardCharsets.UTF_8)))
                .build().parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private boolean isTokenExpired(String token){
        Date expiration = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Secret_Key.getBytes(StandardCharsets.UTF_8)))
                .build().parseSignedClaims(token)
                .getPayload().getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
