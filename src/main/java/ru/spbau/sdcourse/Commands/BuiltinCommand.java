package ru.spbau.sdcourse.Commands;

import java.lang.annotation.*;

/**
 * You can use this annotation to mark builtin commands, which has to be registered in shell.
 * Created by svloyso on 22.09.16.
 */

@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface BuiltinCommand {
    String name();
}

