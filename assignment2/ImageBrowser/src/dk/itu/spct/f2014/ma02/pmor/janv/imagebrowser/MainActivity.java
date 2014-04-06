package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

public class MainActivity extends Activity {
	private ImageAdapter imageAdapter;
	private NetworkClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		reloadImages();
	}
	
	private void reloadImages() {
		this.imageAdapter = new ImageAdapter(this);
		GridView gv = (GridView) this.findViewById(R.id.gridView1);
		gv.setAdapter(this.imageAdapter);
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
			File imgFolder = new File(extFolder + "/" + "assignment2images");
			
			File[] files = imgFolder.listFiles();
			for(File f : files) {
				displayImage(f);
			}
		}
	}
	
	private void displayImage(File f) {
		imageAdapter.imageList.add(Uri.fromFile(f));
	}
	
	public void reloadButtonClicked(View view) {
		reloadImages();
	}
	
	public void connectButtonClicked() {
		EditText ipBox = (EditText) findViewById(R.id.editTextServerIp);
		client = new NetworkClient(ipBox.getText().toString());
		client.connectSignalR();
	}
}
