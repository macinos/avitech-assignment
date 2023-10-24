package cz.nekula.avitech.producerconsumer;

import cz.nekula.avitech.UserCommand;
import cz.nekula.avitech.UserService;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class UserCommandConsumer implements Runnable {

    private final BlockingQueue<UserCommand> fifoQueue;
    private final UserService userService;

    public UserCommandConsumer(BlockingQueue<UserCommand> fifoQueue, UserService userService) {
        this.fifoQueue = Objects.requireNonNull(fifoQueue);
        this.userService = Objects.requireNonNull(userService);
    }

    @Override
    public void run() {
        try {
            while (true) {
                UserCommand command = fifoQueue.take();
                System.out.printf("Consumer %s is taking next command from the queue...\n", this);
                command.apply(userService);
            }
        } catch (InterruptedException e) {
            System.out.printf("Consumer on thread %s got interrupted.\n", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
        System.out.println("Consumer stopped.");
    }
}
