package ru.gorohov.eventmanager.secured.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gorohov.eventmanager.secured.convector.UserConvector;
import ru.gorohov.eventmanager.secured.user.UserDomain;
import ru.gorohov.eventmanager.secured.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserConvector userConvector;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, UserConvector userConvector) {
        this.userRepository = userRepository;
        this.userConvector = userConvector;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            var userDomain = getUserByLogin(username);
            return new User(username, userDomain.getPasswordHash(),
                    List.of(new SimpleGrantedAuthority(userDomain.getRole().name())));
        }
        catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

    }

    @Transactional
    public UserDomain saveUser(UserDomain userDomain) {
        log.info("Saving user {}", userDomain);
        if (isExistingUserByLogin(userDomain.getLogin())) {
            throw new EntityExistsException("Login " + userDomain.getLogin() + " already exists");
        }
        var toSave = userConvector.fromDomainToEntity(userDomain);
        var savedUser = userRepository.save(toSave);
        return userConvector.fromEntityToDomain(savedUser);
    }

    public boolean isExistingUserByLogin(String login) {
        log.info("Checking if user with login {} exists", login);
        return userRepository.existsByLogin(login);
    }


    public UserDomain getUserByLogin(String login) {
        log.info("Getting user with login {}", login);
        var user = userRepository.findByLogin(login)
                .orElseThrow(()->new EntityNotFoundException("User with login " + login + " not found"));
        return userConvector.fromEntityToDomain(user);
    }

    public UserDomain getUserById(Long id) {
        log.info("Getting user with id {}", id);
        var user = userRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("User with id " + id + " not found"));
        return userConvector.fromEntityToDomain(user);
    }


}
