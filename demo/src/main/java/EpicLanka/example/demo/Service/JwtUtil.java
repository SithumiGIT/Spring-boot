package EpicLanka.example.demo.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key for signing tokens (in production, use environment variable)
    private final String SECRET = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm";

    // Token expiration time (24 hours in milliseconds)
    private final int EXPIRATION = 86400000;

    // Create signing key from secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Generate JWT token for user
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)  // Store user email in token
                .setIssuedAt(new Date())  // When token was created
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))  // When it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign with our secret
                .compact();  // Create the token string
    }

    // Extract email from token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract any claim from token
    public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // Check if token is expired
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;  // If any error, consider token expired
        }
    }

    // Validate token against user email
    public Boolean validateToken(String token, String email) {
        try {
            final String tokenEmail = extractEmail(token);
            return (tokenEmail != null && tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Functional interface for extracting claims
    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}
