package core.networking;

import core.Logger;
import core.Pin;

import java.util.concurrent.Callable;

class SendValueToSpiPin implements Callable<NetworkingParams> {

    private static final int TWO_DIGITS_VALUE = 10;

    private String dateAndTime;
    private NetworkingParams params;
    private Logger logger;
    private Pin pin;
    private String address;
    private String message;

    SendValueToSpiPin(String dateAndTime, NetworkingParams params, Logger logger, Pin pin, String address, String message) {
        this.dateAndTime = dateAndTime;
        this.params = params;
        this.logger = logger;
        this.pin = pin;
        this.address = address;
        this.message = message;
    }

    @Override
    public NetworkingParams call() throws Exception {
        if (params != null) {
            if (pin.getPinId() < TWO_DIGITS_VALUE) {
                String spiMessage = dateAndTime + pin.getIoType() + pin.getPinType() + ":0" + pin.getPinId() + address + message;
                logger.log("Sending: " + spiMessage);
                params.out.println(message);
            } else {
                String spiMessage = dateAndTime + pin.getIoType() + pin.getPinType() + ":" + pin.getPinId() + address + message;
                logger.log("Sending: " + spiMessage);
                params.out.println(message);
            }

            // TODO: 16.8.2016 uncomment when spi response will be implemented on server side (and test it then)
//            String response = params.in.readLine();
//            params.message = response;
//            logger.log(response);
        } else {
            logger.log("Not connected. Cannot send SPI message.");
        }
        return params;
    }
}
