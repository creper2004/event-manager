package ru.gorohov.eventmanager.secured.initial;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.secured.service.UserService;
import ru.gorohov.eventmanager.secured.user.UserDomain;
import ru.gorohov.eventmanager.secured.user.UserRole;

@Component
public class UserInitializer {


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

    }

    @PostConstruct
    public void initialize() {
        createUserIfNotExists("admin", "admin", UserRole.ADMIN);
        createUserIfNotExists("user", "user", UserRole.USER);
    }

    public void createUserIfNotExists(String username, String password, UserRole userRole) {

        if (userService.isExistingUserByLogin(username)) {
            return;
        }
        else {
            var passwordHash = passwordEncoder.encode(password);
            var user = UserDomain.builder()
                    .id(null)
                    .age(20)
                    .role(userRole)
                    .login(username)
                    .passwordHash(passwordHash)
                    .build();
            userService.saveUser(user);
        }

    }

}