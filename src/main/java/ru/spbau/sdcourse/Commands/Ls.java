package ru.spbau.sdcourse.Commands;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple ls implementation.
 * Prints filenames for directories in args
 * If args is empty, prints files in current working dir
 * Created by svloyso on 21.09.16.
 */
@BuiltinCommand(name="ls")
public class Ls extends Command {
    public Ls(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        List<File> folders;
        if(arguments == null || arguments.size() == 0) {
            folders = Collections.singletonList(new File(System.getProperty("user.dir")));
        } else {
            folders = arguments.stream().map(File::new).collect(Collectors.toList());
        }
        String res = folders.stream()
                            .flatMap(f -> f.listFiles() == null ? Stream.of("ls: Can not open directory " + f.getName())
                                                                : Arrays.stream(f.listFiles()).map(File::getName))
                            .collect(Collectors.joining(" "));
        putLine(res);
    }
}
