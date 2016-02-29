package core;

import java.util.ArrayList;

public class RequestedPinsParser {

	ArrayList<String> pinsToGetStatus;

	public RequestedPinsParser(String input) {
		pinsToGetStatus = new ArrayList<String>();
		String[] pins = input.split(";");
		addPinsToSend(pins);
	}

	public ArrayList<String> getPinsToSend() {
		return pinsToGetStatus;
	}
	
	private void addPinsToSend(String[] pins){
		for (String pin : pins) {
			if (pin.length() < 3) {
				pinsToGetStatus.add(pin);
			}
		}
	}
}
