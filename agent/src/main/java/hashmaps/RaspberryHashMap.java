package hashmaps;

import io.silverspoon.bulldog.raspberrypi.RaspiNames;

import java.util.HashMap;

public class RaspberryHashMap {

	public static final int PIN_COUNT = 40;
	private HashMap<Integer, String[]> hashMap = new HashMap<Integer, String[]>();

	public RaspberryHashMap() {
		String[] pin3 = { RaspiNames.P1_3, RaspiNames.I2C_0 };
		String[] pin5 = { RaspiNames.P1_5, RaspiNames.I2C_0 };
		String[] pin7 = { RaspiNames.P1_7 };
		String[] pin8 = { RaspiNames.P1_8 };
		String[] pin10 = { RaspiNames.P1_10 };
		String[] pin11 = { RaspiNames.P1_11 };
		String[] pin12 = { RaspiNames.P1_12 };
		String[] pin13 = { RaspiNames.P1_13 };
		String[] pin15 = { RaspiNames.P1_15 };
		String[] pin16 = { RaspiNames.P1_16 };
		String[] pin18 = { RaspiNames.P1_18 };
		String[] pin19 = { RaspiNames.P1_19 };
		String[] pin21 = { RaspiNames.P1_21 };
		String[] pin22 = { RaspiNames.P1_22 };
		String[] pin23 = { RaspiNames.P1_23 };
		String[] pin24 = { RaspiNames.P1_24, RaspiNames.SPI_0_CS0 };
		String[] pin26 = { RaspiNames.P1_26, RaspiNames.SPI_0_CS1 };
		String[] pin29 = { RaspiNames.P1_29 };
		String[] pin31 = { RaspiNames.P1_31 };
		String[] pin32 = { RaspiNames.P1_32 };
		String[] pin33 = { RaspiNames.P1_33 };
		String[] pin35 = { RaspiNames.P1_35 };
		String[] pin36 = { RaspiNames.P1_36 };
		String[] pin37 = { RaspiNames.P1_37 };
		String[] pin38 = { RaspiNames.P1_38 };
		String[] pin40 = { RaspiNames.P1_40 };
		

		hashMap.put(3, pin3);
		hashMap.put(5, pin5);
		hashMap.put(7, pin7);
		hashMap.put(8, pin8);
		hashMap.put(10, pin10);
		hashMap.put(11, pin11);
		hashMap.put(12, pin12);
		hashMap.put(13, pin13);
		hashMap.put(15, pin15);
		hashMap.put(16, pin16);
		hashMap.put(18, pin18);
		hashMap.put(19, pin19);
		hashMap.put(21, pin21);
		hashMap.put(22, pin22);
		hashMap.put(23, pin23);
		hashMap.put(24, pin24);
		hashMap.put(26, pin26);
		hashMap.put(29, pin29);
		hashMap.put(31, pin31);
		hashMap.put(32, pin32);
		hashMap.put(33, pin33);
		hashMap.put(35, pin35);
		hashMap.put(36, pin36);
		hashMap.put(37, pin37);
		hashMap.put(38, pin38);
		hashMap.put(40, pin40);
	}

	public String[] getValueByKey(int key) {
		return (String[]) hashMap.get(key);
	}
}
