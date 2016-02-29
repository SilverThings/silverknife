package managers;

import java.io.IOException;

import io.silverspoon.bulldog.core.io.bus.i2c.I2cBus;
import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.io.bus.i2c.I2cConnection;

public class I2CManager {
	
	private final I2cBus i2c;
	private static final int RECEIVE_LENGTH = 40;
	private I2cConnection connection;
	
	public I2CManager(Board board, String address){
		i2c = board.getI2cBuses().get(0);
		connection = i2c.createI2cConnection(Byte.decode(address));
	}

	public void sendI2CMessage(String message) throws IOException{
		byte[] requestBuffer = new byte[message.length() / 2];
		for (int i = 0; i < requestBuffer.length; i++) {
			String value = "0x" + message.substring(2 * i, 2 * (i + 1));
			requestBuffer[i] = Integer.decode(value).byteValue();
			System.out.println("Accepting byte: " + value);
		}
		connection.writeBytes(requestBuffer);
		System.out.println("I2C message sent succesfully. Receiving status.");
	}
	
	public String receiveI2Cmessage() throws IOException{
		byte[] responseBuffer = new byte[RECEIVE_LENGTH];
		int count = connection.readBytes(responseBuffer);
		StringBuffer response = new StringBuffer();
		for (int i = 0; i < count && i < RECEIVE_LENGTH; i++) {
			response.append(Integer.toHexString(responseBuffer[i]));
		}
		if (response.toString().length() > 0) {
			return response.toString();
		} else {
			return "OK";
		}
	}
}
