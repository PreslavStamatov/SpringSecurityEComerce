package com.eComerce.eCom.security;

import com.eComerce.eCom.entities.Role;
import com.eComerce.eCom.entities.UserEntity;
import com.eComerce.eCom.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.eComerce.eCom.security.SecurityConstants.JWT_EXPIRATION;
import static com.eComerce.eCom.security.SecurityConstants.JWT_SECRET;

@Component
public class JwtGenerator {

    @Autowired
    private UserRepository userRepository;

    public String generateToken(Authentication authentication) {
        // Fetch email from Authentication object (assuming email is stored as the principal)
        String email = authentication.getName();

        // Fetch user from the database using email (email is unique)
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get roles from the user
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        // Set expiration time for the token
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION); // Define JWT_EXPIRATION as required

        // Secret key for signing the JWT
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

        // Generate JWT token
        String jwt = Jwts.builder()
                .setSubject(email)  // Set the email as the subject
                .claim("roles", roles)  // Add roles as claims
                .setIssuedAt(currentDate)  // Set the token issue date
                .setExpiration(expireDate)  // Set the expiration date
                .signWith(secretKey)  // Sign with the secret key
                .compact();  // Generate the JWT

        return jwt;  // Return the generated JWT token
    }


    public String getEmailFromJwt(String token) {
        // Parse the JWT token and extract claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))  // Set signing key
                .build()  // Build the parser
                .parseClaimsJws(token)  // Parse the token
                .getBody();  // Get the claims body

        // Return the subject (which is the email in this case)
        return claims.getSubject();  // Subject in JWT is the email
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes())) // Use SecretKey instead of raw string
                    .build() // Build the parser
                    .parseClaimsJws(token); // Parse the token
            return true;
        } catch (Exception exception) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect!");
        }
    }
}
