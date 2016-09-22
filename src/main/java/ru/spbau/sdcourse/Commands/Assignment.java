package ru.spbau.sdcourse.Commands;

import java.util.List;
import java.util.Map;

/**
 * Fake command for enviroment updating
 * Created by svloyso on 21.09.16.
 */
@BuiltinCommand(name="$assignment")
public class Assignment extends Command {
    public Assignment(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        environment.put(arguments.get(0), arguments.get(1));
    }
}
