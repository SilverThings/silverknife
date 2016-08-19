package core.networking;

import core.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;

class Disconnect implements Callable<NetworkingParams> {

    private final static String DISCONNECT_COMMAND = "Disconnect";
    // TODO: 28.7.2016 zmenit najprv na serveri potom tu
    private final static String DISCONNECT_RESPONSE = "disconnected from server.";
    private final static String ATTEMPTING_DISCONNECT = "Trying to disconnect from server.";
    private final static String CONNECTION_SUCCESSFULLY_CLOSED = "Connection successfully closed.";
    private final static String CONNECTION_ALREADY_CLOSED = "Connection is already closed.";
    private final static String UNABLE_CLOSE_CONNECTION = "Unable to close connection.";

    private NetworkingParams params;
    private Logger logger;

    Disconnect(NetworkingParams params, Logger logger) {
        this.params = params;
        this.logger = logger;
    }

    @Override
    public NetworkingParams call() {
        try {
            if (params != null) {
                if (params.connected) {
                    params.out.println(DISCONNECT_COMMAND);
                    params.message = params.in.readLine();
                    if (params.message.equals(DISCONNECT_RESPONSE)) {
                        disconnect();
                    }
                    return params;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            logger.log(e.toString());
        }
        return null;
    }

    private void disconnect() {
        if (params.socket != null) {
            logger.log(ATTEMPTING_DISCONNECT);
            if (!params.socket.isClosed()) {
                try {
                    params.socket.close();
                    logger.log(CONNECTION_SUCCESSFULLY_CLOSED);
                } catch (IOException e) {
                    logger.log(UNABLE_CLOSE_CONNECTION);
                    logger.log(e.toString());
                }
            } else {
                logger.log(CONNECTION_ALREADY_CLOSED);
            }
            params.connected = false;
        }
    }
}
