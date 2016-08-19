package core;

public class Pin {

    public static final String I2C = "I2C";
    public static final String SPI = "SPI";
    public static final String GPIO = "GPIO";
    public static final String GND = "GND";
    public static final String PWR3 = "PWR3";
    public static final String PWR5 = "PWR5";
    public static final String UART = "UART";
    public static final String EEPROM = "EEPROM";
    public static final String PWR_BTN = "PWR_BTN";
    public static final String SYS_RST = "SYS_RST";
    public static final String PWM = "PWM";
    public static final String AI = "AI";

    private int pinId;
    private String ioType;
    private String pinType;
    private boolean value;

    public Pin(int pinId, String ioType, String pinType) {
        this.pinId = pinId;
        this.ioType = ioType;
        this.pinType = pinType;
    }

    public int getPinId() {
        return pinId;
    }

    public String getIoType() {
        return ioType.substring(0,1);
    }

    public String getPinType() {
        return pinType;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
