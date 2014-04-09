package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RestHandler {
	public static int restPort = 9000;
	
	private static final RestHandler singleton = new RestHandler();
	
	private final List<IRestHandlerObserver> observers = new ArrayList<>();
	
	/**
	 * Private constructor due to singleton pattern.
	 */
	private RestHandler() {
		
	}
	
	public static RestHandler getInstance() {
		return singleton;
	}
	
	public void addObserver(IRestHandlerObserver obs) {
		this.observers.add(obs);
	}
	
	public void removeObserver(IRestHandlerObserver obs) {
		this.observers.remove(obs);
	}
	
	/**
	 * Called by the SignalR server when this phone should download an image.
	 * @param url The URL of the image to download from the web server.
	 * @param f The file in which the data will be downloaded
	 * @throws IOException If the connection could not be established.
	 */
	public void downloadImage(URL url, File f) throws IOException {
		InputStream input = url.openStream();
		FileOutputStream output = new FileOutputStream(f);
		
		int n = 0;
		byte[] buffer = new byte[4096];
		while((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
		}
		
		output.flush();
		output.close();
		input.close();
		for(IRestHandlerObserver obs : this.observers) {
			obs.onDownloadImageDone(f);
		}
	}
	
	/**
	 * Upload a file to the web server.
	 * @param url The URL to post the request to
	 * @param f The file to upload
	 */
	public static void UploadImage(URL url, File f) {
		UploadImageTask uit = new UploadImageTask(url.toString());
		uit.execute(f);
	}
}
