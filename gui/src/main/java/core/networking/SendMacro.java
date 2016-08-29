package core.networking;

import core.Logger;
import core.Pin;
import core.Validations;
import javafx.application.Platform;
import layouts.EmbeddedLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

class SendMacro implements Callable<NetworkingParams> {

    private EmbeddedLayout pinCallback;
    private PopupDismiss popupCallback;
    private List<String> commands;
    private NetworkingParams params;
    private Logger logger;
    private Validations validations;

    SendMacro(EmbeddedLayout pinCallback, PopupDismiss popupCallback, NetworkingParams params, Logger logger, List<String> commands) {
        this.pinCallback = pinCallback;
        this.popupCallback = popupCallback;
        this.commands = commands;
        this.params = params;
        this.logger = logger;
        this.validations = new Validations(logger);
    }

    @Override
    public NetworkingParams call() throws Exception {
        for (String command : commands) {
            command = command.substring(0, command.lastIndexOf(";"));
            String finalCommand = command;
            Platform.runLater(() -> logger.log("Sending: " + finalCommand));
            sendMacroCommand(command);
        }
        Platform.runLater(() -> logger.log("Macro sent."));
        Platform.runLater(() -> popupCallback.dismissPopup());
        return params;
    }

    private void sendMacroCommand(String command) throws Exception {
        if (validations.isOnlyDigitString(command)) {
            Thread.sleep(Integer.valueOf(command));
            return;
        }

        NetworkingParams networkingParams;
        int pinId;
        String address;
        String hexaCommand;

        if (command.startsWith("GPIO")) {
            pinId = Integer.valueOf(command.substring(5, 7));
            Pin pin = new Pin(pinId, "O", "GPIO");
            String pinValue;
            if (command.substring(command.length() - 1).equals("1")) {
                pinValue = "1";
                pin.setValue(true);
            } else if (command.substring(command.length() - 1).equals("0")) {
                pinValue = "0";
                pin.setValue(false);
            } else {
                //Something went wrong. Setting no value. Server handles no value on GPIO pin as toggle. (TextArea validation should not allow this line)
                pinValue = "";
            }
            networkingParams = new ToggleGpioPin(pinCallback, getDateAndTime(), params, logger, pin, pinValue).call();
        } else if (command.startsWith("I2C")) {
            address = command.substring(4, 6);
            pinId = Integer.valueOf(command.substring(6, 8));
            hexaCommand = command.substring(8);
            Pin pin = new Pin(pinId, "O", "I2C");
            networkingParams = new SendValueToI2CPin(getDateAndTime(), params, logger, pin, address, hexaCommand).call();
        } else if (command.startsWith("SPI")) {
            address = command.substring(4, 6);
            pinId = Integer.valueOf(command.substring(6, 8));
            hexaCommand = command.substring(8);
            Pin pin = new Pin(pinId, "O", "SPI");
            networkingParams = new SendValueToSpiPin(getDateAndTime(), params, logger, pin, address, hexaCommand).call();
        } else {
            Platform.runLater(() -> logger.log("Not supported command in macro."));
            return;
        }

        if (networkingParams != null) {
            Platform.runLater(() -> logger.log(networkingParams.message));
        }
    }

    private String getDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat(Networking.DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
}
