package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataUploader {
	private static String host = "http://localhost:8888/movementservlet"; //"http://pmorjanv.appspot.com/movementservlet";
	
	public static void postData(File file, boolean trainingMode) throws IOException {
		String id = file.getName();
		StringBuilder post = new StringBuilder();
		post.append("mode=").append(trainingMode ? "training" : "test").append("&id").append(id).append("&data=");
		
		URL url = new URL(host);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("charset", "utf-8");
		con.setRequestProperty("Content-Length", Long.toString(post.length() + file.length()));
		con.connect();
		DataOutputStream dos = new DataOutputStream(con.getOutputStream());
		dos.writeBytes(post.toString());
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			dos.writeBytes(line);
		}
		reader.close();
		dos.flush();
		dos.close();
		con.getInputStream().close();
		con.disconnect();
	}
}
