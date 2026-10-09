package com.aziz.gateway.repository;

import com.aziz.gateway.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private static final String TOKEN_PREFIX = "refresh:";
    private static final String USER_PREFIX = "user-refresh:";

    private final StringRedisTemplate redis;
    private final JwtProperties jwt;

    public void save(Long userId, String jti) {
        Duration ttl = jwt.refreshToken().ttl();
        redis.opsForValue().set(TOKEN_PREFIX + jti, userId.toString(), ttl);

        String userKey = USER_PREFIX + userId;
        redis.opsForSet().add(userKey, jti);
        redis.expire(userKey, ttl);
    }

    /** Atomically deletes the token and returns its owner; empty if unknown, revoked or already used. */
    public Optional<Long> consume(String jti) {
        String userId = redis.opsForValue().getAndDelete(TOKEN_PREFIX + jti);
        if (userId == null) return Optional.empty();

        redis.opsForSet().remove(USER_PREFIX + userId, jti);
        return Optional.of(Long.valueOf(userId));
    }

    public void deleteAllForUser(Long userId) {
        String userKey = USER_PREFIX + userId;
        Set<String> jtis = redis.opsForSet().members(userKey);
        if (jtis != null && !jtis.isEmpty()) {
            redis.delete(jtis.stream().map(j -> TOKEN_PREFIX + j).toList());
        }
        redis.delete(userKey);
    }
}