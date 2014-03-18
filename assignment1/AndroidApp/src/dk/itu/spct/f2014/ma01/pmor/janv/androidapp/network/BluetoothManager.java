package dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network;

import android.bluetooth.BluetoothAdapter;

public class BluetoothManager {
	/**
	 * Get the Bluetooth ID of this device.
	 * 
	 * @return The Bluetooth ID or null if this device does not support
	 *         Bluetooth.
	 */
	public static String getBluetoothId() {
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if (ba == null)
			return null;

		return ba.getAddress().replace(":", "").toLowerCase();
	}

	/**
	 * Toggles Bluetooth on/off.
	 * 
	 * @param on
	 *            {@code true} to toggle Bluetooth on, {@code false} to toggle
	 *            Bluetooth off.
	 */
	public static void toggleBluetooth(boolean on) {
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if (ba == null)
			return;
		
		if (on)
			ba.enable();
		else
			ba.disable();
	}
}
