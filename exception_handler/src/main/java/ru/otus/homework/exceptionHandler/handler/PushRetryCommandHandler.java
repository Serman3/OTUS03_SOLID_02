package ru.otus.homework.exceptionHandler.handler;

import ru.otus.homework.exceptionHandler.command.Command;
import ru.otus.homework.exceptionHandler.command.RetryThrowingCommand;

import java.util.concurrent.ConcurrentLinkedDeque;

public class PushRetryCommandHandler implements Command {

    private final Command command;

    private final ConcurrentLinkedDeque<Command> deque;

    public PushRetryCommandHandler(Command command, ConcurrentLinkedDeque<Command> deque) {
        this.command = command;
        this.deque = deque;
    }

    @Override
    public void execute() {
        deque.push(new RetryThrowingCommand(command));
    }
}