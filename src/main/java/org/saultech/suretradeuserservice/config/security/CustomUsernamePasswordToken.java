package org.saultech.suretradeuserservice.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUsernamePasswordToken extends AbstractAuthenticationToken {
    private final String username;
    private final String password;


    public CustomUsernamePasswordToken(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
        this.username = username;
        this.password = password;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }
}
