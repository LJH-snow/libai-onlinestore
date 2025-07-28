/*
 * @Author: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @Date: 2025-06-21 16:30:28
 * @LastEditors: error: error: git config user.name & please set dead value or install git && error: git config user.email & please set dead value or install git & please set dead value or install git
 * @LastEditTime: 2025-06-23 19:06:48
 * @FilePath: \AAA\backend\src\main\java\com\gzmtu\backend\security\JwtAuthenticationFilter.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.gzmtu.backend.security;

import com.gzmtu.backend.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        log.info("====== JWT Filter Start for Request URI: {} ======", request.getRequestURI());

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                log.info("Token parsed successfully. Username: '{}'", username);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired", e);
            } catch (Exception e) {
                log.error("Error parsing JWT token", e);
            }
        } else {
            log.warn("Authorization header is missing or does not start with Bearer String for URI: {}", request.getRequestURI());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Security context is null, attempting to authenticate user '{}'", username);
            try {
                UserDetails userDetails = this.userService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(jwtToken)) {
                    String role = jwtTokenUtil.getRoleFromToken(jwtToken);
                    log.info("Token validated. Role from token: '{}'", role);
                    
                    String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
                    List<SimpleGrantedAuthority> authorities =
                            Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));
                    log.info("User '{}' granted authorities: {}", username, authorities);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("User '{}' authenticated successfully. Security context updated.", username);
                } else {
                    log.warn("Token validation failed for user: '{}'", username);
                }
            } catch (Exception e) {
                log.error("Error during user authentication process for user '{}'", username, e);
            }
        } else if (username != null) {
            log.info("Security context already contains authentication for user '{}'", username);
        }

        log.info("====== JWT Filter End for Request URI: {} ======", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
