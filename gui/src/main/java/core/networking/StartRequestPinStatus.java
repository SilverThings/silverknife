package core.networking;

import core.Logger;
import core.Pin;
import layouts.EmbeddedLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

class StartRequestPinStatus implements Callable<NetworkingParams> {

    private final static String PIN_REQUEST_CODE = "REQUEST:990";

    private Logger logger;
    private NetworkingParams params;
    private String dateAndTime;
    private List<Pin> pinList;
    private EmbeddedLayout callback;

    StartRequestPinStatus(EmbeddedLayout callback, Logger logger, String dateAndTime, List<Pin> pinList) {
        this.callback = callback;
        this.params = new NetworkingParams();
        this.logger = logger;
        this.dateAndTime = dateAndTime;
        this.pinList = pinList;
    }

    @Override
    public NetworkingParams call() throws Exception {
        String pinsToRequest = "";
        if (pinList != null || !pinList.isEmpty()) {
            for (Pin pin : pinList) {
                if (pinsToRequest.isEmpty()) {
                    pinsToRequest = String.valueOf(pin.getPinId());
                } else {
                    pinsToRequest = pinsToRequest + ";" + pin.getPinId();
                }
            }
        }
        logger.log("Scheduling at fixed rate with pins: " + pinsToRequest);
        // TODO: 16.8.2016 Uncomment to test with server and response
//        params.out.println(dateAndTime + PIN_REQUEST_CODE + pinsToRequest);
//        params.message = params.in.readLine();
        // TODO: 19.8.2016 zistit string message a rozparsovat
        // TODO: 19.8.2016 foreach pin new List<Pin> kazdemu hodnotu a ID
        callback.updatePinsStatus(pinList);
        return params;
    }
}
