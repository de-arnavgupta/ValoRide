package com.arnavgpt.valoride.user.service;

import com.arnavgpt.valoride.config.JwtService;
import com.arnavgpt.valoride.exception.DuplicateResourceException;
import com.arnavgpt.valoride.exception.ResourceNotFoundException;
import com.arnavgpt.valoride.exception.UnauthorizedException;
import com.arnavgpt.valoride.notification.service.NotificationService;
import com.arnavgpt.valoride.user.dto.AuthResponse;
import com.arnavgpt.valoride.user.dto.LoginRequest;
import com.arnavgpt.valoride.user.dto.RefreshTokenRequest;
import com.arnavgpt.valoride.user.dto.RegisterRequest;
import com.arnavgpt.valoride.user.entity.Role;
import com.arnavgpt.valoride.user.entity.User;
import com.arnavgpt.valoride.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AuthenticationManager authenticationManager,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.notificationService = notificationService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Prevent self-registration as ADMIN
        if (request.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("Cannot register as admin");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName().trim());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with id: {}", savedUser.getId());

        // Send welcome notification
        notificationService.sendWelcomeNotification(savedUser);

        return generateAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().toLowerCase().trim(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for email: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmailAndActiveTrue(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        logger.info("User logged in successfully: {}", user.getId());
        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!refreshTokenService.isRefreshTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        // Get user from token
        UUID userId = refreshTokenService.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        // Revoke old refresh token
        refreshTokenService.revokeRefreshToken(refreshToken);

        logger.info("Token refreshed for user: {}", userId);
        return generateAuthResponse(user);
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.revokeRefreshToken(refreshToken);
            logger.info("User logged out successfully");
        }
    }

    public void logoutAll(UUID userId) {
        refreshTokenService.revokeAllUserTokens(userId);
        logger.info("All sessions revoked for user: {}", userId);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getEmail()
        );

        // Store refresh token in Redis
        refreshTokenService.storeRefreshToken(user.getId(), refreshToken);

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpiration() / 1000, // Convert to seconds
                userInfo
        );
    }
}