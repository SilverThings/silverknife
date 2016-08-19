package core.networking;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

class NetworkingParams {

    final static int PORT_NUMBER = 18924;

    Socket socket;
    String ipAddress;
    PrintWriter out;
    BufferedReader in;
    String message;
    boolean connected;
}
