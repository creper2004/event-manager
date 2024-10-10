package ru.gorohov.eventmanager.secured.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDomain {


    private Long id;

    private String login;

    private Integer age;

    private UserRole role;

    private String passwordHash;

}