package com.library.user_management.security;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ApiRuleDefiner {


    private final List<SecurityRule> rules = List.of(
        new SecurityRule("/api/auth/**", "permitAll"),
        new SecurityRule("/api/public/**", "permitAll"),
        new SecurityRule("/api/booking/**", "hasAnyRole(ADMIN,MEMBER,LIBRARIAN)"),
        new SecurityRule("/api/payment/**", "hasAnyRole(ADMIN,MEMBER,LIBRARIAN)"),
        new SecurityRule("/api/admin/**", "hasRole(ADMIN)"),
        new SecurityRule("/actuator/**", "permitAll")
    );

    public List<SecurityRule> getSecurityRules() {
        return rules;
    }
}
