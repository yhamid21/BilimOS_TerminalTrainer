package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;
import com.terminaltrainer.core.filesystem.VirtualFileSystem;
import com.terminaltrainer.core.filesystem.VirtualFileSystemNode;

/**
 * Implementation of the 'mkdir' command, which creates directories.
 */
public class MkdirCommand implements Command {
    @Override
    public String getName() {
        return "mkdir";
    }

    @Override
    public String getDescription() {
        return "Create directories";
    }

    @Override
    public String getHelpText() {
        return "Usage: mkdir [OPTION]... DIRECTORY...\n" +
               "Create the DIRECTORY(ies), if they do not already exist.\n\n" +
               "Options:\n" +
               "  -p     create parent directories as needed\n\n" +
               "Examples:\n" +
               "  mkdir dir1 dir2     Create directories dir1 and dir2\n" +
               "  mkdir -p a/b/c      Create directory a, then a/b, then a/b/c";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        if (args.length == 0) {
            return "mkdir: missing operand\nTry 'mkdir --help' for more information.";
        }
        
        if (args[0].equals("--help") || args[0].equals("-h")) {
            return getHelpText();
        }
        
        boolean createParents = false;
        int startIndex = 0;
        
        // Check for -p option
        if (args[0].equals("-p")) {
            createParents = true;
            startIndex = 1;
            
            if (args.length == 1) {
                return "mkdir: missing operand\nTry 'mkdir --help' for more information.";
            }
        }
        
        VirtualFileSystem fileSystem = session.getFileSystem();
        String currentDirectory = session.getCurrentDirectory();
        StringBuilder result = new StringBuilder();
        
        for (int i = startIndex; i < args.length; i++) {
            String path = args[i];
            
            if (createParents) {
                // Create parent directories as needed
                String[] components = path.split("/");
                StringBuilder currentPath = new StringBuilder();
                
                // Handle absolute paths
                if (path.startsWith("/")) {
                    currentPath.append("/");
                }
                
                for (int j = 0; j < components.length; j++) {
                    String component = components[j];
                    
                    if (component.isEmpty()) {
                        continue;
                    }
                    
                    if (currentPath.length() > 0 && currentPath.charAt(currentPath.length() - 1) != '/') {
                        currentPath.append("/");
                    }
                    
                    currentPath.append(component);
                    
                    // Check if this directory already exists
                    VirtualFileSystemNode node = fileSystem.resolvePath(currentPath.toString(), currentDirectory);
                    
                    if (node == null) {
                        // Create the directory
                        boolean success = fileSystem.createDirectory(currentPath.toString(), currentDirectory);
                        
                        if (!success && result.length() > 0) {
                            result.append("\n");
                            result.append("mkdir: cannot create directory '").append(currentPath).append("': File exists or parent directory doesn't exist");
                        }
                    } else if (!node.isDirectory()) {
                        if (result.length() > 0) {
                            result.append("\n");
                        }
                        result.append("mkdir: cannot create directory '").append(currentPath).append("': Not a directory");
                        break;
                    }
                }
            } else {
                // Create just the specified directory
                boolean success = fileSystem.createDirectory(path, currentDirectory);
                
                if (!success) {
                    if (result.length() > 0) {
                        result.append("\n");
                    }
                    
                    // Check if the directory already exists
                    VirtualFileSystemNode node = fileSystem.resolvePath(path, currentDirectory);
                    
                    if (node != null && node.isDirectory()) {
                        result.append("mkdir: cannot create directory '").append(path).append("': File exists");
                    } else if (node != null) {
                        result.append("mkdir: cannot create directory '").append(path).append("': Not a directory");
                    } else {
                        result.append("mkdir: cannot create directory '").append(path).append("': No such file or directory");
                    }
                }
            }
        }
        
        return result.toString();
    }
}