package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;
import com.terminaltrainer.core.filesystem.VirtualFileSystem;
import com.terminaltrainer.core.filesystem.VirtualFileSystemNode;

/**
 * Implementation of the 'touch' command, which creates empty files.
 */
public class TouchCommand implements Command {
    @Override
    public String getName() {
        return "touch";
    }

    @Override
    public String getDescription() {
        return "Create empty files or update file timestamps";
    }

    @Override
    public String getHelpText() {
        return "Usage: touch [FILE]...\n" +
               "Create empty files or update file timestamps.\n\n" +
               "Examples:\n" +
               "  touch file.txt     Create an empty file named file.txt\n" +
               "  touch f1 f2 f3     Create three empty files: f1, f2, and f3";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        if (args.length == 0) {
            return "touch: missing file operand\nTry 'touch --help' for more information.";
        }
        
        if (args[0].equals("--help") || args[0].equals("-h")) {
            return getHelpText();
        }
        
        VirtualFileSystem fileSystem = session.getFileSystem();
        String currentDirectory = session.getCurrentDirectory();
        StringBuilder result = new StringBuilder();
        
        for (String path : args) {
            // Check if the file already exists
            VirtualFileSystemNode node = fileSystem.resolvePath(path, currentDirectory);
            
            if (node != null) {
                // File exists, update its timestamp
                node.updateModificationTime();
            } else {
                // File doesn't exist, create it
                boolean success = fileSystem.createFile(path, currentDirectory, "");
                
                if (!success) {
                    if (result.length() > 0) {
                        result.append("\n");
                    }
                    result.append("touch: cannot touch '").append(path).append("': No such file or directory");
                }
            }
        }
        
        return result.toString();
    }
}