package com.terminaltrainer.ui;

import com.terminaltrainer.core.TerminalSession;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TerminalUI {
    private final TerminalSession session;
    private JFrame frame;
    private JTextPane terminal;
    private StyledDocument doc;
    private Style defaultStyle;
    private Style promptStyle;
    private Style commandStyle;
    private Style outputStyle;
    private Style errorStyle;
    private Style successStyle;
    private Style highlightStyle;
    private Style mascotStyle;

    private int inputStart;
    private int inputEnd;
    private boolean processingCommand;
    private final List<String> commandHistory;
    private int historyIndex;

    private final JPanel mascotPanel;
    private final JLabel mascotLabel;
    private final JTextArea mascotSpeech;


    public TerminalUI(TerminalSession session) {
        this.session = session;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = 0;
        this.processingCommand = false;

        this.mascotPanel = new JPanel(new BorderLayout());

        ImageIcon mascotIcon = null;
        try {
            mascotIcon = new ImageIcon("src/com/terminaltrainer/ui/mascot.png", "Mascot Image");
        } catch (Exception e) {
            mascotIcon = loadMascotImage();
        }

        this.mascotLabel = new JLabel(mascotIcon);
        this.mascotSpeech = new JTextArea(5, 20);

        initializeUI();
    }


    private void initializeUI() {
        frame = new JFrame("Terminal Trainer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        terminal = new JTextPane();
        terminal.setEditable(true);
        terminal.setBackground(Color.BLACK);
        terminal.setCaretColor(Color.WHITE);
        terminal.setFont(new Font("Monospaced", Font.PLAIN, 14));

        doc = terminal.getStyledDocument();
        defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        promptStyle = doc.addStyle("prompt", defaultStyle);
        StyleConstants.setForeground(promptStyle, Color.GREEN);
        StyleConstants.setBold(promptStyle, true);

        commandStyle = doc.addStyle("command", defaultStyle);
        StyleConstants.setForeground(commandStyle, Color.WHITE);

        outputStyle = doc.addStyle("output", defaultStyle);
        StyleConstants.setForeground(outputStyle, Color.LIGHT_GRAY);

        errorStyle = doc.addStyle("error", defaultStyle);
        StyleConstants.setForeground(errorStyle, Color.RED);

        successStyle = doc.addStyle("success", defaultStyle);
        StyleConstants.setForeground(successStyle, Color.GREEN);

        highlightStyle = doc.addStyle("highlight", defaultStyle);
        StyleConstants.setForeground(highlightStyle, Color.YELLOW);
        StyleConstants.setBold(highlightStyle, true);

        mascotStyle = doc.addStyle("mascot", defaultStyle);
        StyleConstants.setForeground(mascotStyle, Color.CYAN);
        StyleConstants.setItalic(mascotStyle, true);

        mascotSpeech.setEditable(false);
        mascotSpeech.setLineWrap(true);
        mascotSpeech.setWrapStyleWord(true);
        mascotSpeech.setBackground(new Color(255, 255, 225));
        mascotSpeech.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mascotSpeech.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mascotPanel.add(mascotLabel, BorderLayout.NORTH);
        mascotPanel.add(new JScrollPane(mascotSpeech), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(terminal), mascotPanel);
        splitPane.setDividerLocation(600);

        frame.getContentPane().add(splitPane);

        terminal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        terminal.setNavigationFilter(new NavigationFilter() {
            @Override
            public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
                if (dot < inputStart) {
                    fb.setDot(inputStart, bias);
                } else if (dot > doc.getLength()) {
                    fb.setDot(doc.getLength(), bias);
                } else {
                    fb.setDot(dot, bias);
                }
            }

            @Override
            public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
                if (dot < inputStart) {
                    fb.setDot(inputStart, bias);
                } else if (dot > doc.getLength()) {
                    fb.setDot(doc.getLength(), bias);
                } else {
                    fb.moveDot(dot, bias);
                }
            }
        });
    }


    private void handleKeyPress(KeyEvent e) {
        if (processingCommand) {
            e.consume();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                e.consume();
                processInput();
                break;
            case KeyEvent.VK_UP:
                e.consume();
                navigateHistory(-1);
                break;
            case KeyEvent.VK_DOWN:
                e.consume();
                navigateHistory(1);
                break;
            case KeyEvent.VK_HOME:
                e.consume();
                terminal.setCaretPosition(inputStart);
                break;
            case KeyEvent.VK_BACK_SPACE:
                if (terminal.getCaretPosition() <= inputStart) {
                    e.consume();
                }
                break;
        }
    }


    private void processInput() {
        try {
            processingCommand = true;

            String command = doc.getText(inputStart, doc.getLength() - inputStart);

            appendText("\n", defaultStyle);

            if (!command.trim().isEmpty()) {
                commandHistory.add(command);
                historyIndex = commandHistory.size();
            }

            String result = session.executeCommand(command);

            if (!result.isEmpty()) {
                appendText(result + "\n", outputStyle);
            }

            displayPrompt();
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            processingCommand = false;
        }
    }


    private void navigateHistory(int direction) {
        if (commandHistory.isEmpty()) {
            return;
        }

        historyIndex += direction;

        if (historyIndex < 0) {
            historyIndex = 0;
        } else if (historyIndex > commandHistory.size()) {
            historyIndex = commandHistory.size();
        }

        try {
            doc.remove(inputStart, doc.getLength() - inputStart);

            if (historyIndex < commandHistory.size()) {
                doc.insertString(inputStart, commandHistory.get(historyIndex), commandStyle);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the command prompt.
     */
    private void displayPrompt() {
        try {
            String prompt = session.getPrompt();
            appendText(prompt, promptStyle);
            inputStart = doc.getLength();
            inputEnd = inputStart;
            terminal.setCaretPosition(inputStart);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private void appendText(String text, Style style) throws BadLocationException {
        doc.insertString(doc.getLength(), text, style);
        inputEnd = doc.getLength();
    }


    public void displayMascotMessage(String message) {
        mascotSpeech.setText(message);
    }


    private ImageIcon createPlaceholderMascot() {
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(new Color(0, 128, 128));
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, width - 1, height - 1);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("TUX", 35, 45);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.drawString("Mascot", 30, 65);

        g2d.dispose();

        return new ImageIcon(image);
    }
    private ImageIcon loadMascotImage() {
        try {
            URL imageUrl = getClass().getResource("src/com/terminaltrainer/ui/mascot.png");
            if (imageUrl == null) {
                System.err.println("Could not find mascot image. Using placeholder instead.");
                return createPlaceholderMascot();
            }
            return new ImageIcon(imageUrl);
        } catch (Exception e) {
            System.err.println("Error loading mascot image: " + e.getMessage());
            return createPlaceholderMascot();
        }
    }


    public void start() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            terminal.requestFocusInWindow();

            try {
                appendText("Welcome to Terminal Trainer!\n", highlightStyle);
                appendText("This educational application will help you learn basic Linux commands.\n", outputStyle);
                appendText("Type 'help' for a list of available commands.\n", outputStyle);
                appendText("Type 'tutorial start' to begin the guided tutorial.\n\n", outputStyle);

                displayPrompt();

                displayMascotMessage("Hi there! I'm Tux, your terminal guide. " +
                        "I'll help you learn how to use Linux commands. " +
                        "Type 'help' to see what commands are available, or " +
                        "'tutorial start' to begin a guided lesson!");
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
}
