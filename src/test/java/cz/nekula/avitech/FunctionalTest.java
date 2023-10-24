package cz.nekula.avitech;

import cz.nekula.avitech.dao.UserDaoImpl;
import cz.nekula.avitech.model.User;
import cz.nekula.avitech.usercommands.AddCommand;
import cz.nekula.avitech.usercommands.DeleteAllCommand;
import cz.nekula.avitech.usercommands.PrintAllCommand;
import cz.nekula.avitech.userservice.UserServiceImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class FunctionalTest {

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
    void userConsumerProducerFunctionalTest_valid_shouldFinishWithoutException() {
        UserDao userDao = new UserDaoImpl(dataSource);
        UserServiceImpl userService = new UserServiceImpl(userDao);

        User user = new User(1, "a1", "Robert");
        User user2 = new User(2, "a2", "Martin");
        AddCommand addCommand = new AddCommand(user);
        AddCommand addCommand2 = new AddCommand(user2);

        UserCommandProcess userCommandProcess = new UserCommandProcess(10, userService);
        userCommandProcess.produceCommand(addCommand);
        userCommandProcess.produceCommand(addCommand2);
        userCommandProcess.produceCommand(new PrintAllCommand());
        userCommandProcess.produceCommand(new DeleteAllCommand());
        userCommandProcess.produceCommand(new PrintAllCommand());

        userCommandProcess.shutdown(100);
    }

    @Test
    void userConsumerProducerFunctionalTest_validWithTimeDelays_shouldFinishWithoutException() throws InterruptedException {
        UserDao userDao = new UserDaoImpl(dataSource);
        UserServiceImpl userService = new UserServiceImpl(userDao);

        UserCommandProcess userCommandProcess = new UserCommandProcess(10, userService);
        User user = new User(1, "a1", "Robert");
        User user2 = new User(2, "a2", "Martin");
        AddCommand addCommand = new AddCommand(user);
        AddCommand addCommand2 = new AddCommand(user2);
        userCommandProcess.produceCommand(addCommand);
        TimeUnit.SECONDS.sleep(1);
        userCommandProcess.produceCommand(addCommand2);
        TimeUnit.SECONDS.sleep(2);

        userCommandProcess.produceCommand(new PrintAllCommand());
        TimeUnit.SECONDS.sleep(1);
        userCommandProcess.produceCommand(new DeleteAllCommand());
        TimeUnit.SECONDS.sleep(3);

        User user3 = new User(3, UUID.randomUUID().toString(), "Pavel");
        User user4 = new User(4, UUID.randomUUID().toString(), "Tibor");
        AddCommand addCommand3 = new AddCommand(user3);
        AddCommand addCommand4 = new AddCommand(user4);
        userCommandProcess.produceCommand(addCommand3);
        TimeUnit.SECONDS.sleep(1);
        userCommandProcess.produceCommand(addCommand4);
        TimeUnit.SECONDS.sleep(2);

        userCommandProcess.produceCommand(new PrintAllCommand());
        TimeUnit.SECONDS.sleep(1);

        userCommandProcess.shutdown(100);
    }
}