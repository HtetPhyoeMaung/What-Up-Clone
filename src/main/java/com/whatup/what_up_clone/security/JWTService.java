package com.whatup.what_up_clone.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JWTService {
    public static final long EXPIRATION_DATE = 1000 * 60 * 24 * 7 ;

    private final SecretKey key;

    public JWTService(){
        String secretKey = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
        byte[] keyByte = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));
        key = new SecretKeySpec(keyByte,"HmacSHA256");
    }

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token , Claims::getSubject);
    }
    public String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken!=null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim(); // Ensure token is trimmed
        }
        return null;
    }







    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        // Check if the token is null or empty
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token is null or empty");
        }

        try {
            // Use parserBuilder to parse the JWT
            Claims claims = Jwts.parser()
                    .setSigningKey(key) // Set your signing key
                    .build()
                    .parseClaimsJws(token.trim()) // Trim whitespace and parse the token
                    .getBody(); // Get the claims from the token

            return claimsTFunction.apply(claims); // Apply the provided function to the claims
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or malformed JWT token", e); // Handle exceptions
        }
    }




    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
