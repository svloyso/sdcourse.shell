package ru.spbau.sdcourse;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.spbau.sdcourse.Commands.*;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by svloyso on 21.09.16.
 */
public class ShellTest {
    private String resDir = "./src/test/resources";
    private String testfile1 = "test1.txt";
    private String testfile2 = "test2.txt";
    private String execFile = "exec";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    ExecutorService executor = Executors.newCachedThreadPool();

    class TestReader extends Command {
        List<String> inputLines;

        public TestReader(List<String> inputLines) {
            super(null, null, null);
            this.inputLines = inputLines == null ? null : new ArrayList<>(inputLines);
        }
        @Override
        protected void start() throws Exception {
            if(inputLines == null) return;
            inputLines.stream().forEach(this::putLine);
        }
    }

    class TestChecker extends Command {
        List<String> expectedLines;

        public TestChecker(Command prev, List<String> expectedLines) {
            super(prev, null, null);
            this.expectedLines = expectedLines == null ? null : new ArrayList<>(expectedLines);
        }

        @Override
        protected void start() throws Exception {
            if(expectedLines == null) assertNull(getLine());
            else expectedLines.stream().forEach(expected -> assertEquals(expected, getLine()));
        }
    }

    @Test(timeout=1000)
    public void assignmentTest() throws Exception {
        Map<String, String> env = new HashMap<>();
        Command assignment = new Assignment(null, Arrays.asList("X", "12345"), env);
        assignment.call();
        assertTrue(env.containsKey("X"));
        assertEquals("12345", env.get("X"));
    }


    @Test(timeout=1000)
    public void catTest() throws Exception {
        Cat cat = new Cat(null, Arrays.asList(Paths.get(resDir, testfile1).toString(), Paths.get(resDir, testfile2).toString()), Collections.emptyMap());
        TestChecker checker = new TestChecker(cat, Arrays.asList("test1", "test2", "abra", "cadabra"));
        cat.call();
        checker.call();
    }


    @Test(timeout=1000)
    public void echoTest() throws Exception {
        Echo echo = new Echo(null, Arrays.asList("abra", "cadabra"), null);
        TestChecker checker = new TestChecker(echo, Collections.singletonList("abra cadabra"));
        echo.call();
        checker.call();
    }


    @Test(timeout=1000)
    public void lsTest() throws Exception {
        Ls ls = new Ls(null, Collections.singletonList(resDir), null);
        TestChecker checker = new TestChecker(ls, Collections.singletonList(String.join(" ", testfile1, testfile2, execFile)));
        ls.call();
        checker.call();
    }


    @Test(timeout=1000)
    public void wcTest() throws Exception {
        TestReader reader = new TestReader(Arrays.asList("aba bcb", "cdc xxxx eeee", "222 3333 511 2 2"));
        Wc wc = new Wc(reader, Arrays.asList("-l", "-c", "-w", "-L"), null);
        TestChecker checker = new TestChecker(wc, Collections.singletonList("36\t10\t3\t16"));
        reader.call();
        wc.call();
        checker.call();
    }


    @Test(timeout=1000)
    public void argsTest() throws Exception {
        TestReader reader = new TestReader(Collections.singletonList(Paths.get(resDir, testfile2).toString()));
        Args args = new Args(reader, Arrays.asList("cat", Paths.get(resDir, testfile1).toString()), Collections.emptyMap());
        TestChecker checker = new TestChecker(args, Arrays.asList("test1", "test2", "abra", "cadabra"));
        reader.call();
        args.call();
        checker.call();
    }

    @Test(timeout=1000)
    public void externTest() throws Exception {
        TestReader reader = new TestReader(Arrays.asList("aaaa", "12345", "bbb", "12345", "cccc", "12345"));
        ExternalCommand cmd = new ExternalCommand(
                Paths.get(resDir, execFile).toAbsolutePath().toString(),
                reader, Collections.singletonList("12345"), null);
        TestChecker checker = new TestChecker(cmd, Arrays.asList("aaaa", "12345", "bbb", "12345", "cccc", "12345"));
        Future<Void> f1 = executor.submit(reader);
        Future<Void> f2 = executor.submit(cmd);
        Future<Void> f3 = executor.submit(checker);
        f1.get();
        f2.get();
        f3.get();
    }

    @Test(timeout=1000)
    public void grepTest() throws Exception {
        List<String> poem = Arrays.asList(
                "Mary had a little lamb",
                "Mary fried a lot of spam",
                "Jack ate a Spam sandwich",
                "Jill had a lamb spamwich");
        TestReader reader = new TestReader(poem);
        Grep grep = new Grep(reader, Collections.singletonList("spam"), null);
        TestChecker checker = new TestChecker(grep, Arrays.asList(poem.get(1), poem.get(3)));
        reader.call();
        grep.call();
        checker.call();

        reader = new TestReader(poem);
        grep = new Grep(reader, Arrays.asList("-i", "-w", "-A", "1", "sPaM"), null);
        checker = new TestChecker(grep, poem.subList(1, poem.size() - 1));
        reader.call();
        grep.call();
        checker.call();
    }

    @Test(timeout=1000)
    public void pipeTest() throws Exception {
        TestReader reader = new TestReader(Collections.singletonList(Paths.get(resDir, testfile1).toString()));
        Echo echo = new Echo(reader, Collections.singletonList(Paths.get(resDir, testfile1).toString()), null);
        Args args = new Args(echo, Collections.singletonList("cat"), Collections.emptyMap());
        Wc wc = new Wc(args, Collections.singletonList("-l"), null);
        TestChecker checker = new TestChecker(wc, Collections.singletonList("2"));
        List<Command> commands = Arrays.asList(reader, echo, args, wc, checker);
        List<Future<Void>> results = commands.stream().map(executor::submit).collect(Collectors.toList());
        for(Future<Void> f : results) f.get();
    }

    @Test(timeout = 1000)
    public void cdTest() throws Exception {
        temporaryFolder.newFile("fileName123");
        Cd cd = new Cd(null, Collections.singletonList(temporaryFolder.getRoot().getAbsolutePath()), null);
        Ls ls = new Ls(null, Collections.emptyList(), null);
        TestChecker checker = new TestChecker(ls, Collections.singletonList("fileName123"));
        cd.call();
        ls.call();
        checker.call();
    }
}