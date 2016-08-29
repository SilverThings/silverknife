package core.networking;

import core.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

class Connect implements Callable<NetworkingParams> {

    private final static String CONNECTION_COMMAND = "Connect";
    private final static String CONNECTION_SUCCESSFUL = "Connected successfully.";
    private final static String CONNECTION_RESPONSE = "Server notified about client connect attempt.";

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
            params.socket = new Socket();
            params.socket.connect(new InetSocketAddress(params.ipAddress, NetworkingParams.PORT_NUMBER), 200);
            params.socket.setSoTimeout(200);
            params.out = new PrintWriter(params.socket.getOutputStream(), true);
            params.in = new BufferedReader(new InputStreamReader(params.socket.getInputStream()));
            params.out.println(CONNECTION_COMMAND);
            params.message = params.in.readLine();
            if (params.message.equals(CONNECTION_RESPONSE)) {
                params.connected = true;
                logger.log(CONNECTION_SUCCESSFUL);
            }
        } catch (IOException e) {
            logger.log("Server not running on selected IP address");
            params = null;
        }
        return params;
    }
}
