package ru.spbau.sdcourse.Commands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Change environment directory.
 * Take only one argument - path to directory.
 * Throw exception if no such directory.
 */
@BuiltinCommand(name = "cd")
public class Cd extends Command {
    /**
     * Create instance of command Cd
     *
     * @param prev other command which provides input.
     * @param args arguments for command.
     * @param env  environment variables.
     */
    public Cd(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }


    /**
     * Change environment directory.
     * Take only one argument - path to directory.
     *
     * @throws Exception if no such directory.
     */
    @Override
    protected void start() throws Exception {
        if (arguments.size() != 1) {
            throw new RuntimeException("cd: invalid argument count");
        }
        Path path = Paths.get(System.getProperty("user.dir")).resolve(arguments.get(0)).normalize().toAbsolutePath();
        if (!path.toFile().exists()) {
            throw new RuntimeException("no such directory!" + path.toString());
        }
        if (!path.toFile().isDirectory()) {
            throw new RuntimeException("this is no directory! " + path.toString());
        }
        System.setProperty("user.dir", path.toString());
    }
}
