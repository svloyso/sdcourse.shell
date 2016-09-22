package ru.spbau.sdcourse;

import ru.spbau.sdcourse.Commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.*;
import java.lang.StringBuilder;
import java.util.stream.Collectors;


/**
 * This class can parse and execute commands. Every command will be runned async and can process data on the fly.
 */
public class Shell {
    private static HashMap<String, String> enviroment = new HashMap<>();
    private static ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Method substitutes variables in text.
     * @param text
     */
    private static String preparse(String text) {
        Pattern pattern = Pattern.compile("([\\w_]*)");
        StringBuilder builder = new StringBuilder();
        int lastPos = 0;
        boolean inStrong = false, inWeak = false;

        int pos = 0;

        while(pos < text.length()) {
            if(text.charAt(pos) == '$' && !inStrong) {
                builder.append(text.substring(lastPos, pos));
                Matcher m = pattern.matcher(text.substring(pos + 1));
                String name = m.find() ? m.group(1) : "";
                if(enviroment.containsKey(name)) {
                    builder.append(enviroment.get(name));
                }
                pos += name.length() + 1;
                lastPos = pos;
                continue;
            }
            if(text.charAt(pos) == '"' && !inStrong) inWeak = !inWeak;
            if(text.charAt(pos) == '\'' && !inWeak)  inStrong = !inStrong;
            pos += 1;
        }

        builder.append(text.substring(lastPos));

        return builder.toString();
    }

    /**
     * Parse and execute command line.
     * @param text
     * @throws ExitException
     */
    public static void processLine(String text) throws ExitException {
        text = preparse(text);
        List<Cmd> cmds = CommandParser.parse(text);
        if(cmds == null) return;
        ArrayList<Command> commands = new ArrayList<>(cmds.size() + 2);
        commands.add(new InputReader());
        for(Cmd cmd : cmds) {
            commands.add(CommandFactory.createCommand(cmd.name(), cmd.args(), commands.get(commands.size() - 1), enviroment));
        }
        commands.add(new OutputWritter(commands.get(commands.size() - 1)));
        List<Future<Void>> results = commands.stream().map(executor::submit).collect(Collectors.toList());
        for(int i = 1; i < results.size(); ++i) {
            try {
                results.get(i).get();
            } catch (ExecutionException e) {
                try {
                    throw e.getCause();
                } catch(ExitException e1) {
                    throw e1;
                } catch (Throwable e1) {
                    System.out.println(e1.getMessage());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        results.get(0).cancel(true);
    }
}
