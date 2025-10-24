package ru.otus.homework.exceptionHandler.command;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryThrowingCommand implements Command {

    private final Command throwingCommand;

    public RetryThrowingCommand(Command throwingCommand) {
        this.throwingCommand = throwingCommand;
    }

    @Override
    public void execute() throws Throwable {
        log.info("Повторно выполняем команду, выбросившая исключение");
        throwingCommand.execute();
    }
}
