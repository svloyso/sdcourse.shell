package ru.spbau.sdcourse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        String input;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("> ");
            System.out.flush();
            while((input = reader.readLine()) != null) {
                Shell.processLine(input);
                System.out.print("> ");
                System.out.flush();
            }
        } catch (IOException e) {
            /*EMPTY*/
        } catch (ExitException e) {
            System.out.println("Bye!");
            System.exit(0);
        }
    }
}
