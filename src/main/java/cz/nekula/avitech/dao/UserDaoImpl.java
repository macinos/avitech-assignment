package cz.nekula.avitech.dao;

import cz.nekula.avitech.UserDao;
import cz.nekula.avitech.model.User;
import org.apache.commons.lang3.Validate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User Data Access Object utilizing a specified data source to access a SUSERS database.
 */
public class UserDaoImpl implements UserDao {

    private static final String TABLE_NAME = "SUSERS";
    private static final String ADD_USER_SQL = "INSERT INTO " + TABLE_NAME + " (USER_ID, USER_GUID, USER_NAME) VALUES (?,?,?)";
    private static final String PRINT_ALL_SQL = "SELECT * FROM " + TABLE_NAME;
    private static final String DELETE_ALL_SQL = "DELETE FROM " + TABLE_NAME;
    private DataSource dataSource;

    public UserDaoImpl(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public void add(User user) {
        System.out.printf("Adding user to DB: %s\n", user);

        Objects.requireNonNull(user);
        Validate.isTrue(user.id() > 0, "User ID must be a positive integer.");
        Validate.notBlank(user.guid());
        Validate.notBlank(user.name());

        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(ADD_USER_SQL)) {
                st.setInt(1, user.id());
                st.setString(2, user.guid());
                st.setString(3, user.name());

                int alteredRows = st.executeUpdate();
                if (alteredRows != 1) {
                    throw new RuntimeException("DAO Error: Bad amount of rows were added : " + alteredRows);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DAO Error: Failed adding a user!", e);
        }
    }

    @Override
    public List<User> findAll() {
        System.out.println("Searching for all users in the DB...");
        List<User> users = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(PRINT_ALL_SQL)) {

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    User user = resultSetToUser(rs);
                    users.add(user);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("DAO Error: Failed finding all users!", ex);
        }

        return users;
    }

    @Override
    public void deleteAll() {
        System.out.println("Deleting all users in the DB...");

        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(DELETE_ALL_SQL)) {
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DAO Error: Failed deleting all entries!");
        }
    }

    private User resultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("USER_ID"),
                rs.getString("USER_GUID"),
                rs.getString("USER_NAME")
        );
    }
}
