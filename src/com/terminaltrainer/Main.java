package com.terminaltrainer;

import com.terminaltrainer.ui.TerminalUI;
import com.terminaltrainer.core.TerminalSession;


public class Main {
    public static void main(String[] args) {
        TerminalSession session = new TerminalSession();
        
        TerminalUI terminalUI = new TerminalUI(session);
        
        terminalUI.start();
    }
}