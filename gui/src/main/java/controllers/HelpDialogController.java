package controllers;

import core.HelpDialog;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class HelpDialogController {

    private HelpDialog dialog;

    public void setDialogParent(HelpDialog dialog) {
        this.dialog = dialog;
    }

    @FXML
    private void dismissDialog() {
        Stage stage = dialog.getStage();
        stage.close();
    }
}
