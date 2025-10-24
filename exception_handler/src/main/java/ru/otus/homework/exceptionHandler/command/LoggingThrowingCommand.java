package ru.otus.homework.exceptionHandler.command;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingThrowingCommand implements Command {

    private final Throwable throwable;

    public LoggingThrowingCommand(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void execute() {
        log.warn("Logging exception", throwable);
    }
}
