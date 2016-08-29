package managers;

import io.silverspoon.bulldog.core.gpio.DigitalInput;
import io.silverspoon.bulldog.core.gpio.DigitalOutput;
import io.silverspoon.bulldog.core.platform.Board;

public class GpioManager {

    public int turnLedOn(Board board, String pin) throws NullPointerException {
        DigitalOutput output = board.getPin(pin).as(DigitalOutput.class);
        output.high();
        return 1;
    }

    public int turnLedOff(Board board, String pin) throws NullPointerException {
        DigitalOutput output = board.getPin(pin).as(DigitalOutput.class);
        output.low();
        return 0;
    }

    public int toggleLed(Board board, String pin) throws NullPointerException {
        DigitalOutput output = board.getPin(pin).as(DigitalOutput.class);
        if (output.isLow()) {
            output.high();
            return 1;
        } else {
            output.low();
            return 0;
        }
    }

    public int getInputValue(Board board, String pin) {
        DigitalInput digitalInput = board.getPin(pin).as(DigitalInput.class);
        if (digitalInput.read().getBooleanValue()) {
            return 1;
        } else {
            return 0;
        }
    }
}
