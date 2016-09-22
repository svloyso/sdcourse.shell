package ru.spbau.sdcourse.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Args is a command, which transfer input as arguments of given command.
 * Created by svloyso on 21.09.16.
 */
@BuiltinCommand(name="args")
public class Args extends Command {
    public Args(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        if (arguments.size() < 1) throw new Exception("args: no command was given");
        String commandName = arguments.get(0);

        ArrayList<String> newArgs = new ArrayList<>();
        String line;
        while((line = getLine()) != null) {
            newArgs.add(line);
        }

        Command cmd = CommandFactory.createCommand(commandName, Stream.concat(arguments.stream().skip(1), newArgs.stream()).collect(Collectors.toList()), this, environment);
        changeInput(cmd);

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Void> result = executor.submit(cmd);

        String input = getLine();
        do {
            putLine(input);
        } while((input = getLine()) != null);
        result.get();
    }
}
