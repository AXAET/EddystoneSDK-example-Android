package com.axaet.application;

import java.util.HashMap;

import android.app.Application;

public class MyApplication extends Application {
	/**
	 * Web site suffix URL
	 */
	public static HashMap<String, String> hashMap;
	/**
	 * URL prefix in Eddystone-URL mode
	 */
	public static HashMap<String, String> hashMapPro;

	@Override
	public void onCreate() {
		super.onCreate();
		hashMap = new HashMap<String, String>();
		hashMap.put("00", ".com/");
		hashMap.put("01", ".org/");
		hashMap.put("02", ".edu/");
		hashMap.put("03", ".net/");
		hashMap.put("04", ".info/");
		hashMap.put("05", ".biz/");
		hashMap.put("06", ".gov/");
		hashMap.put("07", ".com");
		hashMap.put("08", ".org");
		hashMap.put("09", ".edu");
		hashMap.put("0a", ".net");
		hashMap.put("0b", ".info");
		hashMap.put("0c", ".biz");
		hashMap.put("0d", ".gov");
		hashMapPro = new HashMap<String, String>();
		hashMapPro.put("00", "http://www.");
		hashMapPro.put("01", "https://www.");
		hashMapPro.put("02", "http://");
		hashMapPro.put("03", "https://");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		System.exit(0);
	}

}
