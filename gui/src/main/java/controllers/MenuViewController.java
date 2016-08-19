package controllers;

import core.*;
import core.networking.Networking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import layouts.EmbeddedLayout;
import models.MenuViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuViewController {

    private final static String RPI_IMAGE = "-fx-background-image: url(\"/RPI_layout.jpg\");-fx-background-repeat: no-repeat;-fx-background-position: bottom left";
    private final static String BBB_IMAGE = "-fx-background-image: url(\"/BBB_layout.jpg\");-fx-background-repeat: no-repeat;-fx-background-position: bottom left";
    private final static String CB_IMAGE = "-fx-background-image: url(\"/CB_layout.jpg\");-fx-background-repeat: no-repeat;-fx-background-position: bottom left";
    private final static String NO_IMAGE = "";
    private final static String WRONG_IP_FORMAT = "Wrong IP address format";
    private final static String NO_EMBEDDED_CHOSEN = "Choose embedded type before saving.";
    private final static String NO_FILE_AVAILABLE = "Cannot load file with system configuration. Create new file by adding system configuration.";
    private final static String DEFAULT_SYSTEM_TYPE = "Cannot process system type. Select from available options.";
    private final static String DEFAULT_AVAILABLE_SYSTEM = "Selected layout is already visible or no layout was chosen.";
    private final static String SEND_I2C_BY_BUTTON = "Send I2C by clicking on appropriate button in layout.";
    private final static String SEND_SPI_BY_BUTTON = "Send SPI by clicking on appropriate button in layout.";

    public final static String OBSERVABLE_MACRO_TEXT = "Macro";
    public final static String OBSERVABLE_I2C_TEXT = "I2C Message";
    public final static String OBSERVABLE_SPI_TEXT = "SPI Message";

    private Root root;

    private MenuViewModel menuViewModel;
    private AlertsImpl alerts;
    private Validations validations;
    private String selectedSystemTypeFromComboBox;
    private String currentlySelectedSystemType;
    private String selectedSystemIpFromComboBox;
    private String selectedSystemTypeFromButton;
    private Networking networking;
    private Logger logger;
    private int refreshRate = -1;
    @FXML
    private TextField ipAddressTextField;

    @FXML
    private ComboBox<String> embeddedTypeComboBox;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button addButton;

    @FXML
    private ComboBox<String> embeddedListComboBox;

    @FXML
    private CheckBox embeddedLayoutCheckBox;

    @FXML
    private CheckBox pinRequestCheckBox;

    @FXML
    private TextField refreshRateTextField;

    @FXML
    private Button updateRefreshRateButton;

    @FXML
    public TextField addressTextField;

    @FXML
    private TextArea messagesTextArea;

    @FXML
    private ComboBox<String> textAreaComboBox;

    @FXML
    private Button sendButton;

    public void setMainApp(Root root, Networking networking, Logger logger) {
        this.root = root;
        this.networking = networking;
        this.logger = logger;
        init();
        setAllValidationBorders();
    }

    private void init() {
        alerts = new AlertsImpl();
        menuViewModel = new MenuViewModel(logger);
        validations = new Validations(logger);
        ObservableList<String> textAreaOptions = FXCollections.observableArrayList(OBSERVABLE_MACRO_TEXT, OBSERVABLE_I2C_TEXT, OBSERVABLE_SPI_TEXT);
        textAreaComboBox.setItems(textAreaOptions);
        textAreaComboBox.getSelectionModel().selectFirst();
    }

    private void setAllValidationBorders() {
        validations.setIpAddressValidationBorder(ipAddressTextField);
        validations.setPinRequestValidationBorder(refreshRateTextField);
        validations.setTextAreaValidationBorder(messagesTextArea, -1);
        validations.setAddressValidationBorder("", addressTextField);
    }

    @FXML
    private void changeIp() {
        validations.setIpAddressValidationBorder(ipAddressTextField);
        setButtonsEnabled();
    }

    @FXML
    private void connect(ActionEvent event) throws IOException {
        connectButton.setDisable(true);
        disconnectButton.setDisable(false);
        ipAddressTextField.setDisable(true);
        embeddedTypeComboBox.setDisable(true);
        embeddedLayoutCheckBox.setDisable(false);
        pinRequestCheckBox.setDisable(false);
        refreshRateTextField.setDisable(false);
//        pinRequestButton.setDisable(false);
        disconnectButton.requestFocus();
        if (event == null) {
            // TODO: 18.8.2016 NETWORK_OP
            // TODO: 19.8.2016 mozno popup 
//            boolean connected = networking.connect(selectedSystemIpFromComboBox);
//            if (connected) {
            root.showEmbeddedLayout(selectedSystemTypeFromComboBox);
            logger.log(selectedSystemTypeFromComboBox + " layout loaded.");
//            }
        } else {
            String ipAddress = ipAddressTextField.getText();
            if (validations.isIpAddress(ipAddress)) {
                // TODO: 18.8.2016 NETWORK_OP
//                boolean connected = networking.connect(ipAddress);
//                if (connected) {
                root.showEmbeddedLayout(selectedSystemTypeFromButton);
                logger.log(selectedSystemTypeFromButton + " layout loaded.");
//                }
            }
        }
    }

    @FXML
    private void disconnect() {
        if (validations.isIpAddress(ipAddressTextField.getText())) {
            connectButton.setDisable(false);
            connectButton.requestFocus();
        }
        embeddedLayoutCheckBox.setDisable(true);
        pinRequestCheckBox.setDisable(true);
        refreshRateTextField.setDisable(true);
        updateRefreshRateButton.setDisable(true);
        embeddedTypeComboBox.setDisable(false);
        ipAddressTextField.setDisable(false);
        disconnectButton.setDisable(true);
        embeddedLayoutCheckBox.setSelected(false);
        if (pinRequestCheckBox.isSelected()) {
            pinRequestCheckBox.setSelected(false);
            toggleRequestPinStatus();
        }
        root.removeEmbeddedLayout();
        networking.disconnect();
    }

    @FXML
    private void add() {
        String ipAddressTextFieldText = ipAddressTextField.getText();
        String selectedItem = embeddedTypeComboBox.getSelectionModel().getSelectedItem();

        if (!validations.isIpAddress(ipAddressTextFieldText)) {
            alerts.createErrorAlert(null, WRONG_IP_FORMAT);
            logger.log(WRONG_IP_FORMAT);
        } else {
            if (selectedItem == null || selectedItem.isEmpty()) {
                alerts.createInfoAlert(null, NO_EMBEDDED_CHOSEN);
            } else {
                menuViewModel.add(ipAddressTextFieldText, selectedItem);
                ipAddressTextField.clear();
                changeIp();
            }
        }
    }

    @FXML
    private void loadAvailableSystems() {
        try {
            if (embeddedListComboBox.getItems().isEmpty()) {
                menuViewModel.readFileContent(true);
            } else {
                menuViewModel.readFileContent(false);
            }
        } catch (IOException e) {
            alerts.createErrorAlert(null, NO_FILE_AVAILABLE);
            logger.log(NO_FILE_AVAILABLE);
        }
        ArrayList<String> embeddedList = menuViewModel.getEmbeddedSystems();
        ObservableList<String> embeddedSystems = FXCollections.observableArrayList(embeddedList);
        embeddedListComboBox.setItems(embeddedSystems);

        currentlySelectedSystemType = embeddedListComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void deleteAll() {
        try {
            menuViewModel.emptyConfigurationFile();
        } catch (IOException e) {
            logger.log(e.toString());
        }
    }

    @FXML
    private void toggleLayoutVisible() {
        GridPane centerLayout = (GridPane) root.getRootLayout().getCenter();

        if (centerLayout != null) {
            if (embeddedLayoutCheckBox.isSelected()) {
                switch (root.getVisibleLayout()) {
                    case Root.RASPBERRY_PI_2_NAME:
                        centerLayout.setStyle(RPI_IMAGE);
                        break;
                    case Root.BEAGLEBONE_BLACK_NAME:
                        centerLayout.setStyle(BBB_IMAGE);
                        break;
                    case Root.CUBIEBOARD_NAME:
                        centerLayout.setStyle(CB_IMAGE);
                        break;
                }
            } else {
                centerLayout.setStyle(NO_IMAGE);
            }
        }
    }

    @FXML
    private void toggleRequestPinStatus() {
        if (pinRequestCheckBox.isSelected()) {
            refreshRateTextField.setDisable(false);
            updateRefreshRateButton.setDisable(false);
            validateAndChangeRefreshRate();
            List<Pin> pins = root.getCheckedPins();
            EmbeddedLayout callback = root.getRequestStatusCallback();
            networking.startRequestPinStatus(callback, refreshRate, pins);
            // TODO: 16.8.2016 from eclipse method setUiFromResponse (include in new Thread task if networking method below does not do it)
        } else {
            refreshRateTextField.setDisable(true);
            updateRefreshRateButton.setDisable(true);
            networking.cancelRequestPinStatus();
        }
    }

    @FXML
    private void validateAndChangeRefreshRate() {
        boolean valid = validations.setPinRequestValidationBorder(refreshRateTextField);
        if (valid) {
            updateRefreshRateButton.setDisable(false);
            pinRequestCheckBox.setDisable(false);
            refreshRate = Integer.valueOf(refreshRateTextField.getText());
        } else {
            updateRefreshRateButton.setDisable(true);
            pinRequestCheckBox.setDisable(true);
            refreshRate = -1;
        }
    }

    @FXML
    private void changeRefreshRate() {
        String refreshRate = refreshRateTextField.getText();
        EmbeddedLayout callback = root.getRequestStatusCallback();
        networking.updateRequestRefreshRate(callback, Integer.valueOf(refreshRate));
        logger.log("Refresh rate updated to " + refreshRate);
    }

    @FXML
    private void setButtonsEnabled() {
        boolean isIp = validations.isIpAddress(ipAddressTextField.getText());

        String selectedItem = embeddedTypeComboBox.getSelectionModel().getSelectedItem();

        boolean isSelected = !(selectedItem == null || selectedItem.isEmpty());

        if (isSelected && isIp) {
            connectButton.setDisable(false);
            addButton.setDisable(false);
        } else {
            connectButton.setDisable(true);
            addButton.setDisable(true);
        }

        setSelectedSystemType();
    }

    private void setSelectedSystemType() {
        String selectedSystemType = embeddedTypeComboBox.getSelectionModel().getSelectedItem();

        if (selectedSystemType != null && !selectedSystemType.isEmpty()) {
            this.selectedSystemTypeFromButton = selectedSystemType;
        } else {
            logger.log(DEFAULT_SYSTEM_TYPE);
        }
    }

    @FXML
    private void selectAvailableSystem() throws IOException {
        String selectedSystemType = embeddedListComboBox.getSelectionModel().getSelectedItem();

        if (selectedSystemType != null && !selectedSystemType.isEmpty() && (!selectedSystemType.equals(currentlySelectedSystemType) || disconnectButton.isDisabled())) {
            this.selectedSystemTypeFromComboBox = menuViewModel.splitSelectedSystemString(selectedSystemType);
            this.selectedSystemIpFromComboBox = menuViewModel.splitIpFromSelectedString(selectedSystemType);
            if (root.isLayoutDisplayed()) {
                disconnect();
            }
            connect(null);
        } else {
            logger.log(DEFAULT_AVAILABLE_SYSTEM);
        }
    }

    @FXML
    private void showHelp(ActionEvent event) {
        new HelpDialog(event, logger);
    }

    @FXML
    public void validatePhysicalAddress() {
        String addressText = addressTextField.getText();
        if (addressTextField.getText() == null || addressTextField.getText().isEmpty()) {
            return;
        }
        validations.setAddressValidationBorder(addressText, addressTextField);
    }

    @FXML
    private void validateTextArea() {
        int textAreaSelectedItem = textAreaComboBox.getSelectionModel().getSelectedIndex();
        String textAreaText = messagesTextArea.getText();

        validations.setTextAreaValidationBorder(messagesTextArea, textAreaSelectedItem);

        if (validations.isTextAreaValid(textAreaSelectedItem, textAreaText)) {
            sendButton.setDisable(false);
        } else {
            sendButton.setDisable(true);
        }
    }

    @FXML
    private void sendTextAreaMessage() {
        String address = addressTextField.getText();
        String textAreaText = messagesTextArea.getText();
        boolean validateAddress = false;
        if (textAreaText.contains("I2C") || textAreaText.contains("SPI")) {
            validateAddress = true;
        }
        switch (textAreaComboBox.getSelectionModel().getSelectedItem()) {
            case OBSERVABLE_MACRO_TEXT:
                List<String> commands = Arrays.asList(textAreaText.split("\n"));
                if (!validations.isTextAreaValid(0, textAreaText)) {
                    logger.log("Commands are not valid");
                    return;
                }
                if (validateAddress) {
                    if (!validations.isPhysicalAddressValid(address)) {
                        logger.log("Address is not valid");
                        return;
                    }
                }
                // TODO: 18.8.2016  vyvolaj popup
                networking.sendMacro(address, commands);
                // TODO: 18.8.2016 zavri popup
                break;
            case OBSERVABLE_I2C_TEXT:
                logger.log(SEND_I2C_BY_BUTTON);
                alerts.createInfoAlert(null, SEND_I2C_BY_BUTTON);
                break;
            case OBSERVABLE_SPI_TEXT:
                logger.log(SEND_SPI_BY_BUTTON);
                alerts.createInfoAlert(null, SEND_SPI_BY_BUTTON);
                break;
        }
    }

    public String getAddress() {
        return addressTextField.getText();
    }

    public String getCommandMode() {
        return textAreaComboBox.getSelectionModel().getSelectedItem();
    }

    public String getCommand() {
        return messagesTextArea.getText();
    }

    public boolean isSendRequestCheckBoxChecked() {
        return pinRequestCheckBox.isSelected();
    }
}
