package cz.nekula.avitech;

public interface UserCommand {
    void apply(UserService userService);
}
