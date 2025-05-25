package com.terminaltrainer;

import com.terminaltrainer.ui.TerminalUI;
import com.terminaltrainer.core.TerminalSession;

/**
 * Main entry point for the Terminal Trainer application.
 * This educational application simulates a Linux terminal environment
 * designed specifically for children to learn basic command-line skills.
 */
public class Main {
    public static void main(String[] args) {
        // Create and start a new terminal session
        TerminalSession session = new TerminalSession();
        
        // Initialize the terminal UI
        TerminalUI terminalUI = new TerminalUI(session);
        
        // Start the application
        terminalUI.start();
    }
}