package textmining.repository;

import textmining.domain.User;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.springframework.data.cassandra.ReactiveResultSet;
import org.springframework.data.cassandra.ReactiveSession;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cassandra repository for the {@link User} entity.
 */
@Repository
public class UserRepository {

    private final ReactiveSession session;

    private final Validator validator;

    private final ReactiveCassandraTemplate cqlTemplate;

    private PreparedStatement findAllStmt;

    private PreparedStatement findOneByActivationKeyStmt;

    private PreparedStatement findOneByResetKeyStmt;

    private PreparedStatement insertByActivationKeyStmt;

    private PreparedStatement insertByResetKeyStmt;

    private PreparedStatement deleteByIdStmt;

    private PreparedStatement deleteByActivationKeyStmt;

    private PreparedStatement deleteByResetKeyStmt;

    private PreparedStatement findOneByLoginStmt;

    private PreparedStatement insertByLoginStmt;

    private PreparedStatement deleteByLoginStmt;

    private PreparedStatement findOneByEmailStmt;

    private PreparedStatement insertByEmailStmt;

    private PreparedStatement deleteByEmailStmt;

    private PreparedStatement truncateStmt;

    private PreparedStatement truncateByResetKeyStmt;

    private PreparedStatement truncateByLoginStmt;

    private PreparedStatement truncateByEmailStmt;

    public UserRepository(ReactiveCassandraTemplate cqlTemplate, ReactiveSession session, Validator validator) {
        this.session = session;
        this.validator = validator;
        this.cqlTemplate = cqlTemplate;

        findAllStmt = session.prepare("SELECT * FROM user").block();

        findOneByActivationKeyStmt = session.prepare(
            "SELECT id " +
                "FROM user_by_activation_key " +
                "WHERE activation_key = :activation_key").block();

        findOneByResetKeyStmt = session.prepare(
            "SELECT id " +
                "FROM user_by_reset_key " +
                "WHERE reset_key = :reset_key").block();

        insertByActivationKeyStmt = session.prepare(
            "INSERT INTO user_by_activation_key (activation_key, id) " +
                "VALUES (:activation_key, :id)").block();

        insertByResetKeyStmt = session.prepare(
            "INSERT INTO user_by_reset_key (reset_key, id) " +
                "VALUES (:reset_key, :id)").block();

        deleteByIdStmt = session.prepare(
            "DELETE FROM user " +
                "WHERE id = :id").block();

        deleteByActivationKeyStmt = session.prepare(
            "DELETE FROM user_by_activation_key " +
                "WHERE activation_key = :activation_key").block();

        deleteByResetKeyStmt = session.prepare(
            "DELETE FROM user_by_reset_key " +
                "WHERE reset_key = :reset_key").block();

        findOneByLoginStmt = session.prepare(
            "SELECT id " +
                "FROM user_by_login " +
                "WHERE login = :login").block();

        insertByLoginStmt = session.prepare(
            "INSERT INTO user_by_login (login, id) " +
                "VALUES (:login, :id)").block();

        deleteByLoginStmt = session.prepare(
            "DELETE FROM user_by_login " +
                "WHERE login = :login").block();

        findOneByEmailStmt = session.prepare(
            "SELECT id " +
                "FROM user_by_email " +
                "WHERE email     = :email").block();

        insertByEmailStmt = session.prepare(
            "INSERT INTO user_by_email (email, id) " +
                "VALUES (:email, :id)").block();

        deleteByEmailStmt = session.prepare(
            "DELETE FROM user_by_email " +
                "WHERE email = :email").block();

        truncateStmt = session.prepare("TRUNCATE user").block();

        truncateByResetKeyStmt = session.prepare("TRUNCATE user_by_reset_key").block();

        truncateByLoginStmt = session.prepare("TRUNCATE user_by_login").block();

        truncateByEmailStmt = session.prepare("TRUNCATE user_by_email").block();
    }

    public Mono<User> findById(String id) {
        return cqlTemplate.selectOneById(id, User.class)
            .map(user -> {
                if (user.getAuthorities() == null) {
                    user.setAuthorities(new HashSet<>());
                }
                return user;
            });
    }

    public Mono<User> findOneByActivationKey(String activationKey) {
        BoundStatement stmt = findOneByActivationKeyStmt.bind();
        stmt.setString("activation_key", activationKey);
        return findOneFromIndex(stmt);
    }

    public Mono<User> findOneByResetKey(String resetKey) {
        BoundStatement stmt = findOneByResetKeyStmt.bind();
        stmt.setString("reset_key", resetKey);
        return findOneFromIndex(stmt);
    }

    public Mono<User> findOneByEmailIgnoreCase(String email) {
        BoundStatement stmt = findOneByEmailStmt.bind();
        stmt.setString("email", email.toLowerCase());
        return findOneFromIndex(stmt);
    }

    public Mono<User> findOneByLogin(String login) {
        BoundStatement stmt = findOneByLoginStmt.bind();
        stmt.setString("login", login);
        return findOneFromIndex(stmt);
    }

    public Flux<User> findAll() {
        return cqlTemplate.select(findAllStmt.bind(), User.class)
            .map(user -> {
                if (user.getAuthorities() == null) {
                    user.setAuthorities(new HashSet<>());
                }
                return user;
            });
    }

    public Mono<User> save(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return this.findById(user.getId())
            .flatMapMany(oldUser -> {
                Flux<ReactiveResultSet> deleteOps = Flux.empty();
                if (!StringUtils.isEmpty(oldUser.getActivationKey()) && !oldUser.getActivationKey().equals(user.getActivationKey())) {
                    deleteOps.mergeWith(session.execute(deleteByActivationKeyStmt.bind().setString("activation_key", oldUser.getActivationKey())));
                }
                if (!StringUtils.isEmpty(oldUser.getResetKey()) && !oldUser.getResetKey().equals(user.getResetKey())) {
                    deleteOps.mergeWith(session.execute(deleteByResetKeyStmt.bind().setString("reset_key", oldUser.getResetKey())));
                }
                if (!StringUtils.isEmpty(oldUser.getLogin()) && !oldUser.getLogin().equals(user.getLogin())) {
                    deleteOps.mergeWith(session.execute(deleteByLoginStmt.bind().setString("login", oldUser.getLogin())));
                }
                if (!StringUtils.isEmpty(oldUser.getEmail()) && !oldUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                    deleteOps.mergeWith(session.execute(deleteByEmailStmt.bind().setString("email", oldUser.getEmail().toLowerCase())));
                }
                return deleteOps;
            })
            .then(Mono.defer(() -> {
                BatchStatement batch = new BatchStatement();
                batch.add(getInsertStatement(user));
                if (!StringUtils.isEmpty(user.getActivationKey())) {
                    batch.add(insertByActivationKeyStmt.bind()
                        .setString("activation_key", user.getActivationKey())
                        .setString("id", user.getId()));
                }
                if (!StringUtils.isEmpty(user.getResetKey())) {
                    batch.add(insertByResetKeyStmt.bind()
                        .setString("reset_key", user.getResetKey())
                        .setString("id", user.getId()));
                }
                batch.add(insertByLoginStmt.bind()
                    .setString("login", user.getLogin())
                    .setString("id", user.getId()));
                batch.add(insertByEmailStmt.bind()
                    .setString("email", user.getEmail().toLowerCase())
                    .setString("id", user.getId()));
                return session.execute(batch);
            }))
            .thenReturn(user);
    }

    private Insert getInsertStatement(User user) {
        CassandraConverter converter = cqlTemplate.getConverter();
        CassandraPersistentEntity<?> persistentEntity = converter.getMappingContext()
            .getRequiredPersistentEntity(user.getClass());
        Map<String, Object> toInsert = new LinkedHashMap<>();
        converter.write(user, toInsert, persistentEntity);
        Insert insert = QueryBuilder.insertInto(persistentEntity.getTableName().toCql());
        for (Map.Entry<String, Object> entry : toInsert.entrySet()) {
            insert.value(entry.getKey(), entry.getValue());
        }
        return insert;
    }

    public Mono<Void> delete(User user) {
        BatchStatement batch = new BatchStatement();
        batch.add(deleteByIdStmt.bind().setString("id", user.getId()));
        if (!StringUtils.isEmpty(user.getActivationKey())) {
            batch.add(deleteByActivationKeyStmt.bind().setString("activation_key", user.getActivationKey()));
        }
        if (!StringUtils.isEmpty(user.getResetKey())) {
            batch.add(deleteByResetKeyStmt.bind().setString("reset_key", user.getResetKey()));
        }
        batch.add(deleteByLoginStmt.bind().setString("login", user.getLogin()));
        batch.add(deleteByEmailStmt.bind().setString("email", user.getEmail().toLowerCase()));
        return session.execute(batch).then();
    }

    private Mono<User> findOneFromIndex(BoundStatement stmt) {
        return session.execute(stmt).flatMap(rs -> rs.rows().next())
            .map(row -> row.getString("id"))
            .flatMap(this::findById);
    }

    public Mono<Void> deleteAll() {
        return Flux.just(truncateStmt, truncateByEmailStmt, truncateByLoginStmt, truncateByResetKeyStmt)
            .map(PreparedStatement::bind)
            .flatMap(session::execute)
            .then();
    }
}
