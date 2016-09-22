package ru.spbau.sdcourse.Commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Fake command for reading input from console
 */
public class InputReader extends Command {
    public InputReader() {
        super(null, null, null);
    }

    @Override
    protected void start() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while(!Thread.interrupted()) {
            if(br.ready()) {
                input = br.readLine();
                putLine(input);
            } else {
                Thread.yield();
            }
        }
    }

}
