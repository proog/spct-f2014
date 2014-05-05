package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.input.ReversedLinesFileReader;

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
import android.widget.Toast;

public class MovementRecorderService extends Service implements SensorEventListener {

	/**
	 * Separator for separating values in .csv files.
	 */
	public static final String ITEM_SEPARATOR = ";";
	
	/**
	 * Minimum number of nanoseconds to remove from end of file (to remove noise).
	 * 5 seconds.
	 */
	private static final long truncateNanos = 5000000000L;
	
	/**
	 * Tag used when logging file read errors.
	 */
	private static final String fileReadErrorTag = "file_error_read";
	
	/**
	 * Tag used when lgging file write errors.
	 */
	private static final String fileWriteErrorTag = "file_error_write";
	
	private final MovementRecorderServiceBinding binding = new MovementRecorderServiceBinding();

	/**
	 * The directory where the recordings are stored.
	 */
	private String filesDir;

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
	
	private int lineCountWithNoise;
	
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
		this.filesDir = this.getExternalFilesDir(null).getAbsolutePath() + this.getString(R.string.directory);
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
			MovementRecorderService.this.lineCountWithNoise = 0;
			// Generate filename.
			MovementRecorderService.this.currentFile = System.currentTimeMillis() + "_" + recordingType + "_.csv";
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
		public Recording stopRecording() {
			// Deactivate sensor.
			MovementRecorderService.this.sensorManager.unregisterListener(MovementRecorderService.this);
			
			
			
			// Write any data accumulated in the "buffer".
			MovementRecorderService.this.writeBatchToFile();
			
			// No longer recording.
			MovementRecorderService.this.recording = false;
			
			// Produce model object for this recording.
			final Recording recording = new Recording(MovementRecorderService.this.recordingType, MovementRecorderService.this.currentFile);
			
			// Post job to remove noisy data from end of file.
			MovementRecorderService.this.fileWriterThread.handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("file length", "File length with noise: " + MovementRecorderService.this.lineCountWithNoise + " lines.");
					MovementRecorderService.this.lineCountWithNoise = 0;
					
					boolean noiseRemoved = MovementRecorderService.this.postProcessRecording(recording);
					if(!noiseRemoved) {
						// If we could not remove noise, we discard the recording.
						File f = new File(MovementRecorderService.this.filesDir + "/" + recording.fileName);
						boolean deleted = f.delete();
						Log.d("stopRecording()", "recording automatically deleted: " + deleted);
						if(deleted) {
							Toast.makeText(MovementRecorderService.this,
									"Your recording was too short and hence automatically deleted. A recording must be at least "
											+ truncateNanos / 1000000000L + " seconds long.",  Toast.LENGTH_LONG)
									.show();
						}
					}
				}
			});
			
			// Perform cleanup.
			MovementRecorderService.this.recordingType = null;
			MovementRecorderService.this.currentFile = null;
			MovementRecorderService.this.batchCount = 0;
			
			return recording;
		}
	}

	private boolean postProcessRecording(Recording r) {
		File origFile = new File(this.filesDir + "/" + r.fileName);
		// Reads original file from end to front.
		ReversedLinesFileReader reader = null;
		String lastLine;
		String[] lastLineParts;
		// Final line to be included in post processed file.
		String finalLine = null;
		// Current read line.
		String line = null;
		try {
			reader = new ReversedLinesFileReader(origFile, 4096, "UTF-8");
			lastLine = reader.readLine();
			if(lastLine == null) {
				// No data in file.
				return false;
			}
			lastLineParts = lastLine.split(ITEM_SEPARATOR);
			// Read timestamp for last line.
			Long lastLineTimestamp = null;
			try {
				lastLineTimestamp = Long.parseLong(lastLineParts[0]);	
			} catch(NumberFormatException nfe) {
				/*
				 * This implies that the only data in the file is the header line.
				 */
				return false;
			}
			/*
			 * Now ready to read line by line in reverse until we find a line with an early-enough timestamp.
			 */
			while((line = reader.readLine()) != null) {
				String[] parts = line.split(ITEM_SEPARATOR);
				Long lineTimestamp = null;
				try {
					lineTimestamp = Long.parseLong(parts[0]);	
				} catch(NumberFormatException nfe) {
					/*
					 * This implies that we have reached the header line (the first line in the file).
					 * Hence there is not enough data in the file to discard noise.
					 */
					return false;
				}
				if(lastLineTimestamp - lineTimestamp >= truncateNanos) {
					// Found a sufficient gap to remove.
					// We log what is to be the final line.
					finalLine = line;
					break;
				}
			}
		} catch (IOException e) {
			Log.e(fileReadErrorTag, "error when reading original file in reverse mode");
			return false;
		} finally {
			// Free resources.
			try {
				if(reader != null) {					
					reader.close();
					Log.d(ReversedLinesFileReader.class.getSimpleName(), "successfully closed " + ReversedLinesFileReader.class.getSimpleName());
				}
			} catch (IOException e) {
				// Too bad.
				Log.e(fileReadErrorTag, "could not close " + ReversedLinesFileReader.class.getSimpleName());
			}
		}
		
		// We now have the final line to include in the final file.
		// Create a temp file.
		File temp;
		try {
			temp = File.createTempFile(origFile.getName(), "tmp");
		} catch (IOException e) {
			Log.e(fileWriteErrorTag, "could not create temp file");
			return false;
		}
		// Create writer for temp file and regular reader for original file.
		BufferedWriter tmpWriter = null;
		BufferedReader origReader = null;
		try {
			origReader = new BufferedReader(new FileReader(origFile));
			tmpWriter = new BufferedWriter(new FileWriter(temp));
		} catch (FileNotFoundException e) {
			Log.e(fileReadErrorTag, "could not create regular reader for original file");
			return false;
		} catch (IOException e) {
			Log.e(fileWriteErrorTag, "could not create Writer for temp file");
			if(origReader != null) {
				try {
					origReader.close();
				} catch (IOException e1) {
					Log.e(fileReadErrorTag, "error closing origReader");
				}
			}
			return false;
		}
		// Now ready to copy to temp file.
		try {
			line = null;
			while((line = origReader.readLine()) != null) {
				// Write current line to temp file.
				tmpWriter.write(line + newLine);
				if(line.equals(finalLine)) {
					// Found final line to include.
					break;
				}
			}
		} catch(IOException ioe) {
			Log.e(fileWriteErrorTag, "error when copying original file to temp file");
			return false;
		} finally {
			try {
				if(tmpWriter != null) {
					tmpWriter.flush();
					tmpWriter.close();
				}
				if(origReader != null) {
					origReader.close();
				}
			} catch (IOException e) {
				Log.e(fileWriteErrorTag, "error when cleaning up after copying to temp file");
			}
		}
		BufferedWriter origWriter = null;
		BufferedReader tmpReader = null;
		try {
			// Create writer that overwrites content of original file.
			origWriter = new BufferedWriter(new FileWriter(origFile, false));
			// Create reader for temp file.
			tmpReader = new BufferedReader(new FileReader(temp));
			line = null;
			int lineCountNoNoise = 0;
			while((line = tmpReader.readLine()) != null) {
				origWriter.write(line + newLine);
				lineCountNoNoise++;
			}
			Log.d("file length", "File length after removing noise: " + lineCountNoNoise + " lines.");
		} catch(IOException ioe) {
			Log.e(fileWriteErrorTag, "error when copying temp file data to original file");
			return false;
		} finally {
			try {
				if(origWriter != null) {
					origWriter.flush();
					origWriter.close();
				}
				if(tmpReader != null) {
					tmpReader.close();
				}
			} catch(IOException ioe) {
				Log.e(fileWriteErrorTag, "error when cleaning up after copying temp file data to original file");
			}
		}
		// Remove temporary file.
		temp.delete();
		// Noise was successfully removed and the original file was updated.
		return true;
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
//			 lineCountWithNoise += this.batch.size();
			 this.writeBatchToFile();
		 }
	}
	
	private void writeBatchToFile() {
		// Get batch to write.
		final List<SensorEvent> tmp = this.batch;
		// Reset list for new readings.
		this.batch = new ArrayList<>();
		// Create runnable with write task.
		RecordingBatchWriter writer = new RecordingBatchWriter(this.currentFile, tmp, batchCount);
		// Post work.
		this.fileWriterThread.handler.post(writer);
		// Increment batch count.
		batchCount++;
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
							writer.write("timestamp;x;y;z;label" + newLine);
							MovementRecorderService.this.lineCountWithNoise++;
						}
						else {
							// File should exist, headers should already be present.
							// Init writer with append = true.
							writer = (new FileWriter(file, true));
						}
						for(SensorEvent evt : this.batch) {
							writer.write(evt.timestamp + ";" + evt.values[0] + ";" + evt.values[1] + ";" + evt.values[2] + ";" + MovementRecorderService.this.recordingType + newLine);
							MovementRecorderService.this.lineCountWithNoise++;
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
