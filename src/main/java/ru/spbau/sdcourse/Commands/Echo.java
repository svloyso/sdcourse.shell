package ru.spbau.sdcourse.Commands;

import java.util.List;
import java.util.Map;

/**
 * Simple echo command implementation
 */
@BuiltinCommand(name="echo")
public class Echo extends Command {
    public Echo(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        putLine(String.join(" ", arguments));
    }

}
