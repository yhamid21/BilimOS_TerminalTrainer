package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;
import com.terminaltrainer.core.filesystem.VirtualFileSystemNode;

/**
 * Implementation of the 'cd' command, which changes the current directory.
 */
public class CdCommand implements Command {
    @Override
    public String getName() {
        return "cd";
    }

    @Override
    public String getDescription() {
        return "Change the current directory";
    }

    @Override
    public String getHelpText() {
        return "Usage: cd [DIRECTORY]\n" +
               "Change the current working directory to DIRECTORY.\n\n" +
               "If no directory is specified, changes to the user's home directory.\n" +
               "Special paths:\n" +
               "  ..    Parent directory\n" +
               "  .     Current directory\n" +
               "  ~     Home directory\n\n" +
               "Examples:\n" +
               "  cd        Change to home directory\n" +
               "  cd ..     Change to parent directory\n" +
               "  cd /bin   Change to /bin directory";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        String targetPath;
        
        if (args.length == 0) {
            // No arguments, go to home directory
            targetPath = "/home/" + session.getUsername();
        } else if (args.length > 1) {
            return "cd: too many arguments";
        } else {
            targetPath = args[0];
            
            // Handle ~ as home directory
            if (targetPath.equals("~") || targetPath.startsWith("~/")) {
                String homePath = "/home/" + session.getUsername();
                if (targetPath.equals("~")) {
                    targetPath = homePath;
                } else {
                    targetPath = homePath + targetPath.substring(1);
                }
            }
        }
        
        // Resolve the target path
        VirtualFileSystemNode node = session.getFileSystem().resolvePath(targetPath, session.getCurrentDirectory());
        
        if (node == null) {
            return "cd: " + targetPath + ": No such file or directory";
        }
        
        if (!node.isDirectory()) {
            return "cd: " + targetPath + ": Not a directory";
        }
        
        // Change the current directory
        session.setCurrentDirectory(node.getPath());
        return ""; // cd command doesn't produce output on success
    }
}