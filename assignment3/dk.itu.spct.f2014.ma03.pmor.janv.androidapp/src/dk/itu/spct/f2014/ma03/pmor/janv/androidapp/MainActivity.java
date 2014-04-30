package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.io.File;

import dk.itu.spct.f2014.ma03.pmor.janv.androidapp.MovementRecorderService.MovementRecorderServiceBinding;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
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
	
	private RecordingsAdapter listAdapter = new RecordingsAdapter(this);

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

		// attach recordings adapter to the list view
		ListView lv = (ListView) findViewById(R.id.recordingsListView);
		lv.setAdapter(listAdapter);
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
		// Service availability check.
		if(!bound) {
			Toast.makeText(this, "The recording service is currently unavailable.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Start accelerometer recording service in case it was stopped.
		this.startService(new Intent(this, MovementRecorderService.class));
		
		// Find out what type of recording the user wants to start.
		RadioGroup radioGrp = (RadioGroup) this.findViewById(R.id.activityRadioGroup);
		int selectedBtnId = radioGrp.getCheckedRadioButtonId();
		RecordingType recordingType = this.getRecordingTypeFromRadioButton(selectedBtnId);
		if(recordingType == null) {
			// Make sure something was selected.
			Toast.makeText(this, "Please select a recording type", Toast.LENGTH_SHORT).show();
			return;
		}
		// Tell the service to begin recording.
		this.service.startRecording(recordingType);
		
		// Update button text and listener.
		Button b = (Button) findViewById(R.id.recordButton);
		b.setText(R.string.stop_recording);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onStopRecordButtonClicked(v);
			}
		});
	}
	
	/**
	 * Get the {@link RecordingType} that corresponds to a given {@link RadioButton} id.
	 * @param selectedRadioButtonId The ID of the selected {@link RadioButton}.
	 * @return The {@link RecordingType} corresponding to the given radio button or null if there is no match.
	 */
	private RecordingType getRecordingTypeFromRadioButton(int selectedRadioButtonId) {
		switch(selectedRadioButtonId) {
		case R.id.activitySitRadio:
			return RecordingType.SITTING;
		case R.id.activityWalkRadio:
			return RecordingType.WALKING;
		case R.id.activityStairsRadio:
			return RecordingType.CLIMBING_STAIRS;
		default:
			return null;
		}
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
		this.service.stopRecording();
		
		/*
		final Recording r = null;
		
		AlertDialog.Builder dBuilder = new Builder(this);
		dBuilder.setMessage(R.string.save_question);
		dBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				keepRecording(r);
			}
		});
		dBuilder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				discardRecording(r);
			}
		});
		dBuilder.create().show();
		*/
	}
	
	public void onUploadButtonClicked(View v) {
		// upload all recording files to server
	}
	
	private void keepRecording(Recording r) {
		refreshRecordingList();
	}
	
	private void discardRecording(Recording r) {
		//new File(r.fileName).delete();
	}
	
	private void refreshRecordingList() {
		//this.listAdapter = new RecordingsAdapter(this);
		//ListView gv = (ListView) this.findViewById(R.id.recordingsListView);
		//gv.setAdapter(this.listAdapter);
		
		listAdapter.recordings.clear();
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
			File folder = new File(extFolder + R.string.directory);
			
			File[] files = folder.listFiles();
			for(File f : files) {
				listAdapter.recordings.add(readRecordingFile(f));
				listAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private static Recording readRecordingFile(File f) {
		String fileName = f.getName();
		String[] split = fileName.split("_");
		long timestamp = Long.parseLong(split[0]);
		RecordingType type = RecordingType.valueOf(split[1]);
		
		return new Recording(timestamp, type, f.getAbsolutePath());
	}
}
