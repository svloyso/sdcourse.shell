package ru.spbau.sdcourse.Commands;

import java.util.List;
import java.util.Map;

/**
 * Count words, lines, chars in input
 * Supported keys:
 * -l count lines
 * -w count words
 * -c count chars
 * -L longest line length
 * if no keys provided, command will be runned with -w -c -l keys by default
 * Created by svloyso on 20.09.16.
 */
@BuiltinCommand(name="wc")
public class Wc extends Command {
    boolean countLines = true;
    boolean countWords = true;
    boolean countChars = true;
    boolean countLongest = false;

    public Wc(Command prev, List<String> args, Map<String, String> env) {
        super(prev, args, env);
    }

    @Override
    protected void start() throws Exception {
        if(arguments.size() > 0) {
            countLines = countWords = countChars = countLongest = false;
        }
        arguments.stream().forEach((String s) -> {
            switch (s) {
                case "-l":
                case "--lines":
                    countLines = true;
                    break;
                case "-w":
                case "--words":
                    countWords = true;
                    break;
                case "-c":
                case "--chars":
                    countChars = true;
                    break;
                case "-L":
                case "--max-line-length":
                    countLongest = true;
                    break;
                default:
                    throw new RuntimeException("wc: invalid command option " + s);
            }
        });

        int words = 0, lines = 0, chars = 0, longest = 0;

        String line;
        while((line = getLine()) != null) {
            lines += 1;
            longest = longest > line.length() ? longest : line.length();
            chars += line.length();
            words += line.split(" ").length;
        }

        String out = "";
        boolean first = true;
        if(countChars) {
            first = false;
            out += Integer.toString(chars);
        }
        if(countWords) {
            if(!first) out += "\t";
            if(first) first = false;
            out += Integer.toString(words);
        }
        if(countLines) {
            if(!first) out += "\t";
            if(first) first = false;
            out += Integer.toString(lines);
        }
        if(countLongest) {
            if(!first) out += "\t";
            if(first) first = false;
            out += Integer.toString(longest);
        }
        putLine(out);
    }
}
