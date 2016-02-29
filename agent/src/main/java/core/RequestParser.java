package core;

public class RequestParser {

	private String day;
	private String month;
	private String year;
	private String hour;
	private String minute;
	private String second;
	private String pinType;
	private String pinNumber;
	private String value;
	
	public RequestParser(String input){
		String[] splitString = input.split(":");
		day = splitString[0].substring(0, 2);
		month = splitString[0].substring(2, 4);
		year = splitString[0].substring(4, 8);
		hour = splitString[0].substring(8, 10);
		minute = splitString[0].substring(10, 12);
		second = splitString[0].substring(12, 14);
		pinType = splitString[0].substring(14);
		pinNumber = splitString[1].substring(0, 2);
		value = splitString[1].substring(2);
	}

	public String getDay() {
		return day;
	}

	public String getMonth() {
		return month;
	}

	public String getYear() {
		return year;
	}

	public String getHour() {
		return hour;
	}

	public String getMinute() {
		return minute;
	}

	public String getSecond() {
		return second;
	}

	public String getPinType() {
		return pinType;
	}

	public String getPinNumber() {
		return pinNumber;
	}

	public String getValue() {
		return value;
	}
}
