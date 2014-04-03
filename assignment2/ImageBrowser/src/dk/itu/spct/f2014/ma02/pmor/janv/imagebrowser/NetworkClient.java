package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class NetworkClient {
	public String host;
	
	public NetworkClient(String host) {
		this.host = host;
		
		Platform.loadPlatformComponent(new AndroidPlatformComponent());
		HubConnection con = new HubConnection(host);
		HubProxy hub = con.createHubProxy("pmor_janv");
		
		hub.subscribe(this);
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
	}
	
	/**
	 * Upload a file to the web server.
	 * @param f The file to upload
	 */
	public void UploadImage(File f) {
		UploadImageTask uit = new UploadImageTask(host);
		uit.execute(f);
	}
}
