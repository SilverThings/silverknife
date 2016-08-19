package core.networking;

import core.Logger;
import core.Pin;
import core.Validations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

class SendMacro implements Callable<NetworkingParams> {

    private String address;
    private List<String> commands;
    private NetworkingParams params;
    private Logger logger;
    private Validations validations;

    SendMacro(NetworkingParams params, Logger logger, String address, List<String> commands) {
        this.address = address;
        this.commands = commands;
        this.params = params;
        this.logger = logger;
        this.validations = new Validations(logger);
    }

    @Override
    public NetworkingParams call() throws Exception {
        for (String command : commands) {
            command = command.substring(0, command.lastIndexOf(";"));
            logger.log("Sending: " + command);
            sendMacroCommand(address, command);
        }
        logger.log("Macro sent.");
        return params;
    }

    private void sendMacroCommand(String address, String command) throws Exception {
        if (validations.isOnlyDigitString(command)) {
//            sleepThread(connectionService, Integer.valueOf(command));
            Thread.sleep(Integer.valueOf(command));
            return;
        }

        NetworkingParams networkingParams;
        int pinId;
        String hexaCommand;

        if (command.startsWith("GPIO")) {
            pinId = Integer.valueOf(command.substring(5, 7));
            Pin pin = new Pin(pinId, "O", "GPIO");
            networkingParams = new ToggleGpioPin(getDateAndTime(), params, logger, pin).call();
        } else if (command.startsWith("I2C")) {
            pinId = Integer.valueOf(command.substring(4, 6));
            hexaCommand = command.substring(6);
            Pin pin = new Pin(pinId, "O", "I2C");
            networkingParams = new SendValueToI2CPin(getDateAndTime(), params, logger, pin, address, hexaCommand).call();
        } else if (command.startsWith("SPI")) {
            pinId = Integer.valueOf(command.substring(4, 6));
            hexaCommand = command.substring(6);
            Pin pin = new Pin(pinId, "O", "SPI");
            networkingParams = new SendValueToSpiPin(getDateAndTime(), params, logger, pin, address, hexaCommand).call();
        } else {
            logger.log("Not supported command in macro.");
            return;
        }

        if (networkingParams != null) {
            logger.log(networkingParams.message);
        }
    }

    private String getDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat(Networking.DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
}
