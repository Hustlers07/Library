package com.library.user_management.config;

import com.library.user_management.security.ApiRuleDefiner;
import com.library.user_management.security.JwtAuthenticationEntryPoint;
import com.library.user_management.security.JwtAuthenticationFilter;
import com.library.user_management.security.SecurityRule;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

/**
 * Spring Security Configuration
 * Configures authentication, authorization, and filter chain
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Value("${ALLOWED_ORIGINS:http://localhost:4200}")  // default value
    private String allowedOrigins;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ApiRuleDefiner securityRules;

    /**
     * Configure password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configure authentication provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configure authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configure security filter chain
     * Defines authorization rules and filter configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> {
                    // Allow preflight requests
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // Separate permitAll rules from others for cleaner configuration
                    List<SecurityRule> permitAllRules = securityRules.getSecurityRules().stream()
                            .filter(r -> r.getAccess().equals("permitAll"))
                            .toList();
                    
                    List<SecurityRule> protectedRules = securityRules.getSecurityRules().stream()
                            .filter(r -> !r.getAccess().equals("permitAll"))
                            .toList();
                    
                    // Configure permitAll rules first
                    for (SecurityRule rule : permitAllRules) {
                        auth.requestMatchers(rule.getPattern()).permitAll();
                    }
                    
                    // Configure protected rules
                    for (SecurityRule rule : protectedRules) {
                        String access = rule.getAccess();
                        if (access.startsWith("hasAnyRole")) {
                            String roles = access.substring(access.indexOf('(') + 1, access.indexOf(')'));
                            auth.requestMatchers(rule.getPattern()).hasAnyRole(roles.split(","));
                        } else if (access.startsWith("hasRole")) {
                            String role = access.substring(access.indexOf('(') + 1, access.indexOf(')'));
                            auth.requestMatchers(rule.getPattern()).hasRole(role);
                        } else {
                            auth.requestMatchers(rule.getPattern()).authenticated();
                        }
                    }
                    
                    // Default: require authentication for any other request
                    auth.anyRequest().authenticated();
                }).authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Split comma-separated origins from env variable
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
