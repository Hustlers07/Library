package com.library.user_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.library.user_management.entity.User;
import com.library.user_management.service.UserProfileDetailsService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserProfileDetailsService userDetailsService;
    private final ApiRuleDefiner securityRules;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();
        log.debug("JwtFilter start: {} {}", method, path);
        log.debug("Authorization header present: {}",
                StringUtils.hasText(request.getHeader(AUTHORIZATION_HEADER)));

        try {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            Optional<SecurityRule> matchedRule = securityRules.getSecurityRules().stream()
                    .filter(r -> "permitAll".equals(r.getAccess()) && pathMatcher.match(r.getPattern(), path))
                    .findFirst();

            if (matchedRule.isPresent()) {
                log.debug("PermitAll matched: {} for path {}", matchedRule.get().getPattern(), path);
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = extractJwtFromRequest(request);
            log.debug("JWT extracted: {}", jwt != null ? "present" : "none");

            if (StringUtils.hasText(jwt)) {
                String email = tokenProvider.extractEmail(jwt);
                log.debug("Email from token: {}", email);

                if (StringUtils.hasText(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userDetailsService.loadByEmail(email);

                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(user.getEmail())
                            .password(user.getPassword())
                            .authorities(user.getAuthorities())
                            .build();

                    boolean valid = tokenProvider.isTokenValid(jwt, userDetails);
                    log.debug("Token valid: {}", valid);

                    if (valid) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        log.debug("SecurityContext before setting: {}",
                                SecurityContextHolder.getContext().getAuthentication());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("SecurityContext after setting: {}",
                                SecurityContextHolder.getContext().getAuthentication());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage(), ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}