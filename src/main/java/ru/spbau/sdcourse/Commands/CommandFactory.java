package ru.spbau.sdcourse.Commands;

import org.reflections.util.ClasspathHelper;

import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CommandFactory is factory for creating commands by name
 * Created by svloyso on 20.09.16.
 */
public class CommandFactory {

    /* Collect all subclasses of abstract class Command annotated with BuiltinCommand(name)
     * and register it in builtinCommand map.
     */

    static Map<String, Class<? extends Command>> builtinCommands;
    static {
        String packageName = Command.class.getPackage().getName();
        String packagePath = packageName.replace('.', '/');
        Set<URL> urls = Collections.singleton(ClasspathHelper.forClass(Command.class));

        builtinCommands = urls.stream()
                .map(url -> Paths.get(url.getPath(), packagePath))
                .flatMap(path -> Arrays.stream(path.toFile().list()))
                .filter(s -> s.endsWith(".class"))
                .map(s -> packageName + "." + s.substring(0, s.length() - 6))
                .map(s -> {
                    try {
                        return Class.forName(s);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }})
                .filter(c -> Command.class.isAssignableFrom(c) && c.isAnnotationPresent(BuiltinCommand.class))
                .collect(Collectors.toMap(c -> c.getAnnotation(BuiltinCommand.class).name(), c -> c.asSubclass(Command.class)));
    }

    /**
     * Method generates new command. If name of command was registered in BuiltinCommand annotation,
     * this annotated command will be created. Otherwise external command will be executed.
     * @param name specified name of builtin or external command
     * @param args list of arguments
     * @param prev command, which provide input
     * @param env environment
     * @return created command
     */
    public static Command createCommand(String name, List<String> args, Command prev, Map<String, String> env) {
        if (builtinCommands.containsKey(name)) {
            try {
                return builtinCommands.get(name).getConstructor(Command.class, List.class, Map.class).newInstance(prev, args, env);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ExternalCommand(name, prev, args, env);
        }
    }
}
