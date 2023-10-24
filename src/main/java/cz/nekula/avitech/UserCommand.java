package cz.nekula.avitech;

/**
 * Interface for passing user commands from a producer to a consumer via queue.
 */
public interface UserCommand {
    void apply(UserService userService);
}
