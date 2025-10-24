package ru.otus.homework.exceptionHandler.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.exceptionHandler.command.Command;
import ru.otus.homework.exceptionHandler.command.LoggingThrowingCommand;
import ru.otus.homework.exceptionHandler.command.RetryThrowingCommand;
import ru.otus.homework.exceptionHandler.command.RetryTwiceThrowingCommand;

import java.io.FileNotFoundException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerTest {

    private final static ConcurrentLinkedDeque<Command> QUEUE = new ConcurrentLinkedDeque<>();

    @Test
    public void checkLoggingThrowingCommand() throws Throwable {
        Command runtimeThrowingCommandMock = mock(Command.class);
        doThrow(RuntimeException.class).when(runtimeThrowingCommandMock).execute();

        ExceptionHandler.register(
                runtimeThrowingCommandMock.getClass(),
                RuntimeException.class,
                (cmd, ex) -> new LoggingThrowingCommand(ex)
        );

        QUEUE.push(runtimeThrowingCommandMock);

        assertDoesNotThrow(this::readQueue);
    }

    @Test
    public void checkPushLoggingCommandHandler() throws Throwable {
        Command fileNotFoundThrowingCommandMock = mock(Command.class);
        doThrow(FileNotFoundException.class).when(fileNotFoundThrowingCommandMock).execute();

        ExceptionHandler.register(
                fileNotFoundThrowingCommandMock.getClass(),
                FileNotFoundException.class,
                (cmd, ex) -> new PushLoggingCommandHandler(ex, QUEUE)
        );

        try {
            fileNotFoundThrowingCommandMock.execute();
        } catch (Throwable e) {
            ExceptionHandler.handle(fileNotFoundThrowingCommandMock, e).execute();
        }

        assertAll(
                () -> assertEquals(1, QUEUE.size()),
                () -> assertEquals(QUEUE.getFirst().getClass(), LoggingThrowingCommand.class)
        );
    }

    @Test
    public void checkRetryThrowingCommand() throws Throwable {
        Command illegalArgumentThrowingCommandMock = mock(Command.class);
        doThrow(IllegalArgumentException.class).when(illegalArgumentThrowingCommandMock).execute();

        ExceptionHandler.register(
                illegalArgumentThrowingCommandMock.getClass(),
                IllegalArgumentException.class,
                (cmd, ex) -> new RetryThrowingCommand(cmd)
        );

        QUEUE.push(illegalArgumentThrowingCommandMock);

        assertThrows(IllegalArgumentException.class, this::readQueue);
    }

    @Test
    public void chetPushRetryThrowingHandler() throws Throwable {
        Command fileNotFoundThrowingCommandMock = mock(Command.class);
        doThrow(FileNotFoundException.class).when(fileNotFoundThrowingCommandMock).execute();

        ExceptionHandler.register(
                fileNotFoundThrowingCommandMock.getClass(),
                FileNotFoundException.class,
                (cmd, ex) -> new PushRetryCommandHandler(cmd, QUEUE)
        );

        try {
            fileNotFoundThrowingCommandMock.execute();
        } catch (Throwable e) {
            ExceptionHandler.handle(fileNotFoundThrowingCommandMock, e).execute();
        }

        assertAll(
                () -> assertEquals(1, QUEUE.size()),
                () -> assertEquals(QUEUE.getFirst().getClass(), RetryThrowingCommand.class)
        );
    }

    @Test
    public void checkLoggingRetryingCommandHandler() throws Throwable {
        Command runtimeThrowingCommandMock = mock(Command.class);
        doThrow(RuntimeException.class).when(runtimeThrowingCommandMock).execute();

        ExceptionHandler.register(
                runtimeThrowingCommandMock.getClass(),
                RuntimeException.class,
                (cmd, ex) -> new LoggingRetryingCommandHandler(ex, cmd)
        );

        QUEUE.push(runtimeThrowingCommandMock);

        assertDoesNotThrow(this::readQueue);
    }

    @Test
    public void checkRetryTwiceThrowingCommand() throws Throwable {
        Command concurrentModificationThrowingCommandMock = mock(Command.class);
        doThrow(ConcurrentModificationException.class).when(concurrentModificationThrowingCommandMock).execute();

        ExceptionHandler.register(
                concurrentModificationThrowingCommandMock.getClass(),
                ConcurrentModificationException.class,
                (cmd, ex) -> new RetryTwiceThrowingCommand(cmd)
        );

        QUEUE.push(concurrentModificationThrowingCommandMock);

        assertThrows(ConcurrentModificationException.class, this::readQueue);
    }

    public void readQueue() throws Throwable {
        while (!QUEUE.isEmpty()) {
            Command command = QUEUE.poll();
            try {
                command.execute();
            } catch (Throwable e) {
                ExceptionHandler.handle(command, e).execute();
            }
        }
    }
}