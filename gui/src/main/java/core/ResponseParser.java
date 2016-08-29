package core;

public class ResponseParser {

    private String day;
    private String month;
    private String year;
    private String hour;
    private String minute;
    private String second;
    private String pinType;
    private String ioType;
    private String pinId;
    private String value;

    public ResponseParser(String input) {
        String[] splitString = input.split(":");
        day = splitString[0].substring(0, 2);
        month = splitString[0].substring(2, 4);
        year = splitString[0].substring(4, 8);
        hour = splitString[0].substring(8, 10);
        minute = splitString[0].substring(10, 12);
        second = splitString[0].substring(12, 14);
        pinType = splitString[0].substring(14);
        ioType = splitString[1].substring(0, 1);
        pinId = splitString[1].substring(1, 3);
        value = splitString[1].substring(3);
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

    public String getIoType() {
        return ioType;
    }

    public int getPinId() {
        return Integer.valueOf(pinId);
    }

    public String getValue() {
        return value;
    }
}
