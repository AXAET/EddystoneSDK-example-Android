package com.axaet.eddystonesdk;

import java.io.Serializable;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

/**
 * Interpretation of broadcast data
 * 
 * You can refer here.
 * https://github.com/google/eddystone/blob/master/protocol-specification.md
 * 
 * @author Administrator
 *
 */
public class EddystoneClass {
	// Eddystone-URL  Broadcast data has been converted into 16 binary data.     http://www.jerry@163.com
	// 020106   0303aafe   10(length,From the right of the 16 to the last 07)   16aafe  10 e9(txpower,Need to turn back to 10 binary data)  00(http://www.) 4a(j) 65(e)  72(r)  72(r)  79(y)  40(@) 31(1) 36(6) 33(3) 07(.com)  

	//Eddystone-iBeacon
	//0201061aff4c000215  fda50693a4e24fb1afcfc6eb07647825(uuid)  2938(major,Need to turn back to 10 binary data) 5048(minor,Need to turn back to 10 binary data) c0(txpower)
	
	//Eddystone-UID
	//0201060303aafe1716aafe00  e9(txpower,Need to turn back to 10 binary data)   001122334455667788ff(namespace ID)   aabbccddee99(instance ID)  0000(Retain)
	
	
	/**
	 * Different modes of transmission of the broadcast data are different,
	 * so the following constants are used to distinguish different patterns
	 */
	private static String iBeaconType = "0215";
	private static String UIDType = "00";
	private static String other = "0303aafe";
	private static String URLType = "10";

	
	
	public static class Eddystone implements Serializable{
	
		private static final long serialVersionUID = 1L;
		/**
		 * Device name
		 */
		public String name;
		/**
		 * Device bluetoothAddress
		 */
		public String bluetoothAddress;
		public int txPower;
		public int rssi;
		/**
		 * It may be UUID on iBeacon mode,or may be Namespace ID on
		 * Eddystone-UID mode;or may be url on Eddystone-URL
		 */
		public String type;
		/**
		 * It may be major, minor on iBeacon mode,or may be Instance ID on
		 * Eddystone-UID
		 */
		public String ID2, ID3;
	}

	@SuppressLint("DefaultLocale")
	public static Eddystone fromScanData(BluetoothDevice device, int rssi, byte[] scanData) {
		
		/**
		 * The byte array into a 16 - band string
		 */
		String scanDataHex = Conversion.bytesToHexString(scanData);
		
		String tempiBeacon = scanDataHex.substring(14, 18);
		String tempOther = scanDataHex.substring(6, 14);
	
//--------------------------This is an iBeacon------------------------------------------------------------------------
		if (iBeaconType.equals(tempiBeacon)) {
			int startByte = 2;
			boolean patternFound = false;
			while (startByte <= 5) {
				if (((int) scanData[startByte + 2] & 0xff) == 0x02 && ((int) scanData[startByte + 3] & 0xff) == 0x15) {
					// yes! This is an iBeacon
					patternFound = true;
					break;
				}
				startByte++;
			}
			if (patternFound == false) {
				// This is not an iBeacon
				return null;
			}
			Eddystone iBeacon = new Eddystone();
			/**
			 * iBeacon.ID2 is major
			 */
			iBeacon.ID2 = ((scanData[startByte + 20] & 0xff) * 0x100 + (scanData[startByte + 21] & 0xff) + "")
					.toUpperCase();
			/**
			 * iBeacon.ID3 is minor
			 */
			iBeacon.ID3 = ((scanData[startByte + 22] & 0xff) * 0x100 + (scanData[startByte + 23] & 0xff) + "")
					.toUpperCase();
			iBeacon.txPower = (int) scanData[startByte + 24];
			
			iBeacon.rssi = rssi;
			byte[] proximityUuidBytes = new byte[16];
			System.arraycopy(scanData, startByte + 4, proximityUuidBytes, 0, 16);
			String hexString = Conversion.bytesToHexString(proximityUuidBytes);
			StringBuilder sb = new StringBuilder();
			sb.append(hexString.substring(0, 8));
			sb.append("-");
			sb.append(hexString.substring(8, 12));
			sb.append("-");
			sb.append(hexString.substring(12, 16));
			sb.append("-");
			sb.append(hexString.substring(16, 20));
			sb.append("-");
			sb.append(hexString.substring(20, 32));
			/**
			 * iBeacon.type is UUID
			 */
			iBeacon.type = sb.toString().toUpperCase();
			if (device != null) {
				iBeacon.bluetoothAddress = device.getAddress();
				/**
				 * Plus ibeacon is mainly for the LeDeviceListAdapter class show a good distinction
				 */
				iBeacon.name = device.getName() + "(iBeacon)";
			}
			return iBeacon;
		} 
		
//----------------------This is an Eddystone-UID-----------------------------------------------------------------		
		else if (other.equals(tempOther)) {
			// This is an Eddystone-UID
			try {
				String UID = scanDataHex.substring(22, 24);
				if (UIDType.equals(UID)) {
					Eddystone UidInstance = new Eddystone();
					if (device != null) {
						/**
						 * Plus Eddystone UID is mainly for the LeDeviceListAdapter class show a good distinction
						 */
						UidInstance.name = device.getName() + "(Eddystone UID)";
						UidInstance.bluetoothAddress = device.getAddress();
					}
					/**
					 * UidInstance.ID2 is Namespace ID
					 */
					UidInstance.ID2 = scanDataHex.substring(46, 58).toUpperCase();

					UidInstance.txPower = (int) scanData[12] - 41;

					UidInstance.rssi = rssi;
					/**
					 * UidInstance.type is Instance ID
					 */
					UidInstance.type = scanDataHex.substring(26, 46).toUpperCase();
					return UidInstance;
				} 
				
//----------------------This is an Eddystone-URL-----------------------------------------------------------------		
				else if (URLType.equals(UID)) {
					// This is an Eddystone-URL
					Eddystone UrlInstance = new Eddystone();
					if (device != null) {
						/**
						 * Plus Eddystone URL is mainly for the LeDeviceListAdapter class show a good distinction
						 */
						UrlInstance.name = device.getName() + "(Eddystone URL)";
						UrlInstance.bluetoothAddress = device.getAddress();
					}
					UrlInstance.txPower = (int) scanData[12] - 41;
					UrlInstance.rssi = rssi;
					/**
					 * URL prefix
					 */
					String urlprefix=Conversion.calculateURLpro(scanDataHex.substring(26,28));
					/**
					 * URL suffix
					 */
					String URLsuffix=Conversion.calculateURL(scanDataHex.substring(28,
							28 + (Integer.parseInt(scanDataHex.substring(14, 16), 16) - 6) * 2));
					/**
					 * UrlInstance.type is Website url
					 */
					UrlInstance.type = urlprefix+URLsuffix;
							
					return UrlInstance;
				}

			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
}
