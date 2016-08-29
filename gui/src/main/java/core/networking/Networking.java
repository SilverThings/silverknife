package core.networking;

import core.Logger;
import core.Pin;
import core.Validations;
import layouts.EmbeddedLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;

public class Networking {

    final static String DATE_FORMAT = "ddMMyyyyHHmmss";
    private final static int INITIAL_DELAY = 5;
    private final static int KEEP_ALIVE_REQUEST_INITIAL_DELAY = 1000;
    private final static int KEEP_ALIVE_REQUEST_INTERVAL = 5000;

    private ExecutorService connectionService;
    private ScheduledExecutorService scheduledService;
    private ScheduledExecutorService keepAliveService;
    private Logger logger;
    private NetworkingParams params;
    private Validations validations;
    private int refreshRate;
    private List<Pin> pins;

    public Networking() {
        connectionService = Executors.newSingleThreadExecutor();
        validations = new Validations(null);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public synchronized boolean connect(DisconnectCallback disconnectCallback, String ipAddress) {
        try {
            params = connectionService.submit(new Connect(ipAddress, logger)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log(e.toString());
        }

        if (params.connected) {
            keepAliveService = Executors.newSingleThreadScheduledExecutor();
            keepAliveService.scheduleAtFixedRate(new KeepAlive(disconnectCallback, params), KEEP_ALIVE_REQUEST_INITIAL_DELAY, KEEP_ALIVE_REQUEST_INTERVAL, TimeUnit.MILLISECONDS);
        }

        return params.connected;
    }

    public synchronized boolean disconnect() {
        if (keepAliveService != null && !keepAliveService.isShutdown()) {
            keepAliveService.shutdown();
        }
        //already disconnected
        if (params != null) {
            try {
                connectionService.submit(new Disconnect(params, logger)).get();
                return params.connected;
            } catch (InterruptedException | ExecutionException e) {
                logger.log("Disconnecting without notifying server.");
                return false;
            }
        }
        return false;
    }

    public synchronized void toggleGpioPin(DisconnectCallback disconnectCallback, EmbeddedLayout callback, Pin pin, boolean toggle) {
        try {
            String pinValue;
            if (toggle) {
                pinValue = "";
            } else {
                if (pin.isValuePositive()) {
                    pinValue = "1";
                } else {
                    pinValue = "0";
                }
            }
            connectionService.submit(new ToggleGpioPin(callback, getDateAndTime(), params, logger, pin, pinValue)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log("Server did not responded on command.");
            disconnectCallback.serverDisconnected();
        }
    }

    public synchronized void sendValueToI2CPin(DisconnectCallback disconnectCallback, Pin pin, String address, String message) {
        try {
            connectionService.submit(new SendValueToI2CPin(getDateAndTime(), params, logger, pin, address, message)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log("Server did not responded on command.");
            disconnectCallback.serverDisconnected();
        }
    }

    public synchronized void sendValueToSpiPin(DisconnectCallback disconnectCallback, Pin pin, String address, String message) {
        try {
            connectionService.submit(new SendValueToSpiPin(getDateAndTime(), params, logger, pin, address, message)).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log("Server did not responded on command.");
            disconnectCallback.serverDisconnected();
        }
    }

    public synchronized void startRequestPinStatus(DisconnectCallback disconnectCallback, EmbeddedLayout embeddedLayoutCallback, int refreshRate, final List<Pin> pins) {
        this.refreshRate = refreshRate;
        this.pins = pins;
        scheduledService = Executors.newSingleThreadScheduledExecutor();
        final StartRequestPinStatus status = new StartRequestPinStatus(disconnectCallback, embeddedLayoutCallback, params, logger, getDateAndTime(), pins);
        scheduledService.scheduleAtFixedRate(status, INITIAL_DELAY, refreshRate, TimeUnit.MILLISECONDS);
    }

    public void cancelRequestPinStatus() {
        if (scheduledService != null) {
            scheduledService.shutdownNow();
        }
    }

    public synchronized void updateRequestRefreshRate(DisconnectCallback disconnectCallback, EmbeddedLayout embeddedLayoutCallback, int refreshRate) {
        this.refreshRate = refreshRate;
        cancelRequestPinStatus();
        scheduledService = Executors.newSingleThreadScheduledExecutor();
        startRequestPinStatus(disconnectCallback, embeddedLayoutCallback, refreshRate, this.pins);
    }

    public synchronized void updatePinsInRequestStatus(DisconnectCallback disconnectCallback, EmbeddedLayout embeddedLayoutCallback, List<Pin> pins) {
        this.pins = pins;
        cancelRequestPinStatus();
        scheduledService = Executors.newSingleThreadScheduledExecutor();
        startRequestPinStatus(disconnectCallback, embeddedLayoutCallback, this.refreshRate, pins);
    }

    public synchronized void sendMacro(EmbeddedLayout pinCallback, PopupDismiss popupCallback, List<String> commands) {
        connectionService.submit(new SendMacro(pinCallback, popupCallback, params, logger, commands));
    }

    private String getDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
}
