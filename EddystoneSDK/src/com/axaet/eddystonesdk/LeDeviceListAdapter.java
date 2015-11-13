package com.axaet.eddystonesdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.axaet.eddystonesdk.EddystoneClass.Eddystone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This is a hybrid of the three modes of the adapter.Some properties may be
 * hidden under different modes.
 * 
 * @author Administrator
 *
 */
public class LeDeviceListAdapter extends BaseAdapter {

	private static ArrayList<Eddystone> mLeDevices;
	private LayoutInflater mInflator;
	private Activity mContext;

	public LeDeviceListAdapter(Activity c) {
		super();
		mContext = c;
		mLeDevices = new ArrayList<Eddystone>();
		mInflator = mContext.getLayoutInflater();
	}

	/**
	 * Add data,And sort by RSSI
	 * 
	 * @param hybrid
	 */
	public void addDevice(Eddystone eddystone) {
		if (eddystone == null)
			return;
		for (int i = 0; i < mLeDevices.size(); i++) {
			String btAddress = mLeDevices.get(i).bluetoothAddress;
			if (btAddress.equals(eddystone.bluetoothAddress)) {
				mLeDevices.add(i + 1, eddystone);
				mLeDevices.remove(i);
				return;
			}
		}
		mLeDevices.add(eddystone);
		Collections.sort(mLeDevices, new Comparator<Eddystone>() {
			@Override
			public int compare(Eddystone h1, Eddystone h2) {
				return h2.rssi - h1.rssi;
			}
		});
	}

	public Eddystone getDevice(int position) {
		return mLeDevices.get(position);
	}

	public void clear() {
		mLeDevices.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mLeDevices.size();
	}

	@Override
	public Object getItem(int i) {
		return mLeDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		if (view == null) {
			view = mInflator.inflate(R.layout.item_scan, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceName = (TextView) view.findViewById(R.id.deviceName);
			viewHolder.deviceAddress = (TextView) view.findViewById(R.id.deviceAddress);
			viewHolder.ID2 = (TextView) view.findViewById(R.id.ID2);
			viewHolder.ID3 = (TextView) view.findViewById(R.id.ID3);
			viewHolder.RSSI = (TextView) view.findViewById(R.id.rssi);
			viewHolder.txpower = (TextView) view.findViewById(R.id.txpower);
			viewHolder.type = (TextView) view.findViewById(R.id.type);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		Eddystone device = mLeDevices.get(i);
		final String deviceName = device.name;
		if (deviceName != null && deviceName.length() > 0)
			viewHolder.deviceName.setText("deviceName: " + deviceName);
		else
			viewHolder.deviceName.setText("deviceName: unknown");
		viewHolder.deviceAddress.setText("address: " + device.bluetoothAddress);
		viewHolder.txpower.setVisibility(View.VISIBLE);
		/**
		 * If device is the iBeacon mode, the pattern is used to display the
		 * data
		 */
		if (device.name.contains("iBeacon")) {
			viewHolder.txpower.setText("TxPower: " + device.txPower);
			viewHolder.type.setText(device.type);
			viewHolder.ID2.setVisibility(View.VISIBLE);
			viewHolder.ID3.setVisibility(View.VISIBLE);
			viewHolder.RSSI.setVisibility(View.VISIBLE);
			viewHolder.ID2.setText("ID2: " + device.ID2);
			viewHolder.ID3.setText("ID3: " + device.ID3);
			viewHolder.RSSI.setText("rssi: " + device.rssi);
		}
		/**
		 * If device is the Eddystone-UID mode, the pattern is used to display
		 * the data
		 */
		else if (device.name.contains("UID")) {
			viewHolder.txpower.setText("TxPower: " + device.txPower);
			viewHolder.type.setText(device.type);
			viewHolder.RSSI.setText("rssi: " + device.rssi);
			viewHolder.ID2.setText("ID2: " + device.ID2);
			viewHolder.ID3.setVisibility(View.GONE);
		}
		/**
		 * If device is the Eddystone-URL mode, the pattern is used to display
		 * the data
		 */
		else if (device.name.contains("URL")) {
			viewHolder.type.setText(device.type);
			viewHolder.RSSI.setVisibility(View.GONE);
			viewHolder.ID2.setText("rssi: " + device.rssi);
			viewHolder.ID3.setVisibility(View.GONE);
			viewHolder.txpower.setVisibility(View.GONE);
		}
		return view;
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		TextView type;
		TextView ID2;
		TextView ID3;
		TextView txpower;
		TextView RSSI;
	}

}
