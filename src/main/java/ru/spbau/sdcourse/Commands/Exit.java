package ru.spbau.sdcourse.Commands;

import ru.spbau.sdcourse.ExitException;

import java.util.List;
import java.util.Map;

/**
 * Exit command throws ExitException, which be catched in Main loop.
 * Created by svloyso on 21.09.16.
 */
@BuiltinCommand(name="exit")
public class Exit extends Command {

    public Exit(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        throw new ExitException();
    }
}
