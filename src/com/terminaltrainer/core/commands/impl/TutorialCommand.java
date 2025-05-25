package com.terminaltrainer.core.commands.impl;

import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.core.commands.Command;
import com.terminaltrainer.education.TutorialManager;

/**
 * Implementation of the 'tutorial' command, which provides access to the tutorial system.
 */
public class TutorialCommand implements Command {
    @Override
    public String getName() {
        return "tutorial";
    }

    @Override
    public String getDescription() {
        return "Access the tutorial system";
    }

    @Override
    public String getHelpText() {
        return "Usage: tutorial [OPTION]\n" +
               "Access the tutorial system.\n\n" +
               "Options:\n" +
               "  start     Start the tutorial\n" +
               "  next      Proceed to the next tutorial step\n" +
               "  end       End the tutorial\n" +
               "  status    Show the current tutorial status\n\n" +
               "Examples:\n" +
               "  tutorial start    Start the tutorial\n" +
               "  tutorial next     Proceed to the next tutorial step";
    }

    @Override
    public String execute(String[] args, TerminalSession session) {
        TutorialManager tutorialManager = session.getTutorialManager();
        
        if (args.length == 0) {
            return "Please specify an option. Type 'tutorial --help' for more information.";
        }
        
        String option = args[0].toLowerCase();
        
        switch (option) {
            case "start":
                return tutorialManager.startTutorial();
            case "next":
                return tutorialManager.nextStep();
            case "end":
                return tutorialManager.endTutorial();
            case "status":
                if (tutorialManager.isTutorialMode()) {
                    return "Tutorial is active. Current step: " + tutorialManager.getCurrentTutorialStep();
                } else {
                    return "Tutorial is not active. Type 'tutorial start' to begin.";
                }
            case "--help":
            case "-h":
                return getHelpText();
            default:
                return "Unknown option: " + option + ". Type 'tutorial --help' for more information.";
        }
    }
}