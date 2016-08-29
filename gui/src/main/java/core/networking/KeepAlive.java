package core.networking;

import javafx.application.Platform;

import java.io.IOException;

class KeepAlive implements Runnable {

    private DisconnectCallback callback;
    private NetworkingParams params;

    KeepAlive(DisconnectCallback callback, NetworkingParams params) {
        this.callback = callback;
        this.params = params;
    }

    @Override
    public void run() {
        params.out.println("Alive?");
        try {
            String response = params.in.readLine();
            if (!response.equals("Yes")) {
                Platform.runLater(() -> callback.serverDisconnected());
            }
        } catch (IOException | NullPointerException e) {
            Platform.runLater(() -> callback.serverDisconnected());
        }
    }
}
