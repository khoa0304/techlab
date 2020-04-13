package textmining.service;

import textmining.config.Constants;
import textmining.domain.User;
import textmining.repository.UserRepository;
import textmining.security.AuthoritiesConstants;
import textmining.security.SecurityUtils;
import textmining.service.dto.UserDTO;

import io.github.jhipster.security.RandomUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .flatMap(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                return updateUser(user);
            })
            .doOnNext(user -> log.debug("Activated user: {}", user));
    }

    public Mono<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .flatMap(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return updateUser(user);
            });
    }

    public Mono<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(User::getActivated)
            .flatMap(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return updateUser(user);
            });
    }

    public Mono<User> registerUser(UserDTO userDTO, String password) {
        return userRepository.findOneByLogin(userDTO.getLogin().toLowerCase())
            .flatMap(existingUser -> {
                if (!existingUser.getActivated()) {
                    return userRepository.delete(existingUser);
                } else {
                    return Mono.error(new UsernameAlreadyUsedException());
                }
            })
            .then(userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()))
            .flatMap(existingUser -> {
                if (!existingUser.getActivated()) {
                    return userRepository.delete(existingUser);
                } else {
                    return Mono.error(new EmailAlreadyUsedException());
                }
            })
            .thenReturn(new User())
            .flatMap(newUser -> {
                newUser.setId(UUID.randomUUID().toString());
                String encryptedPassword = passwordEncoder.encode(password);
                newUser.setLogin(userDTO.getLogin().toLowerCase());
                // new user gets initially a generated password
                newUser.setPassword(encryptedPassword);
                newUser.setFirstName(userDTO.getFirstName());
                newUser.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    newUser.setEmail(userDTO.getEmail().toLowerCase());
                }
                newUser.setLangKey(userDTO.getLangKey());
                // new user is not active
                newUser.setActivated(false);
                // new user gets registration key
                newUser.setActivationKey(RandomUtil.generateActivationKey());
                Set<String> authorities = new HashSet<>();
                authorities.add(AuthoritiesConstants.USER);
                newUser.setAuthorities(authorities);
                return createUser(newUser)
                    .doOnNext(user -> log.debug("Created Information for User: {}", user));
            });
    }

    public Mono<User> createUser(UserDTO userDTO) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        user.setAuthorities(userDTO.getAuthorities());
        return createUser(user)
            .doOnNext(user1 -> log.debug("Created Information for User: {}", user1));
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     */
    public Mono<Void> updateUser(String firstName, String lastName, String email, String langKey) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                return updateUser(user);
            })
            .doOnNext(user -> log.debug("Changed Information for User: {}", user))
            .then();
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Mono<UserDTO> updateUser(UserDTO userDTO) {
        return userRepository.findById(userDTO.getId())
            .map(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                user.setAuthorities(userDTO.getAuthorities());
                return user;
            })
            .flatMap(this::updateUser)
            .doOnNext(user -> log.debug("Changed Information for User: {}", user))
            .map(UserDTO::new);
    }

    private Mono<User> updateUser(User user) {
        return userRepository.save(user);
    }

    private Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> deleteUser(String login) {
        return userRepository.findOneByLogin(login)
            .flatMap(user -> userRepository.delete(user).thenReturn(user))
            .doOnNext(user -> log.debug("Deleted User: {}", user))
            .then();
    }

    public Mono<Void> changePassword(String currentClearTextPassword, String newPassword) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    return Mono.error(new InvalidPasswordException());
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                return updateUser(user);
            })
            .doOnNext(user -> log.debug("Changed password for User: {}", user))
            .then();
    }

    public Flux<UserDTO> getAllManagedUsers() {
        return userRepository.findAll()
            .filter(user -> !Constants.ANONYMOUS_USER.equals(user.getLogin()))
            .map(UserDTO::new);
    }

    public Mono<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    public Mono<User> getUserWithAuthorities(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
    }

}
