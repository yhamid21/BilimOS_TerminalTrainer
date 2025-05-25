package com.terminaltrainer.core;

import com.terminaltrainer.core.filesystem.VirtualFileSystem;
import com.terminaltrainer.core.commands.CommandProcessor;
import com.terminaltrainer.education.TutorialManager;


public class TerminalSession {
    private final VirtualFileSystem fileSystem;
    private final CommandProcessor commandProcessor;
    private final TutorialManager tutorialManager;
    private String currentDirectory;
    private String username;
    private String hostname;
    private final CommandHistory commandHistory;


    public TerminalSession() {
        this.fileSystem = new VirtualFileSystem();
        this.commandProcessor = new CommandProcessor(this);
        this.tutorialManager = new TutorialManager(this);
        this.currentDirectory = "/home/user";
        this.username = "user";
        this.hostname = "terminaltrainer";
        this.commandHistory = new CommandHistory();
    }


    public String executeCommand(String commandInput) {
        commandHistory.addCommand(commandInput);
        return commandProcessor.processCommand(commandInput);
    }


    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }


    public String getPrompt() {
        String displayPath = currentDirectory;
        if (currentDirectory.equals("/home/" + username)) {
            displayPath = "~";
        } else if (currentDirectory.startsWith("/home/" + username + "/")) {
            displayPath = "~" + currentDirectory.substring(("/home/" + username).length());
        }

        return username + "@" + hostname + ":" + displayPath + "$ ";
    }


    public VirtualFileSystem getFileSystem() {
        return fileSystem;
    }


    public String getCurrentDirectory() {
        return currentDirectory;
    }


    public void setCurrentDirectory(String directory) {
        this.currentDirectory = directory;
    }


    public CommandHistory getCommandHistory() {
        return commandHistory;
    }


    public TutorialManager getTutorialManager() {
        return tutorialManager;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getHostname() {
        return hostname;
    }


    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
