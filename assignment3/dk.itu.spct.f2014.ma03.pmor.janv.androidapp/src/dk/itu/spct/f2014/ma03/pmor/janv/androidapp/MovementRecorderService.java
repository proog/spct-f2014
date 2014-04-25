package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MovementRecorderService extends Service {

	private final MovementRecorderServiceBinding binding = new MovementRecorderServiceBinding();
	
	// TODO overwrite onStartCommand!!
	
	@Override
	public IBinder onBind(Intent intent) {
		return this.binding;
	}
	
	public class MovementRecorderServiceBinding extends Binder {
		
		public void startRecording(RecordingType recordingType) {
			// TODO start sensor.
		}
		
		public void stopRecording() {
			// TODO stop sensor.
		}
	}
}
