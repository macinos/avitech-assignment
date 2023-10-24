package cz.nekula.avitech.usercommands;

import cz.nekula.avitech.UserCommand;
import cz.nekula.avitech.model.User;
import cz.nekula.avitech.UserService;

import java.util.Objects;

public class AddCommand implements UserCommand {

    private final User newUser;

    public AddCommand(User newUser) {
        this.newUser = Objects.requireNonNull(newUser);
    }

    @Override
    public void apply(UserService userService) {
        userService.add(newUser);
    }
}
