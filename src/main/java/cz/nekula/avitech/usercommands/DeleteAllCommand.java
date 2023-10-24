package cz.nekula.avitech.usercommands;

import cz.nekula.avitech.UserCommand;
import cz.nekula.avitech.UserService;

public class DeleteAllCommand implements UserCommand {

    @Override
    public void apply(UserService userService) {
        userService.deleteAll();
    }
}
