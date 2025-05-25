package com.terminaltrainer.core;

import java.util.ArrayList;
import java.util.List;


public class CommandHistory {
    private final List<String> history;
    private int currentIndex;

    public CommandHistory() {
        this.history = new ArrayList<>();
        this.currentIndex = 0;
    }


    public void addCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        
        if (!history.isEmpty() && command.equals(history.get(history.size() - 1))) {
            return;
        }
        
        history.add(command);
        currentIndex = history.size();
    }


    public String getPreviousCommand() {
        if (history.isEmpty() || currentIndex <= 0) {
            return "";
        }
        
        currentIndex = Math.max(0, currentIndex - 1);
        return history.get(currentIndex);
    }


    public String getNextCommand() {
        if (history.isEmpty() || currentIndex >= history.size() - 1) {
            currentIndex = history.size();
            return "";
        }
        
        currentIndex++;
        return history.get(currentIndex);
    }


    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
        currentIndex = 0;
    }


    public int size() {
        return history.size();
    }
}