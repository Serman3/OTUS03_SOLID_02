package ru.otus.homework.exceptionHandler.handler;

import ru.otus.homework.exceptionHandler.command.Command;
import ru.otus.homework.exceptionHandler.command.LoggingThrowingCommand;

import java.util.concurrent.ConcurrentLinkedDeque;

public class PushLoggingCommandHandler implements Command {

    private final Throwable throwable;

    private final ConcurrentLinkedDeque<Command> deque;

    public PushLoggingCommandHandler(Throwable throwable, ConcurrentLinkedDeque<Command> deque) {
        this.throwable = throwable;
        this.deque = deque;
    }

    @Override
    public void execute() {
        deque.push(new LoggingThrowingCommand(throwable));
    }
}
