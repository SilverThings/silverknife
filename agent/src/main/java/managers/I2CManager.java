package managers;

import core.Networking;
import io.silverspoon.bulldog.core.io.bus.i2c.I2cConnection;
import io.silverspoon.bulldog.core.platform.Board;
import strings.Information;
import strings.Response;

import java.io.IOException;

public class I2cManager {

    private static final int RECEIVE_LENGTH = 40;
    private static final String ADDRESS_PREFIX = "0x";

    private I2cConnection connection;
    private String address;

    public I2cManager(Board board, String address) {
        this.address = address;
        connection = board.getI2cBuses().get(0).createI2cConnection(Byte.decode(address));
    }

    public void sendI2cMessage(String message) {
        byte[] requestBuffer = new byte[message.length() / 2];
        for (int i = 0; i < requestBuffer.length; i++) {
            String value = ADDRESS_PREFIX + message.substring(2 * i, 2 * (i + 1));
            requestBuffer[i] = Integer.decode(value).byteValue();
            System.out.println(Information.BUS_ADDRESS + address);
            System.out.println(Information.ACCEPTING_MESSAGE + value);
        }
        try {
            connection.writeBytes(requestBuffer);
            System.out.println(Networking.I2C + Information.BUS_MESSAGE_WROTE_SUCCESSFULLY);
        } catch (IOException e) {
            System.out.println(Networking.I2C + Information.BUS_NOT_SUPPORTED_OR_DISABLED);
        } catch (NullPointerException e) {
            System.out.println(Information.CANNOT_WRITE_MESSAGE_ON_BUS);
        }
    }

    public String receiveI2cMessage() {
        try {
            byte[] responseBuffer = new byte[RECEIVE_LENGTH];
            int count = connection.readBytes(responseBuffer);
            StringBuilder response = new StringBuilder();
            for (int i = 0; i < count && i < RECEIVE_LENGTH; i++) {
                response.append(Integer.toHexString(responseBuffer[i]));
            }
            if (response.toString().length() > 0) {
                return response.toString();
            }
        } catch (IOException e) {
            System.out.println(Networking.I2C + Information.BUS_NOT_SUPPORTED_OR_DISABLED);
            return Response.INVALID_RESPONSE;
        } catch (NullPointerException e) {
            System.out.println(Information.CANNOT_READ_MESSAGE_FROM_BUS);
            return Response.INVALID_RESPONSE;
        }
        return Response.INVALID_RESPONSE;
    }
}
