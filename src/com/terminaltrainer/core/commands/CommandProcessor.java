package com.terminaltrainer.core.commands;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.impl.CatCommand;
import com.terminaltrainer.core.commands.impl.CdCommand;
import com.terminaltrainer.core.commands.impl.HelpCommand;
import com.terminaltrainer.core.commands.impl.LsCommand;
import com.terminaltrainer.core.commands.impl.MkdirCommand;
import com.terminaltrainer.core.commands.impl.PwdCommand;
import com.terminaltrainer.core.commands.impl.TouchCommand;
import com.terminaltrainer.core.commands.impl.TutorialCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CommandProcessor {
    private final Map<String, Command> commands;
    private final TerminalSession session;


    public CommandProcessor(TerminalSession session) {
        this.session = session;
        this.commands = new HashMap<>();
        registerCommands();
    }

    private void registerCommands() {

        registerCommand(new CdCommand());
        registerCommand(new LsCommand());
        registerCommand(new PwdCommand());

        registerCommand(new TouchCommand());
        registerCommand(new MkdirCommand());

        registerCommand(new CatCommand());

        registerCommand(new HelpCommand());

        registerCommand(new TutorialCommand());
    }


    private void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }


    public String processCommand(String commandInput) {
        if (commandInput == null || commandInput.trim().isEmpty()) {
            return "";
        }

        String[] parts = commandInput.trim().split("\\s+");
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        if (args.length > 0 && (args[0].equals("--help") || args[0].equals("-h"))) {
            return getCommandHelp(commandName);
        }

        Command command = commands.get(commandName);

        if (command == null) {
            return "Command not found: " + commandName + ". Type 'help' for a list of available commands.";
        }

        try {
            return command.execute(args, session);
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }


    public String getCommandHelp(String commandName) {
        Command command = commands.get(commandName);

        if (command == null) {
            return "No help available for unknown command: " + commandName;
        }

        return command.getHelpText();
    }


    public Map<String, Command> getCommands() {
        return new HashMap<>(commands);
    }
}
