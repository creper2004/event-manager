package ru.gorohov.eventmanager.user.api;

import lombok.*;
import ru.gorohov.eventmanager.user.domain.UserRole;

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
