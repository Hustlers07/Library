package com.library.user_management.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SecurityRule {

    private String pattern;
    private String access;

}
