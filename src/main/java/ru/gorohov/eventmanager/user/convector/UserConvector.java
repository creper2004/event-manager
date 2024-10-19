package ru.gorohov.eventmanager.user.convector;

import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.user.api.UserDto;
import ru.gorohov.eventmanager.user.db.UserEntity;
import ru.gorohov.eventmanager.user.domain.UserDomain;
import ru.gorohov.eventmanager.user.domain.UserRole;

@Component
public class UserConvector {

    public UserEntity fromDomainToEntity(UserDomain userDomain) {
        return UserEntity.builder()
                .id(userDomain.getId())
                .age(userDomain.getAge())
                .login(userDomain.getLogin())
                .passwordHash(userDomain.getPasswordHash())
                .role(userDomain.getRole().name())
                .build();
    }

    public UserDomain fromEntityToDomain(UserEntity userEntity) {
        return UserDomain.builder()
                .id(userEntity.getId())
                .age(userEntity.getAge())
                .login(userEntity.getLogin())
                .passwordHash(userEntity.getPasswordHash())
                .role(UserRole.valueOf(userEntity.getRole()))
                .build();
    }

    public UserDto fromDomainToDto(UserDomain userDomain) {
        return UserDto.builder()
                .id(userDomain.getId())
                .age(userDomain.getAge())
                .login(userDomain.getLogin())
                .role(userDomain.getRole())
                .build();
    }



}
