package core.hashmaps;

import core.Pin;

import java.util.HashMap;

public class BeagleBoneHashMap {

    private HashMap<Integer, String[]> hashMap = new HashMap<>();

    public void createHashMap() {
        String[] pin1 = { Pin.GND};
        String[] pin2 = { Pin.GND};
        String[] pin3 = { Pin.PWR3};
        String[] pin4 = { Pin.PWR3};
        String[] pin5 = { Pin.PWR5};
        String[] pin6 = { Pin.PWR5};
        String[] pin7 = { Pin.PWR5};
        String[] pin8 = { Pin.PWR5};
        String[] pin9 = { Pin.PWR_BTN};

        String[] pin10 = { Pin.SYS_RST};
        String[] pin11 = { Pin.GPIO, Pin.UART};
        String[] pin12 = { Pin.GPIO};
        String[] pin13 = { Pin.GPIO, Pin.UART};
        String[] pin14 = { Pin.GPIO, Pin.PWM};
        String[] pin15 = { Pin.GPIO};
        String[] pin16 = { Pin.GPIO, Pin.PWM};
        String[] pin17 = { Pin.GPIO, Pin.I2C, Pin.SPI};
        String[] pin18 = { Pin.GPIO, Pin.I2C, Pin.SPI};
        String[] pin19 = { Pin.GPIO, Pin.I2C, Pin.SPI, Pin.UART};

        String[] pin20 = { Pin.GPIO, Pin.I2C, Pin.SPI, Pin.UART};
        String[] pin21 = { Pin.GPIO, Pin.I2C, Pin.SPI, Pin.UART, Pin.PWM};
        String[] pin22 = { Pin.GPIO, Pin.I2C, Pin.SPI, Pin.UART, Pin.PWM};
        String[] pin23 = { Pin.GPIO};
        String[] pin24 = { Pin.GPIO, Pin.I2C, Pin.UART};
        String[] pin25 = { Pin.GPIO};
        String[] pin26 = { Pin.GPIO, Pin.I2C, Pin.UART};
        String[] pin27 = { Pin.GPIO};
        String[] pin28 = { Pin.GPIO, Pin.SPI, Pin.PWM};
        String[] pin29 = { Pin.GPIO, Pin.SPI, Pin.PWM};

        String[] pin30 = { Pin.GPIO, Pin.SPI};
        String[] pin31 = { Pin.GPIO, Pin.SPI, Pin.PWM};
        String[] pin32 = { Pin.AI};
        String[] pin33 = { Pin.AI};
        String[] pin34 = { Pin.AI};
        String[] pin35 = { Pin.AI};
        String[] pin36 = { Pin.AI};
        String[] pin37 = { Pin.AI};
        String[] pin38 = { Pin.AI};
        String[] pin39 = { Pin.AI};

        String[] pin40 = { Pin.AI};
        String[] pin41 = { Pin.GPIO};
        String[] pin42 = { Pin.GPIO, Pin.SPI, Pin.UART};
        String[] pin43 = { Pin.GND};
        String[] pin44 = { Pin.GND};
        String[] pin45 = { Pin.GND};
        String[] pin46 = { Pin.GND};
        String[] pin47 = { Pin.GND};
        String[] pin48 = { Pin.GND};
        String[] pin49 = { Pin.GPIO};

        String[] pin50 = { Pin.GPIO};
        String[] pin51 = { Pin.GPIO};
        String[] pin52 = { Pin.GPIO};
        String[] pin53 = { Pin.GPIO};
        String[] pin54 = { Pin.GPIO};
        String[] pin55 = { Pin.GPIO};
        String[] pin56 = { Pin.GPIO};
        String[] pin57 = { Pin.GPIO};
        String[] pin58 = { Pin.GPIO};
        String[] pin59 = { Pin.GPIO, Pin.PWM};

        String[] pin60 = { Pin.GPIO};
        String[] pin61 = { Pin.GPIO};
        String[] pin62 = { Pin.GPIO};
        String[] pin63 = { Pin.GPIO};
        String[] pin64 = { Pin.GPIO};
        String[] pin65 = { Pin.GPIO, Pin.PWM};
        String[] pin66 = { Pin.GPIO};
        String[] pin67 = { Pin.GPIO};
        String[] pin68 = { Pin.GPIO};
        String[] pin69 = { Pin.GPIO};

        String[] pin70 = { Pin.GPIO};
        String[] pin71 = { Pin.GPIO};
        String[] pin72 = { Pin.GPIO};
        String[] pin73 = { Pin.GPIO};
        String[] pin74 = { Pin.GPIO};
        String[] pin75 = { Pin.GPIO};
        String[] pin76 = { Pin.GPIO};
        String[] pin77 = { Pin.GPIO, Pin.UART};
        String[] pin78 = { Pin.GPIO, Pin.UART};
        String[] pin79 = { Pin.GPIO, Pin.UART};

        String[] pin80 = { Pin.GPIO, Pin.UART, Pin.PWM};
        String[] pin81 = { Pin.GPIO, Pin.UART};
        String[] pin82 = { Pin.GPIO, Pin.UART, Pin.PWM};
        String[] pin83 = { Pin.GPIO, Pin.UART};
        String[] pin84 = { Pin.GPIO, Pin.UART};
        String[] pin85 = { Pin.GPIO};
        String[] pin86 = { Pin.GPIO};
        String[] pin87 = { Pin.GPIO};
        String[] pin88 = { Pin.GPIO};
        String[] pin89 = { Pin.GPIO};

        String[] pin90 = { Pin.GPIO};
        String[] pin91 = { Pin.GPIO, Pin.PWM};
        String[] pin92 = { Pin.GPIO, Pin.PWM};

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
        hashMap.put(41, pin41);
        hashMap.put(42, pin42);
        hashMap.put(43, pin43);
        hashMap.put(44, pin44);
        hashMap.put(45, pin45);
        hashMap.put(46, pin46);
        hashMap.put(47, pin47);
        hashMap.put(48, pin48);
        hashMap.put(49, pin49);

        hashMap.put(50, pin50);
        hashMap.put(51, pin51);
        hashMap.put(52, pin52);
        hashMap.put(53, pin53);
        hashMap.put(54, pin54);
        hashMap.put(55, pin55);
        hashMap.put(56, pin56);
        hashMap.put(57, pin57);
        hashMap.put(58, pin58);
        hashMap.put(59, pin59);

        hashMap.put(60, pin60);
        hashMap.put(61, pin61);
        hashMap.put(62, pin62);
        hashMap.put(63, pin63);
        hashMap.put(64, pin64);
        hashMap.put(65, pin65);
        hashMap.put(66, pin66);
        hashMap.put(67, pin67);
        hashMap.put(68, pin68);
        hashMap.put(69, pin69);

        hashMap.put(70, pin70);
        hashMap.put(71, pin71);
        hashMap.put(72, pin72);
        hashMap.put(73, pin73);
        hashMap.put(74, pin74);
        hashMap.put(75, pin75);
        hashMap.put(76, pin76);
        hashMap.put(77, pin77);
        hashMap.put(78, pin78);
        hashMap.put(79, pin79);

        hashMap.put(80, pin80);
        hashMap.put(81, pin81);
        hashMap.put(82, pin82);
        hashMap.put(83, pin83);
        hashMap.put(84, pin84);
        hashMap.put(85, pin85);
        hashMap.put(86, pin86);
        hashMap.put(87, pin87);
        hashMap.put(88, pin88);
        hashMap.put(89, pin89);

        hashMap.put(90, pin90);
        hashMap.put(91, pin91);
        hashMap.put(92, pin92);
    }

    public String[] getValueByKey(int key) {
        return hashMap.get(key);
    }
}
