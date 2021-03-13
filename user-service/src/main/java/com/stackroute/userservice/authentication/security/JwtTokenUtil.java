package com.stackroute.userservice.authentication.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    public static final long JWT_TOKEN_VALIDITY = 5 * 24 * 3600l;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    /*secret key in configuration file*/
    @Value("${jwt.secret}")
    private String secret;


    /**
     * Used to generate JWT token after successful authentication
     */
    public String generateToken(int userId) {
        String subject = String.valueOf(userId);
        Map<String, Object> claims = new HashMap<>();
        logger.info("Generating token for userId {}", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }
}

