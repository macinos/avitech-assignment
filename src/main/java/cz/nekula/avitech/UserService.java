package cz.nekula.avitech;

import cz.nekula.avitech.model.User;

public interface UserService {

    /**
     * Adds a user to the database.
     * @param user User to add.
     */
    void add(User user);

    /**
     * Deletes all user entries.
     */
    void deleteAll();

    /**
     * Prints all user entries with all attributes.
     */
    void printAll();
}
