package com.axaet.eddystonesdk;

import com.axaet.application.MyApplication;

/**
 * Tools class
 * 
 * @author Administrator
 *
 */
public class Conversion {

	/**
	 * Turn byte data into a  16 hex string
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 16 hex into Ascii
	 * 
	 * @param s
	 * @return
	 */
	private static  String HexToAscii(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/**
	 * Calculating distance
	 * 
	 * @param txPower
	 * @param rssi
	 * @return
	 */
	public static double calculateAccuracy(int txPower, double rssi) {
		if (rssi == 0) {
			return -1.0; // if we cannot determine accuracy, return -1.
		}
		double ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			return accuracy;
		}
	}

	/**
	 * Analysis of Web site prefix in URL mode based on broadcast data
	 * 
	 * @param urlPro
	 * @return
	 */
	public static String calculateURLpro(String urlPro) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < urlPro.length(); i += 2) {
			String tempBit = urlPro.substring(i, i + 2);
			String temp = MyApplication.hashMapPro.get(tempBit);
			if (temp == null) {
				temp = Conversion.HexToAscii(tempBit);
			}
			buffer.append(temp);
		}
		return buffer.toString();
	}

	/**
	 * Analysis of Web sites in URL mode based on broadcast data
	 * 
	 * @param urlTemp
	 * @return
	 */
	public static String calculateURL(String urlTemp) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < urlTemp.length(); i += 2) {
			String tempBit = urlTemp.substring(i, i + 2);
			String temp = MyApplication.hashMap.get(tempBit);
			if (temp == null) {
				temp = Conversion.HexToAscii(tempBit);
			}
			buffer.append(temp);
		}
		return buffer.toString();
	}

}
