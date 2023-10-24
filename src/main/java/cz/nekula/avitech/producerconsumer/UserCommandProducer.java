package cz.nekula.avitech.producerconsumer;

import cz.nekula.avitech.UserCommand;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

/**
 * Produces events for user queue.
 */
public class UserCommandProducer {

    private final BlockingQueue<UserCommand> fifoQueue;

    public UserCommandProducer(BlockingQueue<UserCommand> fifoQueue) {
        this.fifoQueue = Objects.requireNonNull(fifoQueue);
    }

    public void produceCommand(UserCommand command) throws InterruptedException {
        fifoQueue.put(command);
    }

}
