package core;

import hashmaps.RaspberryHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.net.util.IPAddressUtil;
import javafx.concurrent.*;

@SuppressWarnings("restriction")
public class Main extends Application implements EventHandler<ActionEvent> {

	private static Button connectButton;
	private static ArrayList<Button> buttons;
	private static ArrayList<CheckBox> checkBoxes;
	private static ArrayList<String> checkedCheckBoxes;

	private static final int PORT = 18924;
	private static final Pattern BODY_PATTERN = Pattern.compile("([0-9a-fA-F][0-9a-fA-F])*");

	private GridPane gridPane;
	private TextField messageTextField;

	private String ip = "192.168.168.2";
	private Networking networking = new Networking();

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		BorderPane borderPane = new BorderPane();
		TextField textField = new TextField();
		textField.setMaxWidth(200);
		checkedCheckBoxes = new ArrayList<String>();

		messageTextField = new TextField();
		messageTextField.setPromptText("I2C message");
		messageTextField.setMaxWidth(150);
		messageTextField.setAlignment(Pos.CENTER);
		setI2CMessageValidationBorder("", messageTextField);
		messageTextField.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				setI2CMessageValidationBorder(newValue, messageTextField);
			}
		});

		setIpAddressTextField(textField);
		setButtons();
		setLayout(borderPane, textField);

		Scene scene = new Scene(borderPane, 800, 900);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("Custom style.css").toExternalForm());
		borderPane.requestFocus();
		primaryStage.setTitle("Embedded systems control");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		setGridElements();
		gridPane.setVisible(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent arg0) {
				networking.disconnect();
				Platform.exit();
			}
		});
	}

	private void setIpAddressTextField(final TextField textField) {
		textField.setPromptText("Default IP address");
		setIpValidationBorder(ip, textField);
		textField.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				if (textField.getPromptText().startsWith("D")) {
					textField.setPromptText("Enter IP address");
				}
				ip = newValue;
				setIpValidationBorder(ip, textField);
			}
		});
	}

	public void handle(ActionEvent event) {
		if (event.getSource() == connectButton && isIpAddress(ip)) {
			connectButton.setDisable(true);
			networking.toggleConnectionStatus(ip, PORT, connectButton.getText());
			if (Networking.connected) {
				connectButton.setDisable(false);
				connectButton.setText("Disconnect");
				Task<Void> task = new Task<Void>() {
					public Void call() throws Exception {
						while (Networking.connected) {
							Platform.runLater(new Runnable() {
								public void run() {
									setUiFromResponse();
								}
							});
							Thread.sleep(1000);
						}
						return null;
					}
				};
				Thread thread = new Thread(task);
				thread.start();
				gridPane.setVisible(true);
			} else {
				connectButton.setText("Connect");
				connectButton.setDisable(false);
				gridPane.setVisible(false);
				Iterator<String> iterator = checkedCheckBoxes.iterator();
				while (iterator.hasNext()) {
					String value = iterator.next();
					CheckBox cb = checkBoxes.get(Integer.valueOf(value) - 1);
					iterator.remove();
					cb.setSelected(false);
				}
			}
			//TODO I2C message visibility
		}
	}

	private void setLayout(BorderPane borderPane, TextField textField) {
		HBox topBox = new HBox();
		topBox.setAlignment(Pos.CENTER);
		topBox.getChildren().add(textField);
		topBox.getChildren().add(connectButton);
		BorderPane topPane = new BorderPane();
		StackPane left = new StackPane();
		StackPane right = new StackPane();
		left.setPrefWidth(200);
		right.setPrefWidth(200);
		right.getChildren().add(connectButton);
		right.setAlignment(Pos.TOP_RIGHT);
		topPane.setLeft(left);
		topPane.setRight(right);
		topPane.setCenter(textField);
		borderPane.setTop(topPane);

		gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);

		VBox centerBox = new VBox();
		centerBox.setAlignment(Pos.TOP_CENTER);
		centerBox.getChildren().add(messageTextField);
		centerBox.getChildren().add(gridPane);
//		centerBox.setStyle("-fx-background-color: green;");
		borderPane.setCenter(centerBox);
	}

	private void setGridElements() {
		checkBoxes = new ArrayList<CheckBox>();
		ArrayList<TextField> textFields = new ArrayList<TextField>();
		ArrayList<ComboBox<String>> inputOutputComboBoxes = new ArrayList<ComboBox<String>>();
		ArrayList<ComboBox<String>> pinTypeComboBoxes = new ArrayList<ComboBox<String>>();
		buttons = new ArrayList<Button>();

		createGridElements(buttons, pinTypeComboBoxes, inputOutputComboBoxes, textFields, checkBoxes);
		disableGridElements(buttons, pinTypeComboBoxes, inputOutputComboBoxes, textFields, checkBoxes);
		for (CheckBox checkBox : checkBoxes) {
			toggleElementsEnabled(false, checkBox, textFields, inputOutputComboBoxes, pinTypeComboBoxes, buttons,
					checkBoxes);
		}
	}

	private void createGridElements(final ArrayList<Button> buttons,
			final ArrayList<ComboBox<String>> pinTypeComboBoxes,
			final ArrayList<ComboBox<String>> inputOutputComboBoxes, final ArrayList<TextField> textFields,
			final ArrayList<CheckBox> checkBoxes) {
		int row = 1;
		int col = 1;
		int buttonId = 1;
		int pinTypeComboBoxId = 1;
		int inputOutputComboBoxId = 1;
		int textFieldId = 1;
		int checkBoxId = 1;

		RaspberryHashMap piMap = new RaspberryHashMap();
		piMap.createHashMap();

		for (int i = 1; i <= 200; i++) {
			if (col == 11) {
				col = 1;
				row++;
			}
			if (pinTypeComboBoxId > 40) {
				pinTypeComboBoxId = 40;
			}
			String[] pinTypes = piMap.getValueByKey(pinTypeComboBoxId);
			ObservableList<String> pinTypeOtions;
			if (pinTypes.length > 1) {
				pinTypeOtions = FXCollections.observableArrayList(pinTypes[0], pinTypes[1]);
			} else {
				pinTypeOtions = FXCollections.observableArrayList(pinTypes[0]);
			}

			final ComboBox<String> pinTypeComboBox;
			final ComboBox<String> inputOutputComboBox;
			final Button button;
			final TextField textField;
			final CheckBox checkBox;

			if (col == 1 || col == 10) {
				checkBox = new CheckBox();
				checkBox.setId(String.valueOf(checkBoxId));
				checkBox.setSelected(false);
				checkBoxes.add(checkBox);
				checkBoxId++;
				checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
					@SuppressWarnings("rawtypes")
					public void changed(ObservableValue observableValue, Boolean oldValue, Boolean newValue) {
						toggleElementsEnabled(newValue, checkBox, textFields, inputOutputComboBoxes, pinTypeComboBoxes,
								buttons, checkBoxes);
						if (newValue) {
							checkedCheckBoxes.add(checkBox.getId());
							networking.addPinToSend(checkBox.getId());
						} else if (!newValue) {
							networking.removePinToSend(checkBox.getId());
							checkedCheckBoxes.remove(checkBox.getId());
						}
					}
				});
				gridPane.add(checkBox, col, row);
			} else if (col == 4 || col == 7) {
				pinTypeComboBox = new ComboBox<String>(pinTypeOtions);
				pinTypeComboBox.setId(String.valueOf(pinTypeComboBoxId));
				pinTypeComboBox.setPrefWidth(110);
				pinTypeComboBox.getSelectionModel().selectFirst();
				pinTypeComboBoxes.add(pinTypeComboBox);
				pinTypeComboBoxId++;
				EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						setElementsVisibility(pinTypeComboBox,
								inputOutputComboBoxes.get(Integer.valueOf(pinTypeComboBox.getId()) - 1),
								textFields.get(Integer.valueOf(pinTypeComboBox.getId()) - 1),
								buttons.get(Integer.valueOf(pinTypeComboBox.getId()) - 1),
								checkBoxes.get(Integer.valueOf(pinTypeComboBox.getId()) - 1));
						synchronizeI2cElements(pinTypeComboBox, pinTypeComboBoxes, textFields, inputOutputComboBoxes,
								checkBoxes, this);
					}
				};
				pinTypeComboBox.setOnAction(eventHandler);
				gridPane.add(pinTypeComboBox, col, row);
			} else if (col == 2 || col == 9) {
				textField = new TextField();
				textField.setId(String.valueOf(textFieldId));
				textField.setPrefWidth(80);
				textField.setVisible(false);
				textField.setPromptText("Address");
				setI2CAddressValidationBorder("", textField);
				textFields.add(textField);
				textFieldId++;
				textField.textProperty().addListener(new ChangeListener<String>() {
					@SuppressWarnings("rawtypes")
					public void changed(ObservableValue observableValue, String oldValue, String newValue) {
						setI2CAddressValidationBorder(newValue, textField);
					}
				});
				if (Integer.valueOf(textField.getId()) != 5) {
					gridPane.add(textField, col, row);
				}
			} else if (col == 3 || col == 8) {
				ObservableList<String> inputOutputOtions = FXCollections.observableArrayList("OUT", "IN");
				inputOutputComboBox = new ComboBox<String>(inputOutputOtions);
				inputOutputComboBox.setId(String.valueOf(inputOutputComboBoxId));
				inputOutputComboBox.setPrefWidth(80);
				inputOutputComboBox.getSelectionModel().selectFirst();
				inputOutputComboBoxes.add(inputOutputComboBox);
				inputOutputComboBoxId++;
				inputOutputComboBox.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						if (inputOutputComboBox.getSelectionModel().getSelectedItem().equals("IN")) {
							buttons.get(Integer.valueOf(inputOutputComboBox.getId()) - 1).setDisable(true);
						} else {
							buttons.get(Integer.valueOf(inputOutputComboBox.getId()) - 1).setDisable(false);
						}
					}
				});
				gridPane.add(inputOutputComboBox, col, row);
			} else {
				button = new Button();
				button.setId(String.valueOf(buttonId));
				button.setUserData("0");
				button.setStyle("-fx-font-size: 12");
				button.setMinSize(35, 35);
				button.setText(String.valueOf(buttonId));
				buttons.add(button);
				buttonId++;
				button.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						boolean isIOComboBoxOut = inputOutputComboBoxes.get(Integer.valueOf(button.getId()) - 1)
								.getSelectionModel().getSelectedItem().equals("OUT");
						boolean isIOComboBoxVisible = inputOutputComboBoxes.get(Integer.valueOf(button.getId()) - 1)
								.isVisible();
						boolean isPinComboBoxI2C = pinTypeComboBoxes.get(Integer.valueOf(button.getId()) - 1)
								.getSelectionModel().getSelectedItem().equals("I2C");
						boolean isI2CAddressValid = isI2CAddressValid(textFields.get(
								Integer.valueOf(button.getId()) - 1).getText());
						boolean isMessageValid = isI2CMessageValid(messageTextField.getText());
						if (isIpAddress(ip)
								&& ((isIOComboBoxOut && isIOComboBoxVisible) || (isPinComboBoxI2C && isI2CAddressValid && isMessageValid))) {
							toggleButton(button, pinTypeComboBoxes.get(Integer.valueOf(button.getId()) - 1),
									textFields.get(Integer.valueOf(button.getId()) - 1), messageTextField.getText());
						}
					}
				});
				gridPane.add(button, col, row);
			}
			col++;
		}
	}

	private void setUiFromResponse() {
		String[] partialStatus = Networking.partialStatus;
		if (partialStatus != null) {
			for (String part : partialStatus) {
				if (part.length() > 10) {
					ResponseParser parser = new ResponseParser(part);
					String pinNumber = parser.getPinNumber();
					String pinValue = parser.getValue();
					if (pinNumber.startsWith("0")) {
						pinNumber = pinNumber.substring(1);
					}
					Button buttonToChange = buttons.get(Integer.valueOf(pinNumber) - 1);
					if (pinValue.equals("1")) {
						buttonToChange.setStyle("-fx-background-color: #99ff99;-fx-text-fill: black;");
					} else if (pinValue.equals("0")) {
						buttonToChange.setStyle("-fx-background-color: #ff9999;-fx-text-fill: black;");
					} else {
						buttonToChange.setStyle("-fx-background-color: white;-fx-text-fill: #03A9F4;");
					}
				}
			}
		}
	}

	private boolean isI2CMessageValid(String input) {
		Matcher i2CMatcher = BODY_PATTERN.matcher(input);
		if (i2CMatcher.matches() && !input.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isI2CAddressValid(String input) {
		if (!(input.length() == 4) || input.isEmpty() || !input.startsWith("0x")) {
			return false;
		} else
			return true;
	}

	private void toggleElementsEnabled(boolean newValue, CheckBox checkBox, ArrayList<TextField> textFields,
			ArrayList<ComboBox<String>> inputOutputComboBoxes, ArrayList<ComboBox<String>> pinTypeComboBoxes,
			ArrayList<Button> buttons, ArrayList<CheckBox> checkBoxes) {
		if (newValue) {
			textFields.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(false);
			inputOutputComboBoxes.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(false);
			pinTypeComboBoxes.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(false);
			buttons.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(false);
			for (ComboBox<String> comboBox : pinTypeComboBoxes) {
				if (comboBox.getItems().contains("I2C") && comboBox.getSelectionModel().getSelectedItem().equals("I2C")) {
					checkBoxes.get(Integer.valueOf(comboBox.getId()) - 1).setSelected(true);
				}
			}
		} else {
			textFields.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(true);
			inputOutputComboBoxes.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(true);
			pinTypeComboBoxes.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(true);
			buttons.get(Integer.valueOf(checkBox.getId()) - 1).setDisable(true);
			for (ComboBox<String> comboBox : pinTypeComboBoxes) {
				if (comboBox.getItems().contains("I2C") && comboBox.getSelectionModel().getSelectedItem().equals("I2C")) {
					checkBoxes.get(Integer.valueOf(comboBox.getId()) - 1).setSelected(false);
				}
			}
		}
		setElementsVisibility(pinTypeComboBoxes.get(Integer.valueOf(checkBox.getId()) - 1),
				inputOutputComboBoxes.get(Integer.valueOf(checkBox.getId()) - 1),
				textFields.get(Integer.valueOf(checkBox.getId()) - 1),
				buttons.get(Integer.valueOf(checkBox.getId()) - 1), checkBox);
	}

	private void disableGridElements(ArrayList<Button> buttons, ArrayList<ComboBox<String>> pinTypeComboBoxes,
			final ArrayList<ComboBox<String>> inputOutputComboBoxes, ArrayList<TextField> textFields,
			ArrayList<CheckBox> checkBoxes) {
		for (int i = 0; i < 40; i++) {
			if (pinTypeComboBoxes.get(i).getSelectionModel().getSelectedItem().equals("PWR5")
					|| pinTypeComboBoxes.get(i).getSelectionModel().getSelectedItem().equals("PWR3")
					|| pinTypeComboBoxes.get(i).getSelectionModel().getSelectedItem().equals("GND")
					|| pinTypeComboBoxes.get(i).getSelectionModel().getSelectedItem().equals("EEPROM")) {
				pinTypeComboBoxes.get(i).setDisable(true);
				inputOutputComboBoxes.get(i).setVisible(false);
				checkBoxes.get(i).setVisible(false);
				buttons.get(i).setDisable(true);
			}
		}
	}

	private void setElementsVisibility(ComboBox<String> pinTypeComboBox, ComboBox<String> inputOutputComboBox,
			TextField textField, Button button, CheckBox checkBox) {
		if (checkBox.isSelected()) {
			if (pinTypeComboBox.getSelectionModel().getSelectedItem().equals("I2C")) {
				textField.setVisible(true);
				inputOutputComboBox.setVisible(false);
				button.setDisable(false);
			} else if (pinTypeComboBox.getSelectionModel().getSelectedItem().equals("SPI")) {
				textField.setVisible(false);
				inputOutputComboBox.setVisible(false);
				button.setDisable(false);
			} else if (pinTypeComboBox.getSelectionModel().getSelectedItem().equals("UART")) {
				textField.setVisible(false);
				inputOutputComboBox.setVisible(false);
				button.setDisable(false);
			} else {
				textField.setVisible(false);
				inputOutputComboBox.setVisible(true);
				if (inputOutputComboBox.getSelectionModel().getSelectedItem().equals("OUT")) {
					button.setDisable(false);
				} else {
					button.setDisable(true);
				}
			}
		}
	}

	private void synchronizeI2cElements(ComboBox<String> pinTypeComboBox,
			ArrayList<ComboBox<String>> pinTypeComboBoxes, ArrayList<TextField> textFields,
			ArrayList<ComboBox<String>> inputOutputComboBoxes, ArrayList<CheckBox> checkBoxes,
			EventHandler<ActionEvent> eventHandler) {

		if (pinTypeComboBox.getSelectionModel().getSelectedItem().equals("I2C")) {
			for (ComboBox<String> comboBox : pinTypeComboBoxes) {
				if (comboBox.getItems().contains("I2C")) {
					comboBox.getSelectionModel().select("I2C");
					checkBoxes.get(Integer.valueOf(comboBox.getId()) - 1).setSelected(true);
				}
			}
		} else if (pinTypeComboBox.getSelectionModel().getSelectedItem().equals("GPIO")
				&& pinTypeComboBox.getItems().contains("I2C")) {
			for (ComboBox<String> comboBox : pinTypeComboBoxes) {
				if (comboBox.getItems().contains("I2C")) {
					comboBox.getSelectionModel().select("GPIO");
				}
			}
		}
	}

	private void toggleButton(Button button, ComboBox<String> pinTypeComboBox, TextField textField, String i2cMessage) {
		String valueToSend;
		if (!isPressed(button)) {
			valueToSend = "1";
			setPressed(button, "1");
		} else {
			valueToSend = "0";
			setPressed(button, "0");
		}
		networking.togglePin(button, pinTypeComboBox, textField, valueToSend, ip, PORT, i2cMessage);
	}

	private void setButtons() {
		connectButton = new Button("Connect");
		connectButton.setOnAction(this);
	}

	private void setIpValidationBorder(String IpAddress, TextField textField) {
		if (!isIpAddress(IpAddress)) {
			textField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
		} else {
			textField.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
		}
	}

	private void setI2CAddressValidationBorder(String input, TextField textField) {
		if (isI2CAddressValid(input)) {
			textField.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
		} else {
			textField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
		}
	}

	private void setI2CMessageValidationBorder(String input, TextField textField) {
		if (isI2CMessageValid(input)) {
			textField.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
		} else {
			textField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
		}
	}

	private boolean isIpAddress(String ip) {
		try {
			if (ip == null || ip.isEmpty() || !IPAddressUtil.isIPv4LiteralAddress(ip) || ip.endsWith(".")) {
				return false;
			}

			String[] parts = ip.split("\\.");

			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}

			return true;
		} catch (NumberFormatException e) {
			System.out.println(e);
			return false;
		}
	}

	public boolean isPressed(Button button) {
		if (button.getUserData() == "0") {
			return false;
		} else {
			return true;
		}
	}

	public void setPressed(Button button, String value) {
		button.setUserData(value);
	}
}
