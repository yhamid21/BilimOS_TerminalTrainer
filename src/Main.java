import com.terminaltrainer.core.TerminalSession;
import com.terminaltrainer.ui.TerminalUI;


public class Main {
    public static void main(String[] args) {
        TerminalSession session = new TerminalSession();

        TerminalUI terminalUI = new TerminalUI(session);

        terminalUI.start();
    }
}
