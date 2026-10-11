package com.coffeekartel.service;

import com.coffeekartel.dto.request.LoginRequest;
import com.coffeekartel.dto.request.RegisterRequest;
import com.coffeekartel.dto.response.AuthResponse;
import com.coffeekartel.dto.response.UserProfileResponse;
import com.coffeekartel.entity.Role;
import com.coffeekartel.entity.User;
import com.coffeekartel.exception.ResourceNotFoundException;
import com.coffeekartel.repository.RoleRepository;
import com.coffeekartel.repository.UserRepository;
import com.coffeekartel.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("An account with this email address already exists.");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalStateException("An account with this phone number already exists.");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER")));

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword())
        );
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);

        User saved = userRepository.save(user);
        log.info("Registered new guest user: {} ({})", saved.getFullName(), saved.getEmail());

        String token = generateTokenForUser(saved);
        return new AuthResponse(token, mapToProfile(saved));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .or(() -> userRepository.findByPhone(request.getUsername()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials: user not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("Your account is deactivated. Please contact support.");
        }

        log.info("User {} logged in successfully", user.getEmail());
        String token = generateTokenForUser(user);
        return new AuthResponse(token, mapToProfile(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhone(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return mapToProfile(user);
    }

    private String generateTokenForUser(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPasswordHash(),
                        user.isActive(),
                        true,
                        true,
                        true,
                        authorities
                );

        return jwtService.generateToken(userDetails);
    }

    public UserProfileResponse mapToProfile(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                roleNames
        );
    }
}
