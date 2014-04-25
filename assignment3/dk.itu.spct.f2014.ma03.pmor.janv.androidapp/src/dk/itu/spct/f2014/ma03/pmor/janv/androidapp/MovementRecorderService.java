package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class MovementRecorderService extends Service {

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
	 * Thread responsible for writing readings to a file.
	 */
	private FileWriterThread fileWriterThread;

	@Override
	public void onCreate() {
		super.onCreate();
		/*
		 * We use the same FileWriterThread during the entire lifetime of the
		 * service.
		 */
		this.fileWriterThread = new FileWriterThread();
		this.fileWriterThread.start();
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
			if (MovementRecorderService.this.recording) {
				return;
			}
			// TODO start sensor.
		}

		/**
		 * Stop any current recording.
		 */
		public void stopRecording() {
			MovementRecorderService.this.recording = false;
			// Stop recording by interrupting the RecorderThread.
			MovementRecorderService.this.recorderThread.interrupt();
		}
	}

	private class RecorderThread extends Thread {
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
}
