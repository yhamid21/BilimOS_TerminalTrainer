package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;
import com.terminaltrainer.core.filesystem.VirtualDirectory;
import com.terminaltrainer.core.filesystem.VirtualFile;
import com.terminaltrainer.core.filesystem.VirtualFileSystemNode;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of the 'ls' command, which lists directory contents.
 */
public class LsCommand implements Command {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd HH:mm");

    @Override
    public String getName() {
        return "ls";
    }

    @Override
    public String getDescription() {
        return "List directory contents";
    }

    @Override
    public String getHelpText() {
        return "Usage: ls [OPTION]... [FILE]...\n" +
               "List information about the FILEs (the current directory by default).\n\n" +
               "Options:\n" +
               "  -l     use a long listing format\n" +
               "  -a     do not ignore entries starting with .\n" +
               "  -h     display this help and exit\n\n" +
               "Examples:\n" +
               "  ls      List files in the current directory\n" +
               "  ls -l   List files in long format\n" +
               "  ls /bin List files in the /bin directory";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        boolean longFormat = false;
        boolean showHidden = false;
        String targetPath = ".";

        // Parse arguments
        for (String arg : args) {
            if (arg.startsWith("-")) {
                // It's an option
                for (int i = 1; i < arg.length(); i++) {
                    char option = arg.charAt(i);
                    switch (option) {
                        case 'l':
                            longFormat = true;
                            break;
                        case 'a':
                            showHidden = true;
                            break;
                        default:
                            return "ls: invalid option -- '" + option + "'\n" +
                                   "Try 'ls --help' for more information.";
                    }
                }
            } else {
                // It's a path
                targetPath = arg;
            }
        }

        // Resolve the target path
        VirtualFileSystemNode node = session.getFileSystem().resolvePath(targetPath, session.getCurrentDirectory());
        
        if (node == null) {
            return "ls: cannot access '" + targetPath + "': No such file or directory";
        }

        if (node.isFile()) {
            // If it's a file, just show the file info
            return formatFileInfo((VirtualFile) node, longFormat);
        } else {
            // It's a directory, list its contents
            VirtualDirectory dir = (VirtualDirectory) node;
            List<VirtualFileSystemNode> contents = dir.getChildren();
            
            if (contents.isEmpty()) {
                return ""; // Empty directory
            }

            StringBuilder result = new StringBuilder();
            
            for (VirtualFileSystemNode child : contents) {
                // Skip hidden files (starting with .) unless -a is specified
                if (!showHidden && child.getName().startsWith(".")) {
                    continue;
                }
                
                if (longFormat) {
                    if (child.isFile()) {
                        result.append(formatFileInfo((VirtualFile) child, true)).append("\n");
                    } else {
                        result.append(formatDirectoryInfo((VirtualDirectory) child)).append("\n");
                    }
                } else {
                    result.append(child.getName());
                    if (child.isDirectory()) {
                        result.append("/");
                    }
                    result.append("  ");
                }
            }
            
            return result.toString().trim();
        }
    }

    /**
     * Formats file information for display.
     *
     * @param file The file
     * @param longFormat Whether to use long format
     * @return The formatted file info
     */
    private String formatFileInfo(VirtualFile file, boolean longFormat) {
        if (!longFormat) {
            return file.getName();
        }
        
        // Format: permissions owner group size date name
        return String.format("%s %s %s %6d %s %s",
                file.getPermissionsString(),
                file.getOwner(),
                file.getGroup(),
                file.getSize(),
                DATE_FORMATTER.format(file.getModificationTime()),
                file.getName());
    }

    /**
     * Formats directory information for display.
     *
     * @param dir The directory
     * @return The formatted directory info
     */
    private String formatDirectoryInfo(VirtualDirectory dir) {
        // Format: permissions owner group size date name
        return String.format("%s %s %s %6d %s %s",
                dir.getPermissionsString(),
                dir.getOwner(),
                dir.getGroup(),
                dir.getSize(),
                DATE_FORMATTER.format(dir.getModificationTime()),
                dir.getName());
    }
}