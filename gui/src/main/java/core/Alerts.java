package core;

import javafx.scene.control.Alert;

interface Alerts {

    void createErrorAlert(String header, String content);

    void createInfoAlert(String header, String content);

    void createWarningAlert(String header, String content);

    Alert createConfirmationAlert(String header, String content);

}
