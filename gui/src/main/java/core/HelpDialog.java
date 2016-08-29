package core;

import controllers.HelpDialogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class HelpDialog {

    private final static String HELP_IMAGE = "-fx-background-image: url(\"/Help.png\");-fx-background-repeat: no-repeat;-fx-background-position: top center";
    private static final URL DIALOG_VIEW = Root.class.getResource("/dialogView.fxml");

    private Stage stage;

    public HelpDialog(ActionEvent event, Logger logger) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DIALOG_VIEW);
            Parent view = loader.load();
            view.getStylesheets().add(Root.CSS_FILE);
            view.setStyle(HELP_IMAGE);

            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(view));
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());

            HelpDialogController controller = loader.getController();
            controller.setDialogParent(this);
            controller.setButtonStyle();

            stage.show();
        } catch (IOException e) {
            logger.log("Cannot load dialog.");
        }
    }

    public Stage getStage() {
        return stage;
    }
}
