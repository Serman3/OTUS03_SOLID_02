package ru.otus.homework.exceptionHandler.command;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryTwiceThrowingCommand implements Command {

    private final Command throwingCommand;

    public RetryTwiceThrowingCommand(Command throwingCommand) {
        this.throwingCommand = throwingCommand;
    }

    @Override
    public void execute() throws Throwable {
        for (int i = 1; i < 3; i++) {
            try {
                log.info("Попытка номер {} выполнить повторно команду", i);
                throwingCommand.execute();
            } catch (Throwable throwable) {
                log.info("Попытка номер {} завершилась не удачей", i);
                if(i == 2) throw throwable;
            }
        }
    }

}
