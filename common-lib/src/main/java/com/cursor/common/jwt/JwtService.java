package com.cursor.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtService {

    private final Key signingKey;
    private final long accessTtlSeconds;

    public JwtService(String base64Secret, long accessTtlSeconds) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTtlSeconds = accessTtlSeconds;
    }

    public String generateToken(String userId, List<String> roles, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(accessTtlSeconds)));
        if (extraClaims != null) {
            extraClaims.forEach((k, v) -> Jwts.builder().claim(k, v));
        }
        return Jwts.builder()
            .claim("roles", roles)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


