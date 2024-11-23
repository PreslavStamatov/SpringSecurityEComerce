package com.eComerce.eCom.controllers;

import com.eComerce.eCom.entities.Role;
import com.eComerce.eCom.entities.UserEntity;
import com.eComerce.eCom.repositories.RoleRepository;
import com.eComerce.eCom.security.JwtGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final JwtGenerator jwtGenerator;

    private RoleRepository roleRepository;

    @Autowired
    public UserController(JwtGenerator jwtGenerator, RoleRepository roleRepository) {
        this.jwtGenerator = jwtGenerator;
        this.roleRepository = roleRepository;
    }

    // Secured endpoint to get user data based on JWT token
    @CrossOrigin(origins = "http://localhost:3001")
    @GetMapping("/data")
    public UserEntity getUserData() {
        // Get the current authenticated user (from SecurityContext)
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Role> roles = authenticatedUser.getAuthorities().stream()
                .map(grantedAuthority -> {
                    String roleName = grantedAuthority.getAuthority(); // Extract role name
                    return roleRepository.findByName(roleName).orElseThrow(() ->
                            new RuntimeException("Role not found: " + roleName));
                })
                .collect(Collectors.toList());

        // You can use the username to fetch more detailed user info from the database
        String username = authenticatedUser.getUsername();

        // For the sake of example, let's just return a mock user entity with the username
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setRoles(roles);
        // You can fetch additional data here from the database if needed
        return user;
    }
}
