package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;
import com.terminaltrainer.core.commands.CommandProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the 'help' command, which provides help information about available commands.
 */
public class HelpCommand implements Command {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Display help information about available commands";
    }

    @Override
    public String getHelpText() {
        return "Usage: help [COMMAND]\n" +
               "Display help information about available commands.\n\n" +
               "If COMMAND is specified, show detailed help for that command.\n" +
               "Otherwise, list all available commands with brief descriptions.\n\n" +
               "Examples:\n" +
               "  help       List all available commands\n" +
               "  help ls    Show detailed help for the 'ls' command";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        CommandProcessor commandProcessor = session.getCommandProcessor();
        
        if (args.length == 0) {
            // No arguments, list all commands
            return listAllCommands(commandProcessor);
        } else if (args.length == 1) {
            // One argument, show help for that command
            String commandName = args[0];
            return commandProcessor.getCommandHelp(commandName);
        } else {
            return "help: too many arguments";
        }
    }

    /**
     * Lists all available commands with brief descriptions.
     *
     * @param commandProcessor The command processor
     * @return A formatted list of commands
     */
    private String listAllCommands(CommandProcessor commandProcessor) {
        Map<String, Command> commands = commandProcessor.getCommands();
        
        if (commands.isEmpty()) {
            return "No commands available.";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("Available commands:\n");
        
        // Group commands by category
        List<Command> navigationCommands = new ArrayList<>();
        List<Command> fileOperationCommands = new ArrayList<>();
        List<Command> textViewingCommands = new ArrayList<>();
        List<Command> helpCommands = new ArrayList<>();
        List<Command> systemInfoCommands = new ArrayList<>();
        List<Command> educationalCommands = new ArrayList<>();
        List<Command> otherCommands = new ArrayList<>();
        
        for (Command command : commands.values()) {
            String name = command.getName();
            
            // Categorize commands
            if (name.equals("cd") || name.equals("ls") || name.equals("pwd")) {
                navigationCommands.add(command);
            } else if (name.equals("touch") || name.equals("mkdir") || name.equals("rm") || 
                       name.equals("cp") || name.equals("mv")) {
                fileOperationCommands.add(command);
            } else if (name.equals("cat") || name.equals("more") || name.equals("less")) {
                textViewingCommands.add(command);
            } else if (name.equals("help") || name.equals("man")) {
                helpCommands.add(command);
            } else if (name.equals("whoami") || name.equals("date") || name.equals("clear")) {
                systemInfoCommands.add(command);
            } else if (name.equals("tutorial") || name.equals("challenge") || name.equals("achievements")) {
                educationalCommands.add(command);
            } else {
                otherCommands.add(command);
            }
        }
        
        // Add navigation commands
        if (!navigationCommands.isEmpty()) {
            result.append("\nNavigation:\n");
            for (Command command : navigationCommands) {
                result.append(String.format("  %-10s %s\n", command.getName(), command.getDescription()));
            }
        }
        
        // Add file operation commands
        if (!fileOperationCommands.isEmpty()) {
            result.append("\nFile Operations:\n");
            for (Command command : fileOperationCommands) {
                result.append(String.format("  %-10s %s\n", command.getName(), command.getDescription()));
            }
        }
        
        // Add text viewing commands
        if (!textViewingCommands.isEmpty()) {
            result.append("\nText Viewing:\n");
            for (Command command : textViewingCommands) {
                result.append(String.format("  %-10s %s\n", command.getName(), command.getDescription()));
            }
        }
        
        // Add help commands
        if (!helpCommands.isEmpty()) {
            result.append("\nHelp System:\n");
            for (Command command : helpCommands) {
                result.append(String.format("  %-10s %s\n", command.getName(), command.getDescription()));
            }
        }
        
        // Add system info commands
        if (!systemInfoCommands.isEmpty()) {
            result.append("\nSystem Information:\n");
            for (Command command : systemInfoCommands) {
                result.append(String.format("  %-10s %s\n", command.getName(), command.getDescription()));
            }
        }
        
        // Add educational commands
        if (!educationalCommands.isEmpty()) {
            result.append("\nEducational Features:\n");
            for (Command command : educationalCommands) {
                result.append(String.format("  %-10s %s\n", command.getName(), command.getDescription()));
            }
        }
        
        // Add other commands
        if (!otherCommands.isEmpty()) {
            result.append("\nOther Commands:\n");
            for (Command command : otherCommands) {
                result.append(String.format("  %-10s %s\n", command.getName(), command.getDescription()));
            }
        }
        
        result.append("\nType 'help COMMAND' for more information about a specific command.");
        
        return result.toString();
    }
}