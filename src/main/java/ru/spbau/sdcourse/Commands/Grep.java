package ru.spbau.sdcourse.Commands;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple grep implementation.
 * Keys supported:
 * -w for search only whole words
 * -i for case insensitive search
 * -A N for putting N lines after match
 * Created by svloyso on 22.09.16.
 */
@BuiltinCommand(name="grep")
public class Grep extends Command {
    public Grep(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        boolean ignoreCase = false;
        boolean wholeWords = false;
        int afterMatch = 0;
        if(arguments == null) throw new Exception("grep: no pattern given");
        for(int i = 0; i < arguments.size() - 1; ++i) {
            String arg = arguments.get(i);
            switch(arg) {
                case "-i":
                    ignoreCase = true;
                    break;
                case "-w":
                    wholeWords = true;
                    break;
                case "-A":
                    if(i == arguments.size() - 2 || !arguments.get(i + 1).matches("\\d+")) throw new Exception("grep: parameter for -A not given");
                    afterMatch = Integer.valueOf(arguments.get(i + 1));
                    i += 1;
                    break;
                default:
                    throw new Exception("grep: invalid key " + arg);
            }
        }
        String stringPattern = arguments.get(arguments.size() - 1);
        if(stringPattern.startsWith("-")) throw new Exception("grep: search pattern not given");

        if(wholeWords) stringPattern = "\\b" + stringPattern + "\\b";
        Pattern pattern = Pattern.compile(stringPattern, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
        String line;
        int toPrintAfter = -1;
        while((line = getLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if(matcher.find()) {
                toPrintAfter = afterMatch;
            }
            if(toPrintAfter >= 0) {
                putLine(line);
                toPrintAfter -= 1;
            }
        }
    }
}
