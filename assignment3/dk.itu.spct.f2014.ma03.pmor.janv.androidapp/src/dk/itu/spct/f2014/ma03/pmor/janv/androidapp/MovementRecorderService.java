package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class MovementRecorderService extends Service implements SensorEventListener {

	private final MovementRecorderServiceBinding binding = new MovementRecorderServiceBinding();

	/**
	 * Thread performing the current recording.
	 */
	private RecorderThread recorderThread;

	/**
	 * Is the service currently recording movement?
	 */
	private boolean recording = false;

	/**
	 * If recording, what kind?
	 */
	private RecordingType recordingType = null;
	
	/**
	 * File data batch count for the current recording.
	 */
	private int batchCount = 0;
	
	/**
	 * Name of the file that any current recording is written to.
	 */
	private String currentFile = null;
	
	/**
	 * Thread responsible for writing readings to a file.
	 */
	private FileWriterThread fileWriterThread;

	/**
	 * Provides access to the device's sensors.
	 */
	private SensorManager sensorManager;
	
	/**
	 * The device's accelerometer.
	 */
	private Sensor accelerometer;
	
	private static final String newLine = System.getProperty("line.separator");
	
	@Override
	public void onCreate() {
		super.onCreate();
		/*
		 * We use the same FileWriterThread during the entire lifetime of the
		 * service.
		 */
		this.fileWriterThread = new FileWriterThread();
		this.fileWriterThread.start();
		this.sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	// TODO overwrite onStartCommand?
	
	@Override
	public IBinder onBind(Intent intent) {		
		return this.binding;
	}

	/**
	 * Provides binding to the {@link MovementRecorderService}.
	 * 
	 * @author Janus Varmarken
	 * 
	 */
	public class MovementRecorderServiceBinding extends Binder {

		/**
		 * Start a recording.
		 * 
		 * @param recordingType
		 *            The type of the recording.
		 */
		public void startRecording(RecordingType recordingType) {
			// Do not allow simultaneous recordings.
			if (MovementRecorderService.this.recording) {
				return;
			}
			MovementRecorderService.this.recording = true;
			MovementRecorderService.this.recordingType = recordingType;
			MovementRecorderService.this.batchCount = 0;
			// Generate filename.
			MovementRecorderService.this.currentFile = System.currentTimeMillis() + "_" + recordingType + ".csv";
			/*
			 *  Start listening for sensor readings.
			 *  Note this version makes the listener method run on the main thread.
			 *  Use overload with a Handler if you want the listener method to run at another thread.
			 *  It is okay to run it on the main thread as longs as blocking calls inside the listener method is delegated to another thread. 
			 */
			MovementRecorderService.this.sensorManager.registerListener(MovementRecorderService.this, MovementRecorderService.this.accelerometer, 50000);
//			MovementRecorderService.this.recorderThread = new RecorderThread(
//					recordingType);
//			MovementRecorderService.this.recorderThread.start();
		}

		/**
		 * Stop any current recording.
		 */
		public void stopRecording() {
			// Deactivate sensor.
			MovementRecorderService.this.sensorManager.unregisterListener(MovementRecorderService.this);
			
			// No longer recording.
			MovementRecorderService.this.recording = false;
			
			// Perform cleanup.
			MovementRecorderService.this.recordingType = null;
			MovementRecorderService.this.currentFile = null;
			MovementRecorderService.this.batchCount = 0;
			
//			// Stop recording by interrupting the RecorderThread.
//			MovementRecorderService.this.recorderThread.interrupt();
//			MovementRecorderService.this.recorderThread = null;
			
		}
	}

	private class RecorderThread extends Thread {

		/**
		 * The type of the recording that is performed by this thread.
		 */
		private final RecordingType recordingType;

		/**
		 * Name of the file containing this recording.
		 */
		private final String fileName;
		
		/**
		 * Create a new {@link RecorderThread}.
		 * 
		 * @param recordingType
		 *            The type of the recording to be performed.
		 */
		public RecorderThread(RecordingType recordingType) {
			this.fileName = System.currentTimeMillis() + "_" + recordingType + ".csv";
			this.recordingType = recordingType;
		}

		@Override
		public void run() {
			// Keep recording until interrupted.
			while (!this.isInterrupted()) {
				// TODO get sensor data.
				// TODO post reading to file writer thread.
				MovementRecorderService.this.fileWriterThread.handler
						.post(new Runnable() {

							@Override
							public void run() {
								// TODO Code to write to file here...
								// Check that external storage is available.
								if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
									String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
									File folder = new File(extFolder + R.string.directory);
									// Create dir if not already present.
									folder.mkdirs();
									File file = new File(folder + "/" + RecorderThread.this.fileName);
									// TODO create file if not exists.
									// TODO else append to file.
									
								}
							}
						});
				try {
					/*
					 * Perform readings at 20Hz (i.e. one reading every 0.05
					 * second).
					 */
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Log.d(this.getClass().getSimpleName(), this.getClass()
							.getSimpleName()
							+ " was interrupted while sleeping!");
					continue;
				}
			}
		}
	}

	/**
	 * A worker thread that runs during the entire lifetime of the
	 * {@link MovementRecorderService}. Its purpose is to accept
	 * {@link Runnable}s containing code that should be run asynchronously from
	 * the sensor thread and the GUI thread (e.g. writing sensor readings to a
	 * file).
	 * 
	 * @author Janus Varmarken
	 * 
	 */
	private class FileWriterThread extends Thread {
		
		/**
		 * Allows other threads to post work to this thread. The handler is
		 * initialized in {@link FileWriterThread#run()} in order to associate
		 * the handler with the this thread.
		 */
		private Handler handler;

		@Override
		public void run() {
			Looper.prepare();
			// Init handler, allowing other threads to post work to this thread.
			this.handler = new Handler();
			Looper.loop();
		}
	}

	/*
	 * Begin SensorEventListener callbacks.
	 */
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		/*
		 *  TODO this is called on main thread.
		 */
	}

	private List<SensorEvent> batch = new ArrayList<SensorEvent>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		/*
		 *  TODO this is called on main thread.
		 *  Post reading to FileWriterThread to do quick return.
		 */
		 Log.d("onSensorChanged", "onSensorChanged is running in UI thread = " + (Looper.getMainLooper() == Looper.myLooper()));
		 Log.d("onSensorChanged", "Sensor values: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
		 this.batch.add(event);
		 if(this.batch.size() < 20) {
			 // Wait for more readings before writing to file.
			 return;
		 } else {
			 // Send batch to file...
			 // Get batch to write.
			 final List<SensorEvent> tmp = this.batch;
			 // Reset for new readings.
			 this.batch = new ArrayList<>();
			 this.fileWriterThread.handler.post(new RecordingBatchWriter(this.currentFile, tmp, batchCount));
			 batchCount++;
		 }
	}
	
	/*
	 * End SensorEventListener callbacks.
	 */
	/**
	 * Writes a recording batch to a file.
	 * @author Janus Varmarken
	 *
	 */
	private class RecordingBatchWriter implements Runnable {
		
		private final String fileName;
		private final List<SensorEvent> batch;
		private final int batchCount;
		
		/**
		 * Creates a new {@link RecordingBatchWriter}.
		 * @param fileName The name of the file to write the batch to.
		 * @param batch The batch to write.
		 * @param batchCount What batch number for the given file is this?
		 * 			Count starts at zero. If zero, a new file is created. If non-zero an existing file is assumed and the batch is appended to this file.
		 */
		public RecordingBatchWriter(String fileName, List<SensorEvent> batch, int batchCount) {
			this.fileName = fileName;
			this.batch = batch;
			this.batchCount = batchCount;
		}
		
		@Override
		public void run() {
			FileWriter writer = null;
			try {
					// Check that external storage is available.
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//						String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
						String extFolder = MovementRecorderService.this.getExternalFilesDir(null).getAbsolutePath();
						File folder = new File(extFolder + MovementRecorderService.this.getString(R.string.directory));
						// Create dir if not already present.
						boolean folderCreated = folder.mkdirs();
						File file = new File(folder + "/" + this.fileName);
						if(batchCount == 0) {
							// This is a new recording, so create the file.
							boolean created = file.createNewFile();
							assert created;
							// Init writer.
							writer = (new FileWriter(file, false));
							// Create headers.
							writer.write("timestamp; x; y; z;" + newLine);
						}
						else {
							// File should exist, headers should already be present.
							// Init writer with append = true.
							writer = (new FileWriter(file, true));
						}
						for(SensorEvent evt : this.batch) {
							writer.write(evt.timestamp + "; " + evt.values[0] + "; " + evt.values[1] + "; " + evt.values[2] + ";" + newLine);
						}
					}
				} catch(IOException ioe) {
					Log.e(this.getClass().getSimpleName(), "error when writing readings to file");
				} finally {
					if(writer != null) {
						try {
							writer.flush();
							writer.close();
						} catch (IOException e) {
							// too bad.
							Log.e(this.getClass().getSimpleName(), "IOException when closing FileWriter in finally block.");
						}
					}
			}
		}
	}
}
