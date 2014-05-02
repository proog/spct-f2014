package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataUploader {
	private static String host = "http://localhost:8888/movementservlet"; //"http://pmorjanv.appspot.com/movementservlet";
	
	public static void postData(String data, String id, boolean trainingMode) throws IOException {
		StringBuilder post = new StringBuilder(data.length() + 50);
		post.append("mode=").append(trainingMode ? "training" : "test").append("&id").append(id).append("&data=").append(data);
		
		URL url = new URL(host);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("charset", "utf-8");
		con.setRequestProperty("Content-Length", Integer.toString(post.length()));
		con.connect();
		DataOutputStream dos = new DataOutputStream(con.getOutputStream());
		dos.writeBytes(post.toString());
		dos.flush();
		dos.close();
		con.getInputStream().close();
		con.disconnect();
	}
}
