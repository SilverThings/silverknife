package core.networking;

import core.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

class Connect implements Callable<NetworkingParams> {

    private final static String CONNECTION_COMMAND = "Connect";
    private final static String CONNECTION_SUCCESSFUL = "Connected successfully.";
    // TODO: 28.7.2016 zmenit najprv na server side potom tu
    private final static String CONNECTION_RESPONSE = "connected to server.";

    private NetworkingParams params;
    private Logger logger;

    Connect(String ipAddress, Logger logger) {
        this.logger = logger;
        params = new NetworkingParams();
        params.ipAddress = ipAddress;
    }

    @Override
    public NetworkingParams call() throws Exception {
        try {
            params.socket = new Socket(params.ipAddress, NetworkingParams.PORT_NUMBER);
            params.socket.setSoTimeout(11000);
            params.out = new PrintWriter(params.socket.getOutputStream(), true);
            params.in = new BufferedReader(new InputStreamReader(params.socket.getInputStream()));
            params.out.println(CONNECTION_COMMAND);
            params.message = params.in.readLine();

            if (params.message.equals(CONNECTION_RESPONSE)) {
                params.connected = true;
                logger.log(CONNECTION_SUCCESSFUL);
            }
        } catch (IOException e) {
            logger.log(e.toString());
        }
        return params;
    }
}
