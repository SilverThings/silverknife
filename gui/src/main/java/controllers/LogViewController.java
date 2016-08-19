package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LogViewController {

    @FXML
    private TextArea logTextArea;

    @FXML
    public void clearLog() {
        logTextArea.setText("");
    }
}
