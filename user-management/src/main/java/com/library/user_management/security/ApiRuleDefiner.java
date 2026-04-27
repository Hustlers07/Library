package com.library.user_management.security;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ApiRuleDefiner {


    private final List<SecurityRule> rules = List.of(
        new SecurityRule("/user-management/api/auth/**", "permitAll"),
        new SecurityRule("/user-management/api/public/**", "permitAll"),
        new SecurityRule("/user-management/api/booking/**", "hasAnyRole(ADMIN,MEMBER,LIBRARIAN)"),
        new SecurityRule("/user-management/api/payment/**", "hasAnyRole(ADMIN,MEMBER,LIBRARIAN)"),
        new SecurityRule("/user-management/api/admin/**", "hasRole(ADMIN)"),
        new SecurityRule("/user-management/actuator/**", "permitAll")
    );

    public List<SecurityRule> getSecurityRules() {
        return rules;
    }
}
