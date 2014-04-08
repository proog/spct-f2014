package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	
	@Override
	protected void onDestroy() {
		if(client != null)
			client.disconnectSignalR();
		
		super.onDestroy();
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
	
	public void connectButtonClicked(final View view) {
		EditText ipBox = (EditText) findViewById(R.id.editTextServerIp);
		EditText tagBox = (EditText) findViewById(R.id.editTextTagId);
		client = new NetworkClient(ipBox.getText().toString(), Byte.parseByte(tagBox.getText().toString()));
		client.connectSignalR();
		
		Button connectButton = (Button) view;
		connectButton.setText("Disconnect");
		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnectButtonClicked(view);
			}
		});
	}
	
	public void disconnectButtonClicked(final View view) {
		client.disconnectSignalR();
		
		Button b = (Button) view;
		b.setText("Connect");
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				connectButtonClicked(view);
			}
		});
	}
}
