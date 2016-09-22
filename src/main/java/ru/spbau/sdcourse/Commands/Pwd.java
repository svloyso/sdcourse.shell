package ru.spbau.sdcourse.Commands;

import java.util.List;
import java.util.Map;

/**
 * Prints current working directory
 * Created by svloyso on 21.09.16.
 */
@BuiltinCommand(name="pwd")
public class Pwd extends Command {
    public Pwd(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        putLine(System.getProperty("user.dir"));
    }
}
