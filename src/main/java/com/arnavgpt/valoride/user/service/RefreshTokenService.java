package com.arnavgpt.valoride.user.service;

import com.arnavgpt.valoride.config.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public RefreshTokenService(StringRedisTemplate redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    /**
     * Store refresh token in Redis with TTL
     */
    public void storeRefreshToken(UUID userId, String refreshToken) {
        String tokenId = jwtService.extractTokenId(refreshToken);
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
        String userTokensKey = USER_TOKENS_PREFIX + userId.toString();

        long ttlMillis = jwtService.getRefreshTokenExpiration();
        long ttlSeconds = ttlMillis / 1000;

        // Store token -> userId mapping
        redisTemplate.opsForValue().set(tokenKey, userId.toString(), ttlSeconds, TimeUnit.SECONDS);

        // Add tokenId to user's token set (for logout all functionality)
        redisTemplate.opsForSet().add(userTokensKey, tokenId);
        redisTemplate.expire(userTokensKey, ttlSeconds, TimeUnit.SECONDS);

        logger.debug("Stored refresh token for user: {}", userId);
    }

    /**
     * Validate refresh token exists in Redis and is not revoked
     */
    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            if (!jwtService.isRefreshToken(refreshToken)) {
                return false;
            }

            String tokenId = jwtService.extractTokenId(refreshToken);
            String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;

            Boolean exists = redisTemplate.hasKey(tokenKey);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            logger.error("Error validating refresh token", e);
            return false;
        }
    }

    /**
     * Get userId from stored refresh token
     */
    public UUID getUserIdFromToken(String refreshToken) {
        String tokenId = jwtService.extractTokenId(refreshToken);
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;

        String userId = redisTemplate.opsForValue().get(tokenKey);
        if (userId == null) {
            return null;
        }
        return UUID.fromString(userId);
    }

    /**
     * Revoke a specific refresh token
     */
    public void revokeRefreshToken(String refreshToken) {
        try {
            String tokenId = jwtService.extractTokenId(refreshToken);
            UUID userId = jwtService.extractUserId(refreshToken);

            String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
            String userTokensKey = USER_TOKENS_PREFIX + userId.toString();

            redisTemplate.delete(tokenKey);
            redisTemplate.opsForSet().remove(userTokensKey, tokenId);

            logger.debug("Revoked refresh token for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error revoking refresh token", e);
        }
    }

    /**
     * Revoke all refresh tokens for a user (logout from all devices)
     */
    public void revokeAllUserTokens(UUID userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId.toString();

        Set<String> tokenIds = redisTemplate.opsForSet().members(userTokensKey);
        if (tokenIds != null && !tokenIds.isEmpty()) {
            for (String tokenId : tokenIds) {
                String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
                redisTemplate.delete(tokenKey);
            }
        }

        redisTemplate.delete(userTokensKey);
        logger.debug("Revoked all tokens for user: {}", userId);
    }

    /**
     * Get count of active sessions for a user
     */
    public long getActiveSessionCount(UUID userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId.toString();
        Long count = redisTemplate.opsForSet().size(userTokensKey);
        return count != null ? count : 0;
    }
}