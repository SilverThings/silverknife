package core;

import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.platform.Platform;

import java.io.IOException;

public class Main {

    static Board board = Platform.createBoard();

    public static void main(String[] args) {
        try {
            Networking networking = new Networking();
            networking.listenSocket();
        } catch (IOException e) {
            System.out.println("Failed to open socket");
        }
    }
}