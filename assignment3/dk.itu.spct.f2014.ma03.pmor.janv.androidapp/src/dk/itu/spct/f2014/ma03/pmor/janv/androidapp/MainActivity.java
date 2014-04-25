package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import dk.itu.spct.f2014.ma03.pmor.janv.androidapp.MovementRecorderService.MovementRecorderServiceBinding;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.os.Build;

public class MainActivity extends Activity {

	/**
	 * Provides an interface for the {@link MovementRecorderService}.
	 */
	private MovementRecorderServiceBinding service;

	/**
	 * Specifies if this activity is currently bound to the
	 * {@link MovementRecorderService}.
	 */
	private boolean bound = false;

	/**
	 * Service connection for connecting this activity to the
	 * {@link MovementRecorderService}.
	 */
	private final ServiceConnection serviceConn = new ServiceConnection() {

		/*
		 * NOTE: These callbacks are run on the main thread.
		 */

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			MainActivity.this.bound = false;
			MainActivity.this.service = null;
		}

		@Override
		public void onServiceConnected(ComponentName classInfo,
				IBinder serviceBinding) {
			if (serviceBinding instanceof MovementRecorderServiceBinding) {
				// Successfully bound to the movement service.
				MainActivity.this.service = (MovementRecorderServiceBinding) serviceBinding;
				MainActivity.this.bound = true;
			}
		}
	};

	/*
	 * TODO add startService to "start recording" btn handler.
	 */
	
	/*
	 * TODO add stopService to "stop recording" btn handler.
	 */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStart() {
		super.onStart();
		/*
		 * Start the service such that it will remain running even though the
		 * activity is recreated (e.g. due to a configuration change).
		 */
		ComponentName serviceName = this.startService(new Intent(this,
				MovementRecorderService.class));
		assert serviceName != null;
		/*
		 * Bind to service to get an interface for the service. This interface
		 * is delivered to the ServiceConnection implementation.
		 */
		Intent i = new Intent(this, MovementRecorderService.class);
		this.bindService(i, this.serviceConn, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (bound) {
			/*
			 * Unbind from the service but do not stop the service. The service
			 * is kept alive as the activity might be stopped while a recording
			 * is taking place. Service should only be stopped by the
			 * "stop recording" button handler.
			 */
			this.unbindService(this.serviceConn);
			this.bound = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onStartRecordButtonClicked(View v) {
		Button b = (Button) findViewById(R.id.recordButton);
		b.setText(R.string.stop_recording);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onStopRecordButtonClicked(v);
			}
		});
		
		// start accelerometer recording service
	}
	
	public void onStopRecordButtonClicked(View v) {
		Button b = (Button) findViewById(R.id.recordButton);
		b.setText(R.string.start_recording);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onStartRecordButtonClicked(v);
			}
		});
		
		// stop accelerometer recording service and retrieve result
		
		ListView lv = (ListView) findViewById(R.id.recordingsListView);
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			// attach recordings adapter to the list view
			ListView lv = (ListView) rootView.findViewById(R.id.recordingsListView);
			lv.setAdapter(new RecordingsAdapter(lv.getContext()));
			
			return rootView;
		}
	}

}
