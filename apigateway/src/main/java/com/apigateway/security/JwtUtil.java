package com.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret}")
    private String SECRET;

    private Key key() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key())
                .build().parseClaimsJws(token).getBody();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}
