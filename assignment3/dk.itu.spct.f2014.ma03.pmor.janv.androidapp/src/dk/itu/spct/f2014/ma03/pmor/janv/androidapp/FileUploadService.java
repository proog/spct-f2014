package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.io.File;

import android.app.IntentService;
import android.content.Intent;

public class FileUploadService extends IntentService {

	public static final String EXTRA_FILE_ABS_PATH = "extra_file_abs_path";
	public static final String EXTRA_TRAINING_MODE = "extra_training_mode";
	
	public FileUploadService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String filePath = intent.getStringExtra(EXTRA_FILE_ABS_PATH);
		File file = new File(filePath);
		boolean training = intent.getBooleanExtra(EXTRA_TRAINING_MODE, false);
		
	}

}
