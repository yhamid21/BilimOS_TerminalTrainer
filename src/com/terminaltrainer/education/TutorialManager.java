package com.terminaltrainer.education;

import com.terminaltrainer.core.TerminalSession;


public class TutorialManager {
    private final TerminalSession session;
    private int currentTutorialStep;
    private boolean tutorialMode;


    public TutorialManager(TerminalSession session) {
        this.session = session;
        this.currentTutorialStep = 0;
        this.tutorialMode = false;
    }


    public String startTutorial() {
        this.tutorialMode = true;
        this.currentTutorialStep = 0;
        return "Welcome to the Terminal Trainer tutorial!\n" +
               "This tutorial will guide you through the basics of using a Linux terminal.\n" +
               "Type 'next' to proceed to the first step.";
    }


    public String nextStep() {
        if (!tutorialMode) {
            return "Tutorial mode is not active. Type 'tutorial start' to begin.";
        }

        currentTutorialStep++;
        
        switch (currentTutorialStep) {
            case 1:
                return "Step 1: Navigation\n" +
                       "Let's start by learning how to navigate the file system.\n" +
                       "Type 'pwd' to see your current location.";
            case 2:
                return "Step 2: Listing Files\n" +
                       "Now let's see what files are in this directory.\n" +
                       "Type 'ls' to list the files and directories.";
            case 3:
                return "Step 3: Changing Directories\n" +
                       "Let's move to another directory.\n" +
                       "Type 'cd Documents' to change to the Documents directory.";
            case 4:
                return "Step 4: Viewing File Contents\n" +
                       "Let's look at the content of a file.\n" +
                       "Type 'cat welcome.txt' to view the welcome message.";
            case 5:
                return "Congratulations! You've completed the basic tutorial.\n" +
                       "You've learned how to navigate the file system, list files,\n" +
                       "change directories, and view file contents.\n" +
                       "Type 'help' to see what other commands are available.";
            default:
                tutorialMode = false;
                return "The tutorial is complete. Type 'tutorial start' to begin again.";
        }
    }


    public boolean isTutorialMode() {
        return tutorialMode;
    }


    public int getCurrentTutorialStep() {
        return currentTutorialStep;
    }


    public String endTutorial() {
        tutorialMode = false;
        return "Tutorial mode has been ended. Type 'tutorial start' to begin again.";
    }
}