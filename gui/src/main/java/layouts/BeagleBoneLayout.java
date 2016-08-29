package layouts;

import core.AlertsImpl;
import core.Logger;
import core.Pin;
import core.Root;
import core.hashmaps.BeagleBoneHashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class BeagleBoneLayout implements EmbeddedLayout {

    private final static int GRID_PANE_POSITION_IN_ROOT_CHILDREN = 3;
    private final static int ELEMENTS_FOR_ROW_COUNT = 5;
    private final static int PIN_DOUBLE_MAX_COL_COUNT = 10;
    private final static int LAYOUT_MAX_COL_COUNT = 20;
    private final static int NUMBER_OF_ROWS_IN_LAYOUT = 23;
    private final static int BUTTON_MIN_SIZE_WIDTH = 35;
    private final static int BUTTON_MIN_SIZE_HEIGHT = 35;
    private final static int IO_COMBO_BOX_PREF_WIDTH = 110;
    private static final int PIN_COUNT = 92;
    private final static int PIN_TYPE_COMBO_BOX_PREF_WIDTH = 110;
    private final static int ELEMENTS_COUNT = PIN_COUNT * ELEMENTS_FOR_ROW_COUNT;
    private final static String GPIO_OUTPUT = "OUT";
    private final static String GPIO_INPUT = "IN";
    private static final String LAYOUT_CREATION_FAILED = "Creation of layout failed.";
    private static final String BUTTON_COLOR_RED = "-fx-background-color: #ff9999;-fx-text-fill: black;-fx-font-size: 9pt;";
    private static final String BUTTON_COLOR_GREEN = "-fx-background-color: #99ff99;-fx-text-fill: black;-fx-font-size: 9pt;";
    private static final String DEFAULT_BUTTON_STYLE = "/Style.css";

    private Root root;
    private Logger logger;
    private GridPane gridPane;
    private AlertsImpl alerts;
    private ArrayList<CheckBox> checkBoxes;
    private ArrayList<ComboBox<String>> inputOutputComboBoxes;
    private ArrayList<ComboBox<String>> pinTypeComboBoxes;
    private ArrayList<Button> buttons;
    private ArrayList<Pin> pins;
    private int row = 1;
    private int column = 1;
    private int buttonId = 1;
    private int pinTypeComboBoxId = 1;
    private int inputOutputComboBoxId = 1;
    private int checkBoxId = 1;

    public void setMainApp(Root root, Logger logger) {
        this.root = root;
        this.logger = logger;
        init();
    }

    private void init() {
        gridPane = (GridPane) root.getRootLayout().getChildren().get(GRID_PANE_POSITION_IN_ROOT_CHILDREN);

        alerts = new AlertsImpl();

        checkBoxes = new ArrayList<>();
        inputOutputComboBoxes = new ArrayList<>();
        pinTypeComboBoxes = new ArrayList<>();
        buttons = new ArrayList<>();
        pins = new ArrayList<>();

        createLayout();
        disableGridElements(buttons, pinTypeComboBoxes, inputOutputComboBoxes, checkBoxes);
        for (CheckBox checkBox : checkBoxes) {
            toggleElementsEnabled(false, Integer.valueOf(checkBox.getId()) - 1);
        }
    }

    private void createLayout() {
        BeagleBoneHashMap bbbMap = new BeagleBoneHashMap();
        bbbMap.createHashMap();

        boolean secondRow = false;
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            if (!secondRow) {
                if (column > PIN_DOUBLE_MAX_COL_COUNT) {
                    column = 1;
                    row++;
                }
            } else {
                if (column > LAYOUT_MAX_COL_COUNT) {
                    column = 11;
                    row++;
                }
            }
            if (pinTypeComboBoxId > PIN_COUNT) {
                pinTypeComboBoxId = PIN_COUNT;
            }
            String[] pinTypes = bbbMap.getValueByKey(pinTypeComboBoxId);
            ObservableList<String> pinTypeOptions = FXCollections.observableArrayList(pinTypes);

            if (column == 1 || column == 10 || column == 11 || column == 20) {
                //reserved in case any element need to be added to the outer edge of layout
            } else if (column == 2 || column == 9 || column == 12 || column == 19) {
                addCheckBoxToLayout();
            } else if (column == 3 || column == 8 || column == 13 || column == 18) {
                addInputOutputComboBoxToLayout();
            } else if (column == 4 || column == 7 || column == 14 || column == 17) {
                addPinTypeComboBoxToLayout(pinTypeOptions);
            } else if (column == 5 || column == 6 || column == 15 || column == 16) {
                addButtonToLayout();
            } else {
                logger.log(LAYOUT_CREATION_FAILED);
                alerts.createErrorAlert(null, LAYOUT_CREATION_FAILED);
            }

            if (column == PIN_DOUBLE_MAX_COL_COUNT && row == NUMBER_OF_ROWS_IN_LAYOUT) {
                secondRow = true;
                row = 1;
            }
            column++;
        }
    }

    private void disableGridElements(ArrayList<Button> buttons, ArrayList<ComboBox<String>> pinTypeComboBoxes, final ArrayList<ComboBox<String>> inputOutputComboBoxes, ArrayList<CheckBox> checkBoxes) {
        for (int i = 0; i < PIN_COUNT; i++) {
            String selectedItem = pinTypeComboBoxes.get(i).getSelectionModel().getSelectedItem();
            if (Pin.PWR5.equals(selectedItem)
                    || Pin.PWR3.equals(selectedItem)
                    || Pin.AI.equals(selectedItem)
                    || Pin.PWR_BTN.equals(selectedItem)
                    || Pin.SYS_RST.equals(selectedItem)
                    || Pin.GND.equals(selectedItem)) {
                pinTypeComboBoxes.get(i).setDisable(true);
                inputOutputComboBoxes.get(i).setVisible(false);
                checkBoxes.get(i).setVisible(false);
                buttons.get(i).setDisable(true);
            }
        }
    }

    private void addCheckBoxToLayout() {
        final CheckBox checkBox = new CheckBox();
        checkBox.setId(String.valueOf(checkBoxId));
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue observableValue, Boolean oldValue, Boolean newValue) {
                int checkBoxId = Integer.valueOf(checkBox.getId()) - 1;

                buttons.get(checkBoxId).setStyle(DEFAULT_BUTTON_STYLE);
                buttons.get(checkBoxId).setStyle("-fx-font-size: 9pt");

                toggleElementsEnabled(newValue, checkBoxId);

                String buttonText = buttons.get(checkBoxId).getText().trim();
                String ioType = inputOutputComboBoxes.get(checkBoxId).getSelectionModel().getSelectedItem();
                String pinType = pinTypeComboBoxes.get(checkBoxId).getSelectionModel().getSelectedItem();

                Pin pin = new Pin(Integer.valueOf(buttonText), ioType, pinType);

                if (newValue) {
                    addPin(pin);
                } else {
                    removePin(pin);
                }

                if (pins != null || !pins.isEmpty()) {
                    root.handleCheckBoxClick(pins);
                }
            }
        });
        checkBoxes.add(checkBox);
        checkBoxId++;
        gridPane.add(checkBox, column, row);
    }

    private void addInputOutputComboBoxToLayout() {
        ObservableList<String> inputOutputOptions = FXCollections.observableArrayList(GPIO_OUTPUT, GPIO_INPUT);
        final ComboBox<String> inputOutputComboBox = new ComboBox<>(inputOutputOptions);
        inputOutputComboBox.setId(String.valueOf(inputOutputComboBoxId));
        inputOutputComboBox.setPrefWidth(IO_COMBO_BOX_PREF_WIDTH);
        inputOutputComboBox.getSelectionModel().selectFirst();
        inputOutputComboBoxes.add(inputOutputComboBox);
        inputOutputComboBoxId++;
        inputOutputComboBox.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                String currentlySelectedItem = inputOutputComboBox.getSelectionModel().getSelectedItem();
                int inputOutputComboBoxId = Integer.valueOf(inputOutputComboBox.getId()) - 1;

                if (GPIO_INPUT.equals(currentlySelectedItem)) {
                    buttons.get(inputOutputComboBoxId).setDisable(true);
                    int pinId = Integer.valueOf(buttons.get(inputOutputComboBoxId).getText().trim());
                    Pin turnOffOutputPin = new Pin(pinId, GPIO_OUTPUT, Pin.GPIO);
                    turnOffOutputPin.setValue(false);
                    root.handlePinButtonClick(turnOffOutputPin, false);

                    Pin getInputPin = new Pin(pinId, GPIO_INPUT, Pin.GPIO);
                    root.handlePinButtonClick(getInputPin, true);
                } else {
                    buttons.get(inputOutputComboBoxId).setDisable(false);
                }
            }
        });
        gridPane.add(inputOutputComboBox, column, row);
    }

    private void addPinTypeComboBoxToLayout(ObservableList<String> pinTypeOptions) {
        final ComboBox<String> pinTypeComboBox = new ComboBox<>(pinTypeOptions);
        pinTypeComboBox.setId(String.valueOf(pinTypeComboBoxId));
        pinTypeComboBox.setPrefWidth(PIN_TYPE_COMBO_BOX_PREF_WIDTH);
        pinTypeComboBox.getSelectionModel().selectFirst();
        pinTypeComboBoxes.add(pinTypeComboBox);
        pinTypeComboBoxId++;
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0) {
                int pinTypeComboBoxId = Integer.valueOf(pinTypeComboBox.getId()) - 1;
                ComboBox<String> currentRowComboBox = inputOutputComboBoxes.get(pinTypeComboBoxId);
                Button currentRowButton = buttons.get(pinTypeComboBoxId);
                CheckBox currentRowCheckBox = checkBoxes.get(pinTypeComboBoxId);
                setElementsVisibility(pinTypeComboBox.getSelectionModel().getSelectedItem(), currentRowComboBox, currentRowButton, currentRowCheckBox.isSelected());
                synchronizePinElements(pinTypeComboBox);
            }
        };
        pinTypeComboBox.setOnAction(eventHandler);
        gridPane.add(pinTypeComboBox, column, row);
    }

    private void addButtonToLayout() {
        final Button button = new Button();
        button.setId(String.valueOf(buttonId));
        button.setStyle("-fx-font-size: 9pt;");
        button.setUserData('0');
        button.setMinSize(BUTTON_MIN_SIZE_WIDTH, BUTTON_MIN_SIZE_HEIGHT);
        button.setText(String.valueOf(buttonId));
        buttons.add(button);
        buttonId++;
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                int buttonId = Integer.valueOf(button.getId()) - 1;

                String buttonText = button.getText().trim();
                String pinType = pinTypeComboBoxes.get(buttonId).getSelectionModel().getSelectedItem();
                String ioType = inputOutputComboBoxes.get(buttonId).getSelectionModel().getSelectedItem();

                Pin pin = new Pin(Integer.valueOf(buttonText), ioType, pinType);
                root.handlePinButtonClick(pin, true);
            }
        });
        gridPane.add(button, column, row);
    }

    private void toggleElementsEnabled(boolean newValue, int checkBoxId) {
        if (newValue) {
            enableRowElements(checkBoxId);
        } else {
            disableRowElements(checkBoxId);
        }

        setElementsVisibility(pinTypeComboBoxes.get(checkBoxId).getSelectionModel().getSelectedItem(),
                inputOutputComboBoxes.get(checkBoxId), buttons.get(checkBoxId), newValue);
    }

    private void enableRowElements(int checkBoxId) {
        ComboBox currentPinTypeComboBox = pinTypeComboBoxes.get(checkBoxId);
        currentPinTypeComboBox.setDisable(false);
        inputOutputComboBoxes.get(checkBoxId).setDisable(false);
        buttons.get(checkBoxId).setDisable(false);
        if (Pin.I2C.equals(currentPinTypeComboBox.getSelectionModel().getSelectedItem())) {
            for (ComboBox<String> pinTypeComboBox : pinTypeComboBoxes) {
                if (Pin.I2C.equals(pinTypeComboBox.getSelectionModel().getSelectedItem())) {
                    int comboBoxId = Integer.valueOf(pinTypeComboBox.getId()) - 1;
                    checkBoxes.get(comboBoxId).setSelected(true);
                }
            }
        } else if (Pin.SPI.equals(currentPinTypeComboBox.getSelectionModel().getSelectedItem())) {
            for (ComboBox<String> pinTypeComboBox : pinTypeComboBoxes) {
                if (Pin.SPI.equals(pinTypeComboBox.getSelectionModel().getSelectedItem())) {
                    int comboBoxId = Integer.valueOf(pinTypeComboBox.getId()) - 1;
                    checkBoxes.get(comboBoxId).setSelected(true);
                }
            }
        }
    }

    private void disableRowElements(int checkBoxId) {
        ComboBox currentPinTypeComboBox = pinTypeComboBoxes.get(checkBoxId);
        inputOutputComboBoxes.get(checkBoxId).setDisable(true);
        currentPinTypeComboBox.setDisable(true);
        buttons.get(checkBoxId).setDisable(true);
        if (Pin.I2C.equals(currentPinTypeComboBox.getSelectionModel().getSelectedItem())) {
            for (ComboBox<String> pinTypeComboBox : pinTypeComboBoxes) {
                if (Pin.I2C.equals(pinTypeComboBox.getSelectionModel().getSelectedItem())) {
                    int comboBoxId = Integer.valueOf(pinTypeComboBox.getId()) - 1;
                    checkBoxes.get(comboBoxId).setSelected(false);
                }
            }
        } else if (Pin.SPI.equals(currentPinTypeComboBox.getSelectionModel().getSelectedItem())) {
            for (ComboBox<String> pinTypeComboBox : pinTypeComboBoxes) {
                if (Pin.SPI.equals(pinTypeComboBox.getSelectionModel().getSelectedItem())) {
                    int comboBoxId = Integer.valueOf(pinTypeComboBox.getId()) - 1;
                    checkBoxes.get(comboBoxId).setSelected(false);
                }
            }
        }
    }

    private void setElementsVisibility(String pinTypeComboBox, ComboBox<String> inputOutputComboBox, Button button, boolean checkBoxSelected) {
        if (checkBoxSelected) {
            switch (pinTypeComboBox) {
                case Pin.I2C:
                    inputOutputComboBox.setVisible(false);
                    button.setDisable(false);
                    break;
                case Pin.SPI:
                    inputOutputComboBox.setVisible(false);
                    button.setDisable(false);
                    break;
                case Pin.UART:
                    inputOutputComboBox.setVisible(false);
                    button.setDisable(true);
                    break;
                case Pin.PWM:
                    inputOutputComboBox.setVisible(false);
                    button.setDisable(true);
                    break;
                default:
                    inputOutputComboBox.setVisible(true);
                    if (GPIO_OUTPUT.equals(inputOutputComboBox.getSelectionModel().getSelectedItem())) {
                        button.setDisable(false);
                    } else {
                        button.setDisable(true);
                    }
                    break;
            }
        }
    }

    private void synchronizePinElements(ComboBox<String> pinTypeComboBox) {
        String pinTypeSelectedItem = pinTypeComboBox.getSelectionModel().getSelectedItem();
        if (Pin.I2C.equals(pinTypeSelectedItem)) {
            for (ComboBox<String> comboBox : pinTypeComboBoxes) {
                if (comboBox.getItems().contains(Pin.I2C)) {
                    comboBox.getSelectionModel().select(Pin.I2C);
                    checkBoxes.get(Integer.valueOf(comboBox.getId()) - 1).setSelected(true);
                }
            }
        } else if (Pin.SPI.equals(pinTypeSelectedItem) && pinTypeComboBox.getItems().contains(Pin.SPI)) {
            for (ComboBox<String> comboBox : pinTypeComboBoxes) {
                if (comboBox.getItems().contains(Pin.SPI)) {
                    comboBox.getSelectionModel().select(Pin.SPI);
                    checkBoxes.get(Integer.valueOf(comboBox.getId()) - 1).setSelected(true);
                }
            }
        } else if (Pin.GPIO.equals(pinTypeSelectedItem)) {
            if (pinTypeComboBox.getItems().contains(Pin.I2C)) {
                for (ComboBox<String> comboBox : pinTypeComboBoxes) {
                    if (comboBox.getItems().contains(Pin.I2C)) {
                        comboBox.getSelectionModel().select(Pin.GPIO);
                    }
                }
            }
            if (pinTypeComboBox.getItems().contains(Pin.SPI)) {
                for (ComboBox<String> comboBox : pinTypeComboBoxes) {
                    if (comboBox.getItems().contains(Pin.SPI)) {
                        comboBox.getSelectionModel().select(Pin.GPIO);
                    }
                }
            }
        }
    }

    private void addPin(Pin pin) {
        if (!pins.contains(pin)) {
            pins.add(pin);
        }
    }

    private void removePin(Pin pinToRemove) {
        for (Pin pin : pins) {
            if (pin.getPinId() == pinToRemove.getPinId()) {
                pins.remove(pin);
                return;
            }
        }
    }

    @Override
    public List<Pin> getCheckedPins() {
        return pins;
    }

    @Override
    public void setColorOnPins(List<Pin> pins) {
        for (Pin pin : pins) {
            int pinId = pin.getPinId() - 1;
            if (pin.isValuePositive()) {
                buttons.get(pinId).setStyle(BUTTON_COLOR_GREEN);
            } else {
                buttons.get(pinId).setStyle(BUTTON_COLOR_RED);
            }
        }
    }
}
