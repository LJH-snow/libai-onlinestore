package com.gzmtu.backend.security;

import com.gzmtu.backend.dto.UserDTO;
import com.gzmtu.backend.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * ✅ 生成包含角色的 JWT token
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)  // ✅ 加入角色字段
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ 用于登录成功后创建带 token 的 UserDTO
     */
    public UserDTO generateUserDTO(User user) {
        String token = generateToken(user.getUsername(), user.getRole());  // ✅ 用包含角色的方法
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        dto.setToken(token);
        return dto;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return (String) claims.get("role");
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
