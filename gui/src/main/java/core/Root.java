package core;

import controllers.MenuViewController;
import core.networking.Networking;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import layouts.BeagleBoneLayout;
import layouts.CubieBoardLayout;
import layouts.EmbeddedLayout;
import layouts.RaspberryLayout;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Root extends Application {

    static final String CSS_FILE = Root.class.getClassLoader().getResource("Style.css").toExternalForm();
    private final static String APPLICATION_TITLE = "Embedded GUI";
    private final static URL ROOT_VIEW = Root.class.getResource("/rootView.fxml");
    private final static URL MENU_VIEW = Root.class.getResource("/menuView.fxml");
    private final static URL LOG_VIEW = Root.class.getResource("/logView.fxml");
    private final static URL RASPBERRY_VIEW_2 = Root.class.getResource("/RaspberryView2.fxml");
    private final static URL BEAGLEBONE_VIEW_2 = Root.class.getResource("/BeagleBoneView2.fxml");
    private final static URL CUBIEBOARD_VIEW_2 = Root.class.getResource("/CubieBoardView2.fxml");
    public final static String RASPBERRY_PI_2_NAME = "Raspberry Pi 2";
    public final static String BEAGLEBONE_BLACK_NAME = "BeagleBone Black";
    public final static String CUBIEBOARD_NAME = "CubieBoard";
    private final static String ROOT_INITIALIZATION_FAILED = "Root layout initialization failed";
    private final static String MENU_INITIALIZATION_FAILED = "Menu layout initialization failed";
    private final static String LOG_INITIALIZATION_FAILED = "Log layout initialization failed";
    private final static String FXML_FILE_NOT_FOUND = "Cannot load .fxml file for appropriate layout";
    private final static String LAYOUT_UNKNOWN = "Unknown layout.";
    private final static String CANNOT_SEND_MACRO = "Cannot send macro. Please send macro with appropriate button.";
    private final static String CANNOT_SEND_I2C = "Cannot send I2C message. Please choose I2C option to send valid I2C message";
    private final static String CANNOT_SEND_SPI = "Cannot send SPI message. Please choose SPI option to send valid SPI message";
    private static final String MESSAGE_SEND_FAILED = "Failed to send message";

    private Networking networking;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private GridPane embeddedView;
    private AlertsImpl alerts = new AlertsImpl();
    private Validations validations;
    private Logger logger;
    private String visibleLayout;
    private EmbeddedLayout layout;
    private MenuViewController menuViewController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(APPLICATION_TITLE);

        networking = new Networking();

        initRootLayout();
        showLogLayout();
        showMenuLayout();

        validations = new Validations(logger);
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ROOT_VIEW);
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(CSS_FILE);

            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

            primaryStage.setX(primaryScreenBounds.getMinX());
            primaryStage.setY(primaryScreenBounds.getMinY());
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());

            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setResizable(false);

            primaryStage.show();

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    networking.disconnect();
                    Platform.exit();
                    System.exit(0);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(ROOT_INITIALIZATION_FAILED, e);
        }
    }

    private void showMenuLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MENU_VIEW);
            HBox menuView = loader.load();

            rootLayout.setLeft(menuView);

            menuViewController = loader.getController();
            menuViewController.setMainApp(this, networking, logger);
        } catch (IOException e) {
            throw new RuntimeException(MENU_INITIALIZATION_FAILED, e);
        }

    }

    private void showLogLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(LOG_VIEW);

            VBox logHolder = loader.load();

            HBox logViewHolder = (HBox) logHolder.getChildren().get(1);
            TextArea logView = (TextArea) logViewHolder.getChildren().get(0);

            logger = new Logger(logView);
            networking.setLogger(logger);

            rootLayout.setRight(logHolder);
        } catch (IOException e) {
            throw new RuntimeException(LOG_INITIALIZATION_FAILED, e);
        }
    }

    public void showEmbeddedLayout(String selectedSystem) {
        try {
            FXMLLoader loader = new FXMLLoader();
            switch (selectedSystem) {
                case RASPBERRY_PI_2_NAME:
                    loader.setLocation(RASPBERRY_VIEW_2);
                    RaspberryLayout raspberryLayout = new RaspberryLayout();
                    layout = raspberryLayout;
                    GridPane rpiGridPane = loader.load();
                    this.embeddedView = rpiGridPane;
                    rootLayout.setCenter(rpiGridPane);
                    this.visibleLayout = RASPBERRY_PI_2_NAME;
                    raspberryLayout.setMainApp(this, logger);
                    break;
                case BEAGLEBONE_BLACK_NAME:
                    loader.setLocation(BEAGLEBONE_VIEW_2);
                    BeagleBoneLayout beagleBoneLayout = new BeagleBoneLayout();
                    layout = beagleBoneLayout;
                    GridPane bbbGridPane = loader.load();
                    this.embeddedView = bbbGridPane;
                    rootLayout.setCenter(bbbGridPane);
                    this.visibleLayout = BEAGLEBONE_BLACK_NAME;
                    beagleBoneLayout.setMainApp(this, logger);
                    break;
                case CUBIEBOARD_NAME:
                    loader.setLocation(CUBIEBOARD_VIEW_2);
                    CubieBoardLayout cubieBoardLayout = new CubieBoardLayout();
                    layout = cubieBoardLayout;
                    GridPane cbGridPane = loader.load();
                    this.embeddedView = cbGridPane;
                    rootLayout.setCenter(cbGridPane);
                    this.visibleLayout = CUBIEBOARD_NAME;
                    cubieBoardLayout.setMainApp(this, networking);
                    break;
                default:
                    visibleLayout = null;
                    layout = null;
                    alerts.createErrorAlert(null, LAYOUT_UNKNOWN);
                    logger.log(LAYOUT_UNKNOWN);
            }
        } catch (IOException e) {

            alerts.createErrorAlert(null, FXML_FILE_NOT_FOUND);
            logger.log(FXML_FILE_NOT_FOUND);
            logger.log(e.toString());
        }
    }

    public void handlePinButtonClick(Pin pin) {
        String selectedCommandMode = menuViewController.getCommandMode();
        String pinType = pin.getPinType();

        if (!Pin.GPIO.equals(pin.getPinType())) {
            if (MenuViewController.OBSERVABLE_MACRO_TEXT.equals(selectedCommandMode)) {
                alerts.createInfoAlert(null, CANNOT_SEND_MACRO);
                logger.log(CANNOT_SEND_MACRO);
                return;
            }

            if (MenuViewController.OBSERVABLE_I2C_TEXT.equals(selectedCommandMode) && !Pin.I2C.equals(pinType)) {
                alerts.createInfoAlert(null, CANNOT_SEND_I2C);
                logger.log(CANNOT_SEND_I2C);
                return;
            }

            if (MenuViewController.OBSERVABLE_SPI_TEXT.equals(selectedCommandMode) && !Pin.SPI.equals(pinType)) {
                alerts.createInfoAlert(null, CANNOT_SEND_SPI);
                logger.log(CANNOT_SEND_SPI);
                return;
            }

            String address = menuViewController.getAddress();
            String command = menuViewController.getCommand();

            switch (selectedCommandMode) {
                case MenuViewController.OBSERVABLE_I2C_TEXT:
                    if (validations.isHexaStringValid(command) && validations.isPhysicalAddressValid(address)) {
                        logger.log("I2C message akoze sent na pin: " + pin + " with address: " + address + " and command: " + command);
                        // TODO: 18.8.2016 NETWORK_OP
//                    networking.sendValueToI2CPin(pin, address, command);
                    }
                    return;
                // TODO: 17.8.2016 ak chcem odpoved od networkingu treba dat parameter to metody this a v metode to bude callback
                case MenuViewController.OBSERVABLE_SPI_TEXT:
                    if (validations.isHexaStringValid(command) && validations.isPhysicalAddressValid(address)) {
                        logger.log("SPI message akoze sent na pin: " + pin + " with address: " + address + " and command: " + command);
                        // TODO: 18.8.2016 NETWORK_OP
//                    networking.sendValueToSpiPin(pin, address, command);
                    }
                    return;
                default:
                    logger.log(MESSAGE_SEND_FAILED);
                    break;
            }
        } else {
            // TODO: 18.8.2016 NETWORK_OP
//            networking.toggleGpioPin(pin);
            logger.log("Pin " + pin.getPinId() + " sent.");
        }
    }

    public void handleCheckBoxClick(ArrayList<Pin> pins) {
        if (menuViewController.isSendRequestCheckBoxChecked()) {
            networking.updatePinsInRequestStatus(layout, pins);
        }
    }

    public String getVisibleLayout() {
        return visibleLayout;
    }

    public void removeEmbeddedLayout() {
        rootLayout.getChildren().remove(embeddedView);
        layout = null;
    }

    public BorderPane getRootLayout() {
        return this.rootLayout;
    }

    public List<Pin> getCheckedPins() {
        return layout.getCheckedPins();
    }

    public boolean isLayoutDisplayed() {
        return layout instanceof EmbeddedLayout;
    }

    public EmbeddedLayout getRequestStatusCallback() {
        return layout;
    }
}
