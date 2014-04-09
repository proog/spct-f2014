package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

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
			RestHandler.getInstance().downloadImage(new URL("http://" + NetworkClient.serverIp + ":" + RestHandler.restPort + url), downloadFile);
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
			RestHandler.UploadImage(new URL("http://" + NetworkClient.serverIp + ":" + RestHandler.restPort + postUrl), uploadFile);
		} catch (MalformedURLException e) {
			Log.e("", "UploadImageToPhone: " + e.getLocalizedMessage());
		}
	}
	
	public void UploadAllImagesToServer(String postUrl) {
		String extFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
		String imgFolder = extFolder + "/" + "assignment2images";
		
		File[] files = new File(imgFolder).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return Pattern.matches(".*\\.(jpg|jpeg|png)", filename);
			}
		});
		
		URL url = null;
		try {
			url = new URL("http://" + NetworkClient.serverIp + ":" + RestHandler.restPort + postUrl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		for(File f : files) {
			File uploadFile = new File(imgFolder + "/" + f.getName());
			RestHandler.UploadImage(url, uploadFile);
		}
	}
}
