package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;

public class PhoneHubSubscriptionHandler implements SubscriptionHandler {
	@Override
	public void run() { }
	
	public void DownloadImageToPhone(String url, String filename) {
		String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
		String imgFolder = extFolder + "/" + "assignment2images";
		File downloadFile = new File(imgFolder + "/" + filename);
		
		try {
			RestHandler.downloadImage(new URL(url), downloadFile);
		} catch (MalformedURLException e) {
			Log.e("", "DownloadImageToPhone: " + e.getLocalizedMessage());
		} catch (IOException e) {
			Log.e("", "DownloadImageToPhone: " + e.getLocalizedMessage());
		}
	}
	
	public void UploadImageToServer(String postUrl, String filename) {
		String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
		String imgFolder = extFolder + "/" + "assignment2images";
		File uploadFile = new File(imgFolder + "/" + filename);
		
		try {
			RestHandler.UploadImage(new URL(postUrl), uploadFile);
		} catch (MalformedURLException e) {
			Log.e("", "UploadImageToPhone: " + e.getLocalizedMessage());
		}
	}
}
