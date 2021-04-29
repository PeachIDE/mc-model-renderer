package com.github.mouse0w0.mmr.cli.command;

public class WindowCommand implements Command {
    @Override
    public void handle(String command, Args args) {
        switch (args.getAsLowerCase(0)) {
            case "show":

                break;
            case "hide":

                break;
        }
    }
}
