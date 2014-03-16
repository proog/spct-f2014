package dk.itu.spct.f2014.ma01.pmor.janv.androidapp;

import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network.TriggerServerClient;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	/**
	 * Listener to be invoked when button is in "Start tracking" mode.
	 */
	private final View.OnClickListener btnOnStartListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO outline:
			// 1) begin monitoring position using GPS.
			// 2) change button functionality to "Stop tracking"
			Button btnOnStart = (Button) v;
			btnOnStart.setText(R.string.lbl_btn_stop);
			// Change button behavior to "stop tracking"
			btnOnStart.setOnClickListener(MainActivity.this.btnOnStopListener);

			TriggerServerClient client = new TriggerServerClient();
			String[] args = new String[3];
			args[0] = "172.20.10.3";
			args[1] = "3345";
			args[2] = String.format(
					"{ \"%s\":\"%s\", \"%s\":\"%s\", \"%s\":\"%s\" }",
					"action", "start", "name", "janus", "deviceId", MainActivity.this.getBluetoothId());
			Toast.makeText(MainActivity.this, "Sending: " + args[2],
					Toast.LENGTH_LONG).show();
			client.execute(args);
		}
	};

	/**
	 * Listener to be invoked when button is in "Stop tracking" mode.
	 */
	private final View.OnClickListener btnOnStopListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO outline:
			// 1) stop monitoring position using GPS.
			// 2) send stop message to TriggerServer.
			// 3) Change button functionality to "Start tracking"
			Button btnOnStart = (Button) v;
			btnOnStart.setText(R.string.lbl_btn_start);
			// Change button behavior to "start tracking"
			btnOnStart.setOnClickListener(MainActivity.this.btnOnStartListener);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btnStartStop = (Button) this
				.findViewById(R.id.btnStartStopTracking);
		// Initialize button in button in "Start tracking" mode.
		btnStartStop.setOnClickListener(this.btnOnStartListener);
		btnStartStop.setText(R.string.lbl_btn_start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Get the Bluetooth ID of this device.
	 * @return The Bluetooth ID or null if this device does not support Bluetooth.
	 */
	private String getBluetoothId() {
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		if(ba == null) {
			return null;
		}
		return ba.getAddress();
	}
}
