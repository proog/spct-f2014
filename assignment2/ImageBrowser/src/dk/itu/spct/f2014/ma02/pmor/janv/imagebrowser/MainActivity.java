package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.os.Build;
import android.provider.MediaStore.Files;

public class MainActivity extends Activity {
	private ImageAdapter imageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			
		}
		
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
}
