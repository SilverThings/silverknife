package controllers;

import core.HelpDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelpDialogController {

    @FXML
    private Button dialogDismissButton;

    private HelpDialog dialog;

    public void setDialogParent(HelpDialog dialog) {
        this.dialog = dialog;
    }

    @FXML
    private void dismissDialog() {
        Stage stage = dialog.getStage();
        stage.close();
    }

    public void setButtonStyle() {
        dialogDismissButton.setStyle("-fx-font-size: 30pt;-fx-background-color: #03A9F4;-fx-text-fill: white;");
    }
}
