package com.letsplay.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;




// This filter intercepts each HTTP request to validate the JWT token and set the authentication in the security context.
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    //Exclude certain URLs from JWT validation so it doesn't check for a token on these paths
    private static final List<String> EXCLUDE_URLS = List.of(
            "/api/auth/login",
            "/api/auth/signup",
            "/products"
    );

    @Autowired // automatically injecting JwtUtil
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Letting GET requests and excluded urls through
        if ("GET".equals(method) && EXCLUDE_URLS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        // SKIP JWT validation for auth endpoints
        if (request.getServletPath().startsWith("/api/auth")) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {

                Claims claims = jwtUtil.extractClaims(token);
                String userId = claims.getSubject();
                String role = claims.get("role", String.class);


                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    User principal = new User(userId, "", authorities);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}

