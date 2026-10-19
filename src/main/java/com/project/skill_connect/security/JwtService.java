package com.project.skill_connect.security;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service 
public class JwtService {
    private final SecretKey secretKey;
    private final long  JwtExpiration;

    public JwtService(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") long JwtExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.JwtExpiration = JwtExpiration;
    }

    public String generateToken (String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(JwtExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token, String email){
        try{
            String tokenEmail = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
            return tokenEmail.equals(email);
        } catch (io.jsonwebtoken.JwtException  | IllegalArgumentException e) {
            return false;
        }
    }


    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
