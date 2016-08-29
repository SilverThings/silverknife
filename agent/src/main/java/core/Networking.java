package core;

import hashmaps.RaspberryHashMap;
import io.silverspoon.bulldog.core.gpio.DigitalIO;
import io.silverspoon.bulldog.core.pin.Pin;
import io.silverspoon.bulldog.core.platform.Board;
import managers.GpioManager;
import managers.I2cManager;
import managers.SpiManager;
import strings.Command;
import strings.Information;
import strings.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Networking {

    private static final int SOCKET_PORT = 18924;
    private static final int BEGINNING_OF_PINS_INDEX = 25;
    private static final String INPUT = "I";
    private static final String OUTPUT = "O";
    private static final String GPIO = "GPIO";
    public static final String I2C = "I2C";
    public static final String SPI = "SPI";
    private static final String UART = "UART";
    private static final String DATE_FORMAT = "ddMMyyyyHHmmss";
    private static final String REQUEST_TO_SEND_ALL = "REQUEST:990";
    private static final int START_OF_COMMAND_RECOGNITION_INDEX = 14;

    private static boolean connected = false;

    private Board board;
    private PrintWriter out;
    private BufferedReader in;
    private RaspberryHashMap piMap;
    private ServerSocket server;

    Networking() throws IOException {
        server = new ServerSocket(SOCKET_PORT);
        board = Main.board;
        piMap = new RaspberryHashMap();
    }

    void listenSocket() {
        System.out.println(Information.SERVER_STARTED);
        // Exactly names for boards are:
        // Raspberry Pi
        // BeagleBone Black
        // Cubieboard
        System.out.println(board.getName() + "\n");

        new Thread(new Runnable() {
            public void run() {
                String input;
                while (true) {
                    try {
                        Socket client = server.accept();
                        out = new PrintWriter(client.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        System.out.println(Information.CLIENT_CONNECTED);
                        while ((input = in.readLine()) != null) {
                            if (!connected) {
                                switch (input) {
                                    case Command.CONNECT:
                                        out.println(Response.CONNECT);
                                        connected = true;
                                        break;
                                    default:
                                        System.out.println(Information.CLIENT_NOT_CONNECTED + input);
                                        break;
                                }
                            } else {
                                switch (input) {
                                    case Command.DISCONNECT:
                                        out.println(Response.DISCONNECT);
                                        connected = false;
                                        break;
                                    case Command.IS_SERVER_ONLINE:
                                        out.println(Response.SERVER_ONLINE);
                                        break;
                                    default:
                                        if (isEmbeddedCommand(input)) {
                                            manageCommand(input);
                                        } else if (isRequestToSendAll(input)) {
                                            sendAllPinStatus(input);
                                        } else {
                                            System.out.println(Information.UNKNOWN_COMMAND + input);
                                        }
                                        break;
                                }
                            }
                        }
                        System.out.println(Information.CLIENT_DISCONNECTED_AND_NOTIFIED);
                    } catch (IOException e) {
                        System.out.println(Information.CLIENT_DISCONNECTED_NO_NOTIFICATION);
                        connected = false;
                    }
                }
            }
        }).start();
    }

    private void sendParsedData(String input) {
        String separator = "|";
        RequestParser parser = new RequestParser(input);
        out.println("Day:" + parser.getDay() + separator +
                "Month:" + parser.getMonth() + separator +
                "Year:" + parser.getYear() + separator +
                "Hour:" + parser.getHour() + separator +
                "Minute:" + parser.getMinute() + separator +
                "Second:" + parser.getSecond() + separator +
                "I/O Type:" + parser.getIoType() + separator +
                "Pin Type:" + parser.getPinType() + separator +
                "Pin Number:" + parser.getPinNumber() + separator +
                "Value:" + parser.getValue());
    }

    private void sendAllPinStatus(String input) {
        out.print(Response.PIN_REQUEST_INITIALIZATION);
        ArrayList<String> pinsToSend = new RequestedPinsParser(input.substring(BEGINNING_OF_PINS_INDEX)).getPinsToSend();
        if (pinsToSend != null && !pinsToSend.isEmpty()) {
            for (String pinNumberString : pinsToSend) {
                if (!pinNumberString.isEmpty()) {
                    int pinNumberInt = Integer.valueOf(pinNumberString);
                    if (pinNumberString.length() == 1) {
                        pinNumberString = "0" + pinNumberString;
                    }
                    Pin physicalPin = null;
                    for (Pin p : board.getPins()) {
                        if (p.getIndexOnPort() == pinNumberInt) {
                            physicalPin = p;
                        }
                    }
                    if (physicalPin != null) {
                        DigitalIO digitalIO = physicalPin.as(DigitalIO.class);
                        int pinValue = digitalIO.read().getNumericValue();

                        if (digitalIO.isOutputActive()) {
                            out.print(getDateAndTime() + "GPIO:O" + pinNumberString + pinValue + Command.SPLIT_BETWEEN_PINS);
                        } else if (digitalIO.isInputActive()) {
                            out.print(getDateAndTime() + "GPIO:I" + pinNumberString + pinValue + Command.SPLIT_BETWEEN_PINS);
                        } else {
                            System.out.println(Information.PIN_NOT_TRIGGERED);
                        }
                    }
                }
            }
            out.println(Response.PIN_REQUEST_END);
        } else {
            out.println(Response.INVALID_RESPONSE);
        }
    }

    private void manageCommand(String input) {
        RequestParser parser = new RequestParser(input);
        String[] pinTypes = piMap.getValueByKey(Integer.valueOf(parser.getPinNumber()));
        if (pinTypes != null) {
            System.out.println(Information.CLIENT_COMMAND + input);
            if (INPUT.equals(parser.getIoType())) {
                manageInput(parser, pinTypes[0]);
            } else if (OUTPUT.equals(parser.getIoType())) {
                manageOutput(parser, pinTypes[0]);
            } else {
                System.out.println(Information.CLIENT_ACTION_INVALID_IO);
                out.println(Response.ACTION_INVALID_IO);
            }
        } else {
            System.out.println(Information.CLIENT_ACTION_INVALID_PIN);
            out.println(Response.ACTION_INVALID_PIN);
        }
    }

    private void manageInput(RequestParser parser, String physicalPin) {
        if (GPIO.equals(parser.getPinType())) {
            GpioManager gpio = new GpioManager();
            int value = gpio.getInputValue(board, physicalPin);
            System.out.println(Information.VALUE_FROM_GPIO + parser.getPinNumber() + " set to: " + value);
            out.println("Value on GPIO pin " + parser.getPinNumber() + " set to: " + value);
        } else {
            System.out.println(Information.WRONG_PIN_TYPE);
        }
    }

    private void manageOutput(RequestParser parser, String physicalPin) {
        switch (parser.getPinType()) {
            case GPIO:
                GpioManager gpio = new GpioManager();
                int setValue;
                if (parser.getValue().equals("1")) {
                    setValue = gpio.turnLedOn(board, physicalPin);
                    System.out.println(Information.VALUE_FROM_GPIO + parser.getPinNumber() + " set to: " + setValue);
                    out.println(Response.VALUE_ON_GPIO + parser.getPinNumber() + " set to: " + setValue);
                } else if (parser.getValue().equals("0")) {
                    setValue = gpio.turnLedOff(board, physicalPin);
                    System.out.println(Information.VALUE_FROM_GPIO + parser.getPinNumber() + " set to: " + setValue);
                    out.println(Response.VALUE_ON_GPIO + parser.getPinNumber() + " set to: " + setValue);
                } else if (parser.getValue().isEmpty()) {
                    setValue = gpio.toggleLed(board, physicalPin);
                    System.out.println(Information.VALUE_FROM_GPIO + parser.getPinNumber() + " set to: " + setValue);
                    out.println(Response.VALUE_ON_GPIO + parser.getPinNumber() + " set to: " + setValue);
                } else {
                    System.out.println("Value for pin " + physicalPin + " unknown.");
                }
                break;
            case I2C: {
                String hexAddress = parser.getValue().substring(0, 4);
                String message = parser.getValue().substring(4);
                I2cManager i2c = new I2cManager(board, hexAddress);
                i2c.sendI2cMessage(message);
                String i2cResponse = i2c.receiveI2cMessage();
                System.out.println(Information.I2C_ON_BUS + i2cResponse);
                out.println(Response.VALUE_ON_I2C + i2cResponse);
                break;
            }
            case SPI: {
                String hexAddress = parser.getValue().substring(0, 4);
                String message = parser.getValue().substring(4);
                SpiManager spi = new SpiManager(board, hexAddress);
                spi.sendSpiMessage(message);
                String spiResponse = spi.receiveSpiMessage();
                System.out.println(Information.SPI_ON_BUS + spiResponse);
                out.println(Response.VALUE_ON_SPI + spiResponse);
                break;
            }
            case UART:
                System.out.println(Information.UART_NOT_SUPPORTED);
                break;
            default:
                out.println(Response.INVALID_RESPONSE);
                break;
        }
    }

    private String getDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    private boolean isRequestToSendAll(String input) {
        return input.length() > START_OF_COMMAND_RECOGNITION_INDEX &&
                input.substring(START_OF_COMMAND_RECOGNITION_INDEX).startsWith(REQUEST_TO_SEND_ALL);
    }

    private boolean isEmbeddedCommand(String input) {
        return input.length() > START_OF_COMMAND_RECOGNITION_INDEX &&
                (input.contains("GPIO:") || input.contains("SPI:") || input.contains("I2C:") || input.contains("UART:"));
    }
}
