package com.terminaltrainer.core.commands;

import com.terminaltrainer.core.TerminalSession;


public interface Command {

    String getName();
    

    String getDescription();
    

    String getHelpText();
    

    String execute(String[] args, TerminalSession session);
}