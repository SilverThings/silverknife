package core;

import javafx.scene.control.TextArea;

public class Logger {

    private TextArea logView;

    public Logger(TextArea logView) {
        this.logView = logView;
    }

    public void log(String message){
        logView.appendText(message + "\n");
    }
}
