package com.github.mouse0w0.mmr.cli;

import com.github.mouse0w0.mmr.cli.command.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelRendererCLI {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");

    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();

    private static final AtomicBoolean aliveFlag = new AtomicBoolean(true);

    public static void main(String[] args) {
        initialize();
        waitCommand();
    }

    private static void initialize() {
        COMMAND_MAP.put("exit", new ExitCommand());
        COMMAND_MAP.put("window", new WindowCommand());
        COMMAND_MAP.put("offscreen", new OffScreenCommand());
    }

    private static void waitCommand() {
        Scanner scanner = new Scanner(System.in);
        while (aliveFlag.get()) {
            String line = scanner.nextLine();
            String name;
            Args args;
            Matcher matcher = WHITESPACE_PATTERN.matcher(line);
            if (matcher.matches()) {
                int firstWhitespace = matcher.start();
                name = line.substring(0, firstWhitespace).toLowerCase(Locale.ROOT);
                args = new Args(WHITESPACE_PATTERN.split(line.substring(firstWhitespace + 1)));
            } else {
                name = line.toLowerCase(Locale.ROOT);
                args = Args.EMPTY;
            }

            Command command = COMMAND_MAP.get(name);
            if (command != null) {
                command.handle(name, args);
            }
        }
    }

    public static void exit() {
        aliveFlag.set(false);
    }
}
