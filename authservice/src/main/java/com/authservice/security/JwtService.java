package com.authservice.security;


import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Service;

import com.authservice.model.EROLE;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
@Service
public class JwtService {
	
	@Value("${jwt.secret}")
    private String SECRET;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String username, EROLE role) {
        return Jwts.builder().setSubject(username).claim("role", role.name())
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build()
                .parseClaimsJws(token).getBody();
    }
}

