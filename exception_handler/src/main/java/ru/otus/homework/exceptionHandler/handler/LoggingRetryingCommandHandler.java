package ru.otus.homework.exceptionHandler.handler;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.exceptionHandler.command.Command;
import ru.otus.homework.exceptionHandler.command.LoggingThrowingCommand;
import ru.otus.homework.exceptionHandler.command.RetryThrowingCommand;

@Slf4j
public class LoggingRetryingCommandHandler implements Command {

    private final Throwable throwable;

    private final Command throwingCommand;

    public LoggingRetryingCommandHandler(Throwable throwable, Command throwingCommand) {
        this.throwable = throwable;
        this.throwingCommand = throwingCommand;
    }

    @Override
    public void execute() {
        if (throwable != null) {
            log.info("Было выброшено исключение при первом вызове");
            try {
                new RetryThrowingCommand(throwingCommand).execute();
            } catch (Throwable e) {
                new LoggingThrowingCommand(throwable).execute();
            }
        }
    }

}
