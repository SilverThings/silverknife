package core.networking;

import core.Logger;
import core.Pin;
import core.ResponseParser;
import javafx.application.Platform;
import layouts.EmbeddedLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class StartRequestPinStatus implements Runnable {

    private final static String PIN_REQUEST_CODE = "REQUEST:990";
    private final static String RESPONSE_SPLITTER = ";";
    private final static int MINIMUM_RESPONSE_LENGTH = 15;

    private DisconnectCallback disconnectCallback;
    private EmbeddedLayout embeddedLayoutCallback;
    private NetworkingParams params;
    private Logger logger;
    private String dateAndTime;
    private List<Pin> pinList;

    StartRequestPinStatus(DisconnectCallback disconnectCallback, EmbeddedLayout embeddedLayoutCallback, NetworkingParams params, Logger logger, String dateAndTime, List<Pin> pinList) {
        this.disconnectCallback = disconnectCallback;
        this.embeddedLayoutCallback = embeddedLayoutCallback;
        this.params = params;
        this.logger = logger;
        this.dateAndTime = dateAndTime;
        this.pinList = pinList;
    }

    @Override
    public void run() {
        String pinsToRequest = "";
        if (pinList != null && !pinList.isEmpty()) {
            for (Pin pin : pinList) {
                if (pinsToRequest.isEmpty()) {
                    pinsToRequest = "" + pin.getPinId();
                } else {
                    pinsToRequest = pinsToRequest + ";" + pin.getPinId();
                }
            }
        }
        params.out.println(dateAndTime + PIN_REQUEST_CODE + pinsToRequest);
        try {
            params.message = params.in.readLine();
//            Platform.runLater(() -> logger.log(params.message));
            params = setColorOnPins();
        } catch (IOException e) {
            Platform.runLater(() -> logger.log("Failed to receive pin status."));
            disconnectCallback.serverDisconnected();
        }
    }

    private NetworkingParams setColorOnPins() {
        String response = params.message;
        if (response == null || response.isEmpty() || !(response.contains("START") && response.contains("END"))) {
            return params;
        }
        String[] partialStatus = response.split(RESPONSE_SPLITTER);
        List<Pin> pins = new ArrayList<>();
        if (partialStatus != null) {
            for (String part : partialStatus) {
                if (!(part.equals("START") || part.equals("END")) && part.length() > MINIMUM_RESPONSE_LENGTH) {
                    ResponseParser parser = new ResponseParser(part);
                    Pin pin = new Pin(parser.getPinId(), parser.getIoType(), parser.getPinType());
                    String pinValueFromParser = parser.getValue();
                    if (pinValueFromParser.length() == 1) {
                        int intValue = Integer.valueOf(pinValueFromParser);
                        if (intValue == 0) {
                            pin.setValue(false);
                        } else {
                            pin.setValue(true);
                        }
                    } else {
                        // Not an GPIO pin instance
                        return params;
                    }
                    pins.add(pin);
                }
            }
        }
        Platform.runLater(() -> embeddedLayoutCallback.setColorOnPins(pins));
        return params;
    }
}
