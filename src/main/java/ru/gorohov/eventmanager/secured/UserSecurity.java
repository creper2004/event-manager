package ru.gorohov.eventmanager.secured;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.gorohov.eventmanager.user.domain.UserRole;

import java.util.Collection;
import java.util.List;

@Data
@Builder
public class UserSecurity implements UserDetails {


    private Long id;

    private String login;

    private Integer age;

    private UserRole role;

    private String passwordHash;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return login;
    }


}