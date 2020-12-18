package ru.spbau.sdcourse.Commands;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Class for external command, which will be created if there is no matches in builtin commands.
 * Created by svloyso on 21.09.16.
 */
public class ExternalCommand extends Command {
    String name;
    public ExternalCommand(String name, Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
        this.name = name;
    }

    @Override
    protected void start() throws Exception {
        Process proc = Runtime.getRuntime().exec(name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
        String line;
        while(proc.isAlive()) {
            if(hasInput()) {
                line = getLine();
                if(line == null) {
                    writer.close();
                } else {
                    writer.write(line + "\n");
                    writer.flush();
                }
            }
            if(reader.ready()) {
                line = reader.readLine();
                putLine(line);
            }
            Thread.yield();
        }
    }
}
