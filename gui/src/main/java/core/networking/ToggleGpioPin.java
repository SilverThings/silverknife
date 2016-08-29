package core.networking;

import core.Logger;
import core.Pin;
import javafx.application.Platform;
import layouts.EmbeddedLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class ToggleGpioPin implements Callable<NetworkingParams> {

    private static final int TWO_DIGITS_VALUE = 10;

    private EmbeddedLayout callback;
    private NetworkingParams params;
    private String dateAndTime;
    private Logger logger;
    private Pin pin;
    private String pinValue;

    ToggleGpioPin(EmbeddedLayout callback, String dateAndTime, NetworkingParams params, Logger logger, Pin pin, String pinValue) {
        this.callback = callback;
        this.params = params;
        this.dateAndTime = dateAndTime;
        this.logger = logger;
        this.pin = pin;
        this.pinValue = pinValue;
    }

    @Override
    public NetworkingParams call() throws Exception {
        if (params != null) {
            if (pin.getPinId() < TWO_DIGITS_VALUE) {
                String message = dateAndTime + pin.getIoType() + pin.getPinType() + ":0" + pin.getPinId() + pinValue;
                Platform.runLater(() -> logger.log("Sending: " + message));
                params.out.println(message);
            } else {
                String message = dateAndTime + pin.getIoType() + pin.getPinType() + ":" + pin.getPinId() + pinValue;
                Platform.runLater(() -> logger.log("Sending: " + message));
                params.out.println(message);
            }
            String response = params.in.readLine();
            params.message = response;
            Platform.runLater(() -> logger.log(response));
        } else {
            Platform.runLater(() -> logger.log("Not connected. Cannot toggle GPIO pin."));
        }

        setColorOnPin();
        return params;
    }

    private void setColorOnPin() {
        String value = params.message.substring(params.message.length() - 1);
        if (value.equals("1")) {
            pin.setValue(true);
        } else  {
            pin.setValue(false);
        }

        List<Pin> pins = new ArrayList<>();
        pins.add(pin);
        Platform.runLater(() -> callback.setColorOnPins(pins));
    }
}
