package core;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sun.net.util.IPAddressUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {

    private final static int MIN_IP_ADDRESS_NUMBER = 0;
    private final static int MAX_IP_ADDRESS_NUMBER = 255;
    private final static int MIN_THREAD_SLEEP_TIME = 100;
    private final static int MAX_THREAD_SLEEP_TIME = 10000;
    private final static int GPIO_CONTENT_LENGTH = 3;
    private final static int GPIO_VALUE_POSITION = 2;
    private final static int MAX_PIN_REQUEST_TEXT_FIELD_LENGTH = 5;
    private final static String MACRO_LINE_END = ";";
    private final static String GPIO_STRING = "GPIO";
    private final static String I2C_STRING = "I2C";
    private final static String SPI_STRING = "SPI";
    private final static String COMMAND_SPLITTER = ":";
    private final static String GPIO_OFF_VALUE = "0";
    private final static String GPIO_ON_VALUE = "1";
    private final static Pattern HEXA_PATTERN_SINGLE = Pattern.compile("([0-9a-fA-F])*");
    private final static Pattern HEXA_PATTERN_DOUBLE = Pattern.compile("([0-9a-fA-F][0-9a-fA-F])*");
    private final static String DIGIT_ONLY_REGEX = "[0-9]+";
    private final static String RED_BORDER = "-fx-border-color: red; -fx-border-width: 2px;";
    private final static String GREEN_BORDER = "-fx-border-color: green; -fx-border-width: 2px;";

    private Logger logger;

    public Validations(Logger logger){
        this.logger = logger;
    }

    public boolean isIpAddress(String ip) {
        try {
            if (ip == null || ip.isEmpty() || !IPAddressUtil.isIPv4LiteralAddress(ip) || ip.endsWith(".")) {
                return false;
            }

            String[] parts = ip.split("\\.");

            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < MIN_IP_ADDRESS_NUMBER) || (i > MAX_IP_ADDRESS_NUMBER)) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            logger.log(e.toString());
            return false;
        }
    }

    public void setIpAddressValidationBorder(TextField textField) {
        if (isIpAddress(textField.getText())) {
            textField.setStyle(GREEN_BORDER);
        } else {
            textField.setStyle(RED_BORDER);
        }
    }

    public void setAddressValidationBorder(String input, TextField textField) {
        if (isPhysicalAddressValid(input)) {
            textField.setStyle(GREEN_BORDER);
        } else {
            textField.setStyle(RED_BORDER);
        }
    }

    private boolean isMacroAreaValid(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (String line : input.split("\\n")) {
            boolean validLine = false;
            if (line == null || line.isEmpty() || !line.endsWith(MACRO_LINE_END)) {
                return false;
            }
            String lineWithoutSemicolon = line.substring(0, line.lastIndexOf(MACRO_LINE_END));
            if (line.startsWith(GPIO_STRING + COMMAND_SPLITTER) || line.startsWith(SPI_STRING + COMMAND_SPLITTER) || line.startsWith(I2C_STRING + COMMAND_SPLITTER)) {
                String[] token = lineWithoutSemicolon.split(COMMAND_SPLITTER);
                if (token.length <= 1 || token[1].isEmpty()) {
                    return false;
                }
                String command = token[0];
                String content = token[1];
                if (!content.isEmpty() && I2C_STRING.equals(command) && isMacroHexaCommandValid(content)) {
                    validLine = true;
                }
                if (!content.isEmpty() && SPI_STRING.equals(command) && isMacroHexaCommandValid(content)) {
                    validLine = true;
                }
                if (GPIO_STRING.equals(command) && content.length() == GPIO_CONTENT_LENGTH && isOnlyDigitString(content)) {
                    String valueToSend = String.valueOf(content.charAt(GPIO_VALUE_POSITION));
                    validLine = valueToSend.equals(GPIO_OFF_VALUE) || valueToSend.equals(GPIO_ON_VALUE);
                }
            } else if (isOnlyDigitString(lineWithoutSemicolon) && line.endsWith(MACRO_LINE_END)) {
                int contentValue = Integer.valueOf(lineWithoutSemicolon);
                validLine = contentValue >= MIN_THREAD_SLEEP_TIME && contentValue <= MAX_THREAD_SLEEP_TIME;
            } else {
                validLine = false;
            }
            if (!validLine) {
                return false;
            }
        }
        return true;
    }

    public boolean isOnlyDigitString(String input) {
        return input.matches(DIGIT_ONLY_REGEX);
    }

    private boolean isDoubleHexaString(String input) {
        Matcher i2CMatcher = HEXA_PATTERN_DOUBLE.matcher(input);
        return (i2CMatcher.matches() && !input.equals(""));
    }

    private boolean isSingleHexaString(String input) {
        Matcher i2CMatcher = HEXA_PATTERN_SINGLE.matcher(input);
        return (i2CMatcher.matches() && !input.equals(""));
    }

    public boolean isMacroHexaCommandValid(String input) {
        return isOnlyDigitString(input.substring(0, 2)) && isHexaStringValid(input.substring(2));
    }

    public boolean isHexaStringValid(String input) {
        return isDoubleHexaString(input);
    }

    public boolean isPhysicalAddressValid(String input) {
        return isSingleHexaString(input) && input.length() == 2;
    }

    public void setTextAreaValidationBorder(TextArea textarea, int item) {
        if (isTextAreaValid(item, textarea.getText())) {
            textarea.setStyle(GREEN_BORDER);
        } else {
            textarea.setStyle(RED_BORDER);
        }
    }

    public boolean isTextAreaValid(int item, String input) {
        switch (item) {
            case 0:
                return isMacroAreaValid(input);
            case 1:
                return isHexaStringValid(input);
            case 2:
                return isHexaStringValid(input);
            default:
                return false;
        }
    }

    public boolean setPinRequestValidationBorder(TextField textField) {
        String textFieldText = textField.getText();

        if (isOnlyDigitString(textFieldText) && textField.getLength() <= MAX_PIN_REQUEST_TEXT_FIELD_LENGTH) {
            int value = Integer.valueOf(textFieldText);
            if (value >= MIN_THREAD_SLEEP_TIME && value <= MAX_THREAD_SLEEP_TIME) {
                textField.setStyle(GREEN_BORDER);
                return true;
            } else {
                textField.setStyle(RED_BORDER);
                return false;
            }
        } else {
            textField.setStyle(RED_BORDER);
            return false;
        }
    }
}
