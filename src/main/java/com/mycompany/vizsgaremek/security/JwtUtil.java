package com.mycompany.vizsgaremek.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

public class JwtUtil {
    
    // Minimum 32 karakter !!!
    private static final String SECRET = "THIS_IS_A_REALLY_LONG_SECRET_KEY_1234567890";
    private static final long EXPIRATION_MS = 1000L * 60 * 60; // 1 óra

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public static String generateToken(Integer id, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(email)           // token tulajdonosa
                .claim("userId", id)         // felhasználó ID hozzáadása
                .setIssuedAt(now)            // kiadás idő
                .setExpiration(expiry)       // lejárat
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
