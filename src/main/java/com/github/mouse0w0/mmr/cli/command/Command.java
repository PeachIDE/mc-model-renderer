package com.github.mouse0w0.mmr.cli.command;

public interface Command {
    void handle(String command, Args args);
}
