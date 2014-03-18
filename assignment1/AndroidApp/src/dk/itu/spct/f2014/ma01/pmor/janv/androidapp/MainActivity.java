package dk.itu.spct.f2014.ma01.pmor.janv.androidapp;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.gson.Gson;

import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.location.LocationManager;
import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network.BluetoothManager;
import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network.TriggerServerClient;
import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network.TriggerServerMessage;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private LocationManager locationManager;

	/**
	 * Listener to be invoked when button is in "Start tracking" mode.
	 */
	private final View.OnClickListener btnOnStartListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (BluetoothManager.getBluetoothId() == null) {
				Toast.makeText(MainActivity.this,
						R.string.toast_bluetooth_not_supported,
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			EditText txtName = (EditText) MainActivity.this
					.findViewById(R.id.txtName);
			String usersName = txtName.getText().toString();
			
			if (usersName.length() < 1) {
				Toast.makeText(MainActivity.this, R.string.toast_name_missing,
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			// TODO outline:
			// 1) begin monitoring position using GPS.
			// 2) change button functionality to "Stop tracking"
			MainActivity.this.locationManager = new LocationManager(MainActivity.this, usersName);
			MainActivity.this.locationManager.connect();
			
			// Change button behavior to "stop tracking"
			Button btnOnStart = (Button) v;
			btnOnStart.setText(R.string.lbl_btn_stop);
			btnOnStart.setOnClickListener(MainActivity.this.btnOnStopListener);
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
			
			// Change button behavior to "start tracking"
			Button btnOnStart = (Button) v;
			btnOnStart.setText(R.string.lbl_btn_start);
			btnOnStart.setOnClickListener(MainActivity.this.btnOnStartListener);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.startService(new Intent(this, ReceiveTransitionIntentService.class));
		setContentView(R.layout.activity_main);
		Button btnStartStop = (Button) this
				.findViewById(R.id.btnStartStopTracking);
		// Initialize button in button in "Start tracking" mode.
		btnStartStop.setOnClickListener(this.btnOnStartListener);
		btnStartStop.setText(R.string.lbl_btn_start);
	}

	@Override
	protected void onStart() {
		super.onStart();
		int canPlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
