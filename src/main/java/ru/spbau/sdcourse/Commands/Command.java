package ru.spbau.sdcourse.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class, provides basic implementation for shell commands.
 * Method start() has to be implemented.
 * Every command implements producer-consumer pattern and can be runned in parallel and process input data on the fly.
 * Input data has to be gotten using getLine() method and sent to out using putLine().
 */
public abstract class Command implements Callable<Void> {
    private LinkedBlockingQueue<Optional<String>> inputQueue;
    private LinkedBlockingQueue<Optional<String>> outputQueue;

    protected List<String> arguments;
    protected Map<String, String> environment;

    /**
     * @param prev other command which provides input.
     * @param args arguments for command.
     * @param env environment variables.
     */
    public Command(Command prev, List<String> args, Map<String, String> env) {
        inputQueue = prev == null ? null : prev.outputQueue;
        outputQueue = new LinkedBlockingQueue<>();
        arguments = args == null ? null : new ArrayList<>(args);
        environment = env;
    }

    protected void changeInput(Command newInput) {
        inputQueue = newInput.outputQueue;
    }

    protected boolean hasInput() {
        return !inputQueue.isEmpty();
    }

    protected String getLine() {
        try {
            return inputQueue.take().orElse(null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void putLine(String chunk) {
        try {
            outputQueue.put(Optional.ofNullable(chunk));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    abstract protected void start() throws Exception;

    @Override
    final public Void call() throws Exception {
        start();
        putLine(null);
        return null;
    }
}
