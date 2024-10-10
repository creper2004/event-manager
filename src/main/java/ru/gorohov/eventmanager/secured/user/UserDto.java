package ru.gorohov.eventmanager.secured.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String login;
    private Integer age;
    private UserRole role;
}
