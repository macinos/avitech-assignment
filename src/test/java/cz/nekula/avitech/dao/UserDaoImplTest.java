package cz.nekula.avitech.dao;

import cz.nekula.avitech.UserDao;
import cz.nekula.avitech.model.User;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing UserDao with H2 in-memory database.
 */
class UserDaoImplTest {

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static JdbcDataSource dataSource;

    @BeforeAll
    public static void setUpDb() throws SQLException {
        RunScript.execute(JDBC_URL, USER, PASSWORD, "src/test/resources/databaseschema/susers.sql", null, false);
        createTestDataSource();
    }

    private static void createTestDataSource() {
        dataSource = new JdbcDataSource();
        dataSource.setURL(JDBC_URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
    }

    @AfterEach
    public void truncateDb() throws SQLException {
        RunScript.execute(JDBC_URL, USER, PASSWORD, "src/test/resources/databaseschema/truncate-susers.sql", null, false);
    }

    @AfterAll
    public static void shutdownDb() throws SQLException {
        dataSource.getConnection(USER, PASSWORD).createStatement().execute("SHUTDOWN");
    }

    @Test
    void add_fiveUsers_shouldReturnCorrectAmount() {
        UserDao userDao = new UserDaoImpl(dataSource);

        int userId = 0;
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));

        List<User> users = userDao.findAll();
        User thirdUser = users.get(2);

        assertEquals(5, users.size());
        assertEquals(3, thirdUser.id());
        assertEquals("Test Milan3", thirdUser.name());
    }

    @Test
    void findAll_emptyTable_shouldGetEmptyList() {
        UserDao userDao = new UserDaoImpl(dataSource);

        List<User> users = userDao.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void findAll_tableWithOneUser_shouldGetCorrectUser() {
        UserDao userDao = new UserDaoImpl(dataSource);

        String uuid = UUID.randomUUID().toString();
        User newUser = new User(1, uuid, "Test Milan");
        userDao.add(newUser);

        List<User> users = userDao.findAll();
        User user = users.get(0);

        assertEquals(1, users.size());
        assertEquals(1, user.id());
        assertEquals(uuid, user.guid());
        assertEquals("Test Milan", user.name());
    }

    @Test
    void deleteAll_addThreeUsers_shouldReturnEmpty_AfterDeletingAll() {
        UserDao userDao = new UserDaoImpl(dataSource);

        int userId = 0;
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));
        userDao.add(new User(++userId, UUID.randomUUID().toString(), "Test Milan"+userId));

        List<User> users = userDao.findAll();
        assertEquals(3, users.size());

        userDao.deleteAll();

        List<User> usersAfterDeletion = userDao.findAll();
        assertTrue(usersAfterDeletion.isEmpty());
    }

}