package core.hashmaps;

import core.Pin;

import java.util.HashMap;

public class RaspberryHashMap {

    private HashMap<Integer, String[]> hashMap = new HashMap<>();

    public void createHashMap() {
        String[] pin1 = { Pin.PWR3};
        String[] pin2 = { Pin.PWR5};
        String[] pin3 = { Pin.GPIO, Pin.I2C};
        String[] pin4 = { Pin.PWR5};
        String[] pin5 = { Pin.GPIO, Pin.I2C};
        String[] pin6 = { Pin.GND};
        String[] pin7 = { Pin.GPIO};
        String[] pin8 = { Pin.GPIO, Pin.UART};
        String[] pin9 = { Pin.GND};

        String[] pin10 = { Pin.GPIO, Pin.UART};
        String[] pin11 = { Pin.GPIO};
        String[] pin12 = { Pin.GPIO};
        String[] pin13 = { Pin.GPIO};
        String[] pin14 = { Pin.GND};
        String[] pin15 = { Pin.GPIO};
        String[] pin16 = { Pin.GPIO};
        String[] pin17 = { Pin.PWR3};
        String[] pin18 = { Pin.GPIO};
        String[] pin19 = { Pin.GPIO, Pin.SPI};

        String[] pin20 = { Pin.GND};
        String[] pin21 = { Pin.GPIO, Pin.SPI};
        String[] pin22 = { Pin.GPIO};
        String[] pin23 = { Pin.GPIO, Pin.SPI};
        String[] pin24 = { Pin.GPIO, Pin.SPI};
        String[] pin25 = { Pin.GND};
        String[] pin26 = { Pin.GPIO, Pin.SPI};
        String[] pin27 = { Pin.EEPROM};
        String[] pin28 = { Pin.EEPROM};
        String[] pin29 = { Pin.GPIO};

        String[] pin30 = { Pin.GND};
        String[] pin31 = { Pin.GPIO};
        String[] pin32 = { Pin.GPIO};
        String[] pin33 = { Pin.GPIO};
        String[] pin34 = { Pin.GND};
        String[] pin35 = { Pin.GPIO};
        String[] pin36 = { Pin.GPIO};
        String[] pin37 = { Pin.GPIO};
        String[] pin38 = { Pin.GPIO};
        String[] pin39 = { Pin.GND};

        String[] pin40 = { Pin.GPIO};

        hashMap.put(1, pin1);
        hashMap.put(2, pin2);
        hashMap.put(3, pin3);
        hashMap.put(4, pin4);
        hashMap.put(5, pin5);
        hashMap.put(6, pin6);
        hashMap.put(7, pin7);
        hashMap.put(8, pin8);
        hashMap.put(9, pin9);

        hashMap.put(10, pin10);
        hashMap.put(11, pin11);
        hashMap.put(12, pin12);
        hashMap.put(13, pin13);
        hashMap.put(14, pin14);
        hashMap.put(15, pin15);
        hashMap.put(16, pin16);
        hashMap.put(17, pin17);
        hashMap.put(18, pin18);
        hashMap.put(19, pin19);

        hashMap.put(20, pin20);
        hashMap.put(21, pin21);
        hashMap.put(22, pin22);
        hashMap.put(23, pin23);
        hashMap.put(24, pin24);
        hashMap.put(25, pin25);
        hashMap.put(26, pin26);
        hashMap.put(27, pin27);
        hashMap.put(28, pin28);
        hashMap.put(29, pin29);

        hashMap.put(30, pin30);
        hashMap.put(31, pin31);
        hashMap.put(32, pin32);
        hashMap.put(33, pin33);
        hashMap.put(34, pin34);
        hashMap.put(35, pin35);
        hashMap.put(36, pin36);
        hashMap.put(37, pin37);
        hashMap.put(38, pin38);
        hashMap.put(39, pin39);

        hashMap.put(40, pin40);
    }

    public String[] getValueByKey(int key) {
        return hashMap.get(key);
    }
}
