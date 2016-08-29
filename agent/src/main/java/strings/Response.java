package strings;

public class Response {
    public static final String CONNECT = "Server notified about client connect attempt.";
    public static final String DISCONNECT = "Server notified about client disconnect attempt.";
    public static final String SERVER_ONLINE = "Yes";
    public static final String PIN_REQUEST_INITIALIZATION = "START;";
    public static final String PIN_REQUEST_END = "END";
    public static final String INVALID_RESPONSE = "N/A";
    public static final String ACTION_INVALID_IO = "Trying to make action on invalid I/O type.";
    public static final String ACTION_INVALID_PIN = "Trying to access invalid pin on currently selected system.";
    public static final String VALUE_ON_GPIO = "Value on GPIO pin ";
    public static final String VALUE_ON_I2C = "Value on I2C bus set to: ";
    public static final String VALUE_ON_SPI = "Value on SPI bus set to: ";
}
