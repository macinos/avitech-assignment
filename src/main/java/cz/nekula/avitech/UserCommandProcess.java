package cz.nekula.avitech;

import cz.nekula.avitech.producerconsumer.UserCommandConsumer;
import cz.nekula.avitech.producerconsumer.UserCommandProducer;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Class encapsulating the Producer & Consumer process of user commands.
 */
public class UserCommandProcess {

    private final ExecutorService executorService;
    private final UserCommandProducer commandProducer;

    /**
     * Creates the command process with a given queue size for commands. Only one consumer is used to consume the
     * commands in the order they have been added to the FIFO queue.
     * @param queueSize Size (capacity) of the command queue. Must be grater than zero.
     */
    public UserCommandProcess(int queueSize, UserService userService) {
        if (queueSize < 1) {
            throw new InvalidParameterException("Queue size must be greater than zero!");
        }
        Objects.requireNonNull(userService);

        //FIFO queue implementation
        BlockingQueue<UserCommand> userCommandsQueue = new LinkedBlockingQueue<>(queueSize);
        commandProducer = new UserCommandProducer(userCommandsQueue);
        UserCommandConsumer commandConsumer = new UserCommandConsumer(userCommandsQueue, userService);

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(commandConsumer);
    }

    /**
     * Send a command to the queue.
     * @param command User command to send.
     */
    public void produceCommand(UserCommand command) {
        try {
            commandProducer.produceCommand(command);
        } catch (InterruptedException e) {
            throw new RuntimeException("Cannot produce command for a queue!");
        }
    }

    /**
     * Shutdown with possible timeout to finish queue processing.
     * @param timeoutInMillis Time in milliseconds to wait before shutting down.
     */
    public void shutdown(int timeoutInMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeoutInMillis);
        } catch (InterruptedException e) {
            System.err.println("Cannot apply timeout because of an interrupt!");
        } finally {
            System.out.println("Shutting down...");
            executorService.shutdownNow();
        }
    }
}
