package textmining.service;

import textmining.AbstractCassandraTest;
import textmining.JhipsterApp;
import textmining.config.Constants;
import textmining.domain.User;
import textmining.repository.UserRepository;
import textmining.service.dto.UserDTO;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link UserService}.
 */
@SpringBootTest(classes = JhipsterApp.class)
public class UserServiceIT extends AbstractCassandraTest {

    private static final String DEFAULT_LOGIN = "johndoe";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";

    private static final String DEFAULT_LASTNAME = "doe";

    private static final String DEFAULT_LANGKEY = "dummy";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void init() {
        userRepository.deleteAll().block();
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setLangKey(DEFAULT_LANGKEY);
    }

    @Test
    public void assertThatAnonymousUserIsNotGet() {
        user.setLogin(Constants.ANONYMOUS_USER);
        if (!userRepository.findOneByLogin(Constants.ANONYMOUS_USER).blockOptional().isPresent()) {
            userRepository.save(user).block();
        }
        final List<UserDTO> allManagedUsers = userService.getAllManagedUsers()
            .collectList().block();
        assertThat(allManagedUsers.stream()
            .noneMatch(user -> Constants.ANONYMOUS_USER.equals(user.getLogin())))
            .isTrue();
    }

}