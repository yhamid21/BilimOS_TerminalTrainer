package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;
import com.terminaltrainer.core.filesystem.VirtualFile;
import com.terminaltrainer.core.filesystem.VirtualFileSystemNode;

/**
 * Implementation of the 'cat' command, which displays file contents.
 */
public class CatCommand implements Command {
    @Override
    public String getName() {
        return "cat";
    }

    @Override
    public String getDescription() {
        return "Display file contents";
    }

    @Override
    public String getHelpText() {
        return "Usage: cat [FILE]...\n" +
               "Display the contents of FILE(s).\n\n" +
               "Examples:\n" +
               "  cat file.txt        Display the contents of file.txt\n" +
               "  cat file1 file2     Display the contents of file1 followed by file2";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        if (args.length == 0) {
            return "cat: missing file operand\nTry 'cat --help' for more information.";
        }
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < args.length; i++) {
            String path = args[i];
            
            if (path.equals("--help") || path.equals("-h")) {
                return getHelpText();
            }
            
            // Resolve the file path
            VirtualFileSystemNode node = session.getFileSystem().resolvePath(path, session.getCurrentDirectory());
            
            if (node == null) {
                result.append("cat: ").append(path).append(": No such file or directory");
                if (i < args.length - 1) {
                    result.append("\n");
                }
                continue;
            }
            
            if (!node.isFile()) {
                result.append("cat: ").append(path).append(": Is a directory");
                if (i < args.length - 1) {
                    result.append("\n");
                }
                continue;
            }
            
            // Display the file contents
            VirtualFile file = (VirtualFile) node;
            result.append(file.getContent());
            
            // Add a newline between files if this isn't the last file
            if (i < args.length - 1 && !file.getContent().endsWith("\n")) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
}