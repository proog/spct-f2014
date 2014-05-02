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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class DataUploader {
	private static String host = "http://pmorjanv.appspot.com/movementservlet";
	
	public static void postData(File file, boolean trainingMode) throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(host);
		
		StringBuilder data = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null)
			data.append(line + "\n");
		reader.close();
		
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("mode", trainingMode ? "training" : "test"));
		pairs.add(new BasicNameValuePair("id", file.getName()));
		pairs.add(new BasicNameValuePair("data", data.toString()));
		
		httpPost.setEntity(new UrlEncodedFormEntity(pairs));
		client.execute(httpPost);
	}
}
