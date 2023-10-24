package cz.nekula.avitech.userservice;

import cz.nekula.avitech.UserDao;
import cz.nekula.avitech.UserService;
import cz.nekula.avitech.model.User;

import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = Objects.requireNonNull(userDao);
    }

    @Override
    public void add(User user) {
        Objects.requireNonNull(user);
        userDao.add(user);
    }

    @Override
    public void deleteAll() {
        userDao.deleteAll();
    }

    @Override
    public void printAll() {
        List<User> users = userDao.findAll();

        System.out.println("=============================================");
        System.out.println("Printing all the users currently in database:");
        System.out.println("=============================================");
        for (User user : users) {
            System.out.println(user);
        }
        System.out.println("=============================================");
    }
}
