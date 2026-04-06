package com.quantnexus.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public void blacklistToken(String token){

        long remainingTimeMs = jwtService.extractExpiration(token).getTime()
                - System.currentTimeMillis();

        //Use tiny jti instead of large token
        String jti = jwtService.extractJti(token);

        if(remainingTimeMs > 0 && jti != null){
            String key = PREFIX + jti;
            redisTemplate.opsForValue().set(key,"revoked", remainingTimeMs, TimeUnit.MICROSECONDS);
            log.info("Security: High-Efficiency JTI [{}] added to Redis Blacklist.", jti);
        }

    }

    public boolean isTokenBlacklisted(String token){
        String jti = jwtService.extractJti(token);
        if(jti == null) return false;
        return redisTemplate.hasKey(PREFIX + jti);
    }
}
