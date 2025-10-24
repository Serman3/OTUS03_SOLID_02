package ru.otus.homework.exceptionHandler.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.exceptionHandler.command.Command;
import ru.otus.homework.exceptionHandler.command.LoggingThrowingCommand;
import ru.otus.homework.exceptionHandler.command.RetryThrowingCommand;
import ru.otus.homework.exceptionHandler.command.RetryTwiceThrowingCommand;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerTest {

    private final static ConcurrentLinkedDeque<Command> QUEUE = new ConcurrentLinkedDeque<>();

    @BeforeEach
    void setUp() throws Throwable {
        Command illegalArgumentThrowingCommandMock = mock(Command.class);
        doThrow(IllegalArgumentException.class).when(illegalArgumentThrowingCommandMock).execute();

        ExceptionHandler.register(
                illegalArgumentThrowingCommandMock.getClass(),
                IllegalArgumentException.class,
                (cmd, ex) -> new RetryThrowingCommand(cmd)
        );

        QUEUE.push(illegalArgumentThrowingCommandMock);

        Command runtimeThrowingCommandMock = mock(Command.class);
        doThrow(RuntimeException.class).when(runtimeThrowingCommandMock).execute();

        ExceptionHandler.register(
                runtimeThrowingCommandMock.getClass(),
                RuntimeException.class,
                (cmd, ex) -> new LoggingThrowingCommand(ex)
        );

        QUEUE.push(runtimeThrowingCommandMock);

        Command fileNotFoundThrowingCommandMock = mock(Command.class);
        doThrow(FileNotFoundException.class).when(fileNotFoundThrowingCommandMock).execute();

        ExceptionHandler.register(
                fileNotFoundThrowingCommandMock.getClass(),
                FileNotFoundException.class,
                (cmd, ex) -> new RetryTwiceThrowingCommand(cmd)
        );

        QUEUE.push(fileNotFoundThrowingCommandMock);
    }

    @Test
    public void handleTest() {
        while (!QUEUE.isEmpty()) {
            Command command = QUEUE.poll();
            try {
                command.execute();
            } catch (Throwable e) {
                try {
                    ExceptionHandler.handle(command, e).execute();
                } catch (Throwable t) {
                    System.out.println("Очередная ошибка");
                }
            }
        }
    }
}