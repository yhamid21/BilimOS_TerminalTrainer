package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;

/**
 * Implementation of the 'pwd' command, which prints the current working directory.
 */
public class PwdCommand implements Command {
    @Override
    public String getName() {
        return "pwd";
    }

    @Override
    public String getDescription() {
        return "Print the current working directory";
    }

    @Override
    public String getHelpText() {
        return "Usage: pwd\n" +
               "Print the full filename of the current working directory.\n\n" +
               "Example:\n" +
               "  pwd    Displays the current directory path";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        if (args.length > 0) {
            if (args[0].equals("--help") || args[0].equals("-h")) {
                return getHelpText();
            }
            return "pwd: too many arguments";
        }
        
        return session.getCurrentDirectory();
    }
}