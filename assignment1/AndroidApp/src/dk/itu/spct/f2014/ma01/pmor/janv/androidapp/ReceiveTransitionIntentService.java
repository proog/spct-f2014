package dk.itu.spct.f2014.ma01.pmor.janv.androidapp;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;

import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network.BluetoothManager;
import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network.TriggerServerClient;
import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network.TriggerServerMessage;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ReceiveTransitionIntentService extends IntentService {
	public ReceiveTransitionIntentService() {
		super("ReceiveTransitionIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		if(LocationClient.hasError(intent)) {
			Toast.makeText(this, "Error in intent from location services", Toast.LENGTH_LONG).show();
			return;
		}
		
		int transitionType = LocationClient.getGeofenceTransition(intent);
		if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
			// start blip tracking
			TriggerServerClient client = new TriggerServerClient();
			String host = TriggerServerClient.TRIGGER_SERVER_HOST_ADDRESS;
			String port = TriggerServerClient.TRIGGER_SERVER_PORT;
			
			TriggerServerMessage msg = new TriggerServerMessage(
					TriggerServerMessage.ACTION_START, intent.getExtras().getString("usersName"),
					BluetoothManager.getBluetoothId());
			String json = new Gson().toJson(msg);
			
			Log.d(MainActivity.class.getSimpleName(), "Sending: " + json);
			client.execute(host, port, json);
			
			// Turn on Bluetooth such that BLIP can detect this device.
			BluetoothManager.toggleBluetooth(true);
		}
		else if(transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
			// stop blip tracking
		}
	}
}
