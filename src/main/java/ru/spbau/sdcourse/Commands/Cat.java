package ru.spbau.sdcourse.Commands;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Concatenate files from args end put them to output.
 * Encoding will be received from LANG environment variable
 */
@BuiltinCommand(name="cat")
public class Cat extends Command {
    private Charset encoding;

    public Cat(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        encoding = environment.containsKey("LANG") && Charset.availableCharsets().containsKey(environment.get("LANG")) ? Charset.availableCharsets().get(environment.get("LANG")) : StandardCharsets.UTF_8;
        arguments.stream().flatMap((String file) -> {
            try {
                return Files.readAllLines(Paths.get(file), encoding).stream();
            } catch (IOException e) {
                return Stream.of("Can not open file " + file);
            }
        }).forEach(this::putLine);
    }
}
