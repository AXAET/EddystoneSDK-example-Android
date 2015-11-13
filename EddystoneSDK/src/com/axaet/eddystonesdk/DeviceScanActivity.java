/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axaet.eddystonesdk;

import com.axaet.eddystonesdk.EddystoneClass.Eddystone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@SuppressLint("NewApi")
public class DeviceScanActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter;
	private ImageView image_refresh;
	private ListView listView;
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private Animation anim;
	private static final int REQUEST_ENABLE_BT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan);

		// -----------------------------------------------------------------------------------------
		/**
		 * Check if the current mobile phone supports ble Bluetooth, if you do
		 * not support the exit program
		 */
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		/**
		 * Adapter Bluetooth, get a reference to the Bluetooth adapter (API),
		 * which must be above android4.3 or above.
		 */
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		/**
		 * Check whether the device supports Bluetooth
		 */
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		/**
		 * Refresh button animation
		 */
		anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		listView = (ListView) findViewById(R.id.listView);

		image_refresh = (ImageView) findViewById(R.id.refresh);
		image_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mLeDeviceListAdapter.clear();
				scanLeDevice(true);
			}
		});
		mLeDeviceListAdapter = new LeDeviceListAdapter(this);
		listView.setAdapter(mLeDeviceListAdapter);
		mLeDeviceListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * In order to ensure that the device can be used in Bluetooth, if the
		 * current Bluetooth device is not enabled, the pop-up dialog box to the
		 * user to grant permissions to enable
		 */
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		mLeDeviceListAdapter.clear();
		image_refresh.clearAnimation();
		image_refresh.startAnimation(anim);
		scanLeDevice(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
	}

	/**
	 * scan ble
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	/**
	 * Device scan callback.
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			/**
			 * Package data into Eddystone
			 */
			final Eddystone eddystone = EddystoneClass.fromScanData(device, rssi, scanRecord);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(eddystone);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			});
		}
	};
}