package com.github.mouse0w0.mmr.cli.command;

import com.github.mouse0w0.mmr.cli.ModelRendererCLI;

public class ExitCommand implements Command {
    @Override
    public void handle(String command, Args args) {
        ModelRendererCLI.exit();
    }
}
