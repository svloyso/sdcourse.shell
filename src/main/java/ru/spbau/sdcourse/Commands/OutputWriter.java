package ru.spbau.sdcourse.Commands;

/**
 * Fake command for putting output to console
 * Created by svloyso on 20.09.16.
 */
public class OutputWriter extends Command {

    public OutputWriter(Command prev) {
        super(prev, null, null);
    }

    @Override
    protected void start() throws Exception {
        String line;
        while((line = getLine()) != null) {
            System.out.println(line);
        }
    }
}
