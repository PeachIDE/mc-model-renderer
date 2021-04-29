package com.github.mouse0w0.mmr.cli.command;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class Args {
    public static final Args EMPTY = new Args(new String[0]);

    private final String[] args;

    public Args(String[] args) {
        this.args = args;
    }

    public String get(int index) {
        return index < args.length ? args[index] : null;
    }

    public String getAsLowerCase(int index) {
        return index < args.length ? args[index].toLowerCase() : null;
    }

    public String getAsLowerCase(int index, Locale locale) {
        return index < args.length ? args[index].toLowerCase(locale) : null;
    }

    public int getAsInt(int index) {
        return getAsInt(index, 0);
    }

    public int getAsInt(int index, int defaultValue) {
        return index < args.length ? Integer.parseInt(args[index]) : defaultValue;
    }

    public Optional<String> optional(int index) {
        return index < args.length ? Optional.of(args[index]) : Optional.empty();
    }

    public int length() {
        return args.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(args);
    }
}
