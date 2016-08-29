package core.networking;

import core.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;

class Disconnect implements Callable<NetworkingParams> {

    private final static String DISCONNECT_COMMAND = "Disconnect";
    private final static String DISCONNECT_RESPONSE = "Server notified about client disconnect attempt.";
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
                    if (params.in.readLine().equals(DISCONNECT_RESPONSE)) {
                        disconnect();
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            logger.log(e.toString());
        }
        params.connected = false;
        return params;
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
        }
    }
}
