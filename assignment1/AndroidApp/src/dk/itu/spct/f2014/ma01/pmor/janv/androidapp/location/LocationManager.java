package dk.itu.spct.f2014.ma01.pmor.janv.androidapp.location;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;

import dk.itu.spct.f2014.ma01.pmor.janv.androidapp.ReceiveTransitionIntentService;

public class LocationManager implements ConnectionCallbacks, OnConnectionFailedListener, OnAddGeofencesResultListener {
	private Context context;
	private Geofence geofence;
	private LocationClient locationClient;
	private String usersName;
	
	public LocationManager(Context c, String usersName) {
		this.context = c;
		this.usersName = usersName;
	}
	
	/**
	 * Starts the location request process
	 */
	public void connect() {
		this.locationClient = new LocationClient(this.context, this, this);
		this.locationClient.connect();
	}
	
	private PendingIntent getTransitionPendingIntent() {
		Intent intent = new Intent(this.context, ReceiveTransitionIntentService.class);
		intent.putExtra("usersName", this.usersName);
		return PendingIntent.getService(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	@Override
	public void onAddGeofencesResult(int arg0, String[] arg1) {
		if(arg0 == LocationStatusCodes.SUCCESS)
			Toast.makeText(this.context, "Geofence added", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this.context, "Failed adding geofence", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this.context, "Connection to location services failed.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		Geofence.Builder builder = new Geofence.Builder();
		builder.setRequestId("itu");
		builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
		builder.setCircularRegion(55.659650, 12.591017, 200);
		builder.setLoiteringDelay(10000); // 10 sec between ENTER and DWELL
		builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
		this.geofence = builder.build();
		
		List<Geofence> gfList = new ArrayList<Geofence>();
		gfList.add(this.geofence);
		PendingIntent pendingIntent = this.getTransitionPendingIntent();
		this.locationClient.addGeofences(gfList, pendingIntent, this);
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this.context, "Disconnected from location services", Toast.LENGTH_LONG).show();
	}
}
