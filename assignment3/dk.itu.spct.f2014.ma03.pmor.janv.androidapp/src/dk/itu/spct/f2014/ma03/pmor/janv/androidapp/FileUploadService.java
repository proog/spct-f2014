package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.io.File;
import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class FileUploadService extends IntentService {

	public static final String EXTRA_FILE_ABS_PATH = "extra_file_abs_path";
	public static final String EXTRA_TRAINING_MODE = "extra_training_mode";
	
	public FileUploadService() {
		super(FileUploadService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String filePath = intent.getStringExtra(EXTRA_FILE_ABS_PATH);
		File file = new File(filePath);
		Log.d(this.getClass().getSimpleName(), "in onHandleIntent invoked with file: " + file.getName());
		boolean training = intent.getBooleanExtra(EXTRA_TRAINING_MODE, false);
		try {
			DataUploader.postData(file, training);
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "failed uploading files due to IOException");
		}
	}
}
