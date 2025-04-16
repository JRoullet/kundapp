package jroullet.mssecurity.configuration;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private long expirationTime;

    // Token generation method
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return createToken(claims, userDetails.getUsername());
    }

    // Token creation method --> Map(String to Object) = claims and pass a username (subject) as arguments
    private String createToken(Map<String, Object> claims, String subject) {
       return Jwts.builder()
                .setClaims(claims) // set claims related to the token
                .setSubject(subject) // set username related to the token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set starting validity
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // set end of validity
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Create Signature Key from the secret key
                .compact();
    }

    // Key encryption
    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes(); //parsing secretKey
        return Keys.hmacShaKeyFor(keyBytes); // making of signature
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // If needed
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract a specific claim from all claims
    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extracting claims method to be able to read the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
