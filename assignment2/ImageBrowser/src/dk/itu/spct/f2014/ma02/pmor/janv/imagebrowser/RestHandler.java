package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class RestHandler {
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
	}
	
	/**
	 * Upload a file to the web server.
	 * @param url The URL to post the request to
	 * @param f The file to upload
	 */
	public void UploadImage(URL url, File f) {
		UploadImageTask uit = new UploadImageTask(url.toString());
		uit.execute(f);
	}
}
