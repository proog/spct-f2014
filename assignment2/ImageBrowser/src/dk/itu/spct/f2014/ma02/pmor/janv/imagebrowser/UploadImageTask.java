package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class UploadImageTask extends AsyncTask<File, Void, String> {
	private String host;
	
	public UploadImageTask(String host) {
		this.host = host;
	}
	
	@Override
	protected String doInBackground(File... files) {
		File f = files[0];
		String attachmentName = f.getName();
		String attachmentFileName = f.getName();
		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		
		try {
			// set up connection and headers
			URL url = new URL(host);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDoOutput(true);
	
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
			httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	
			DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
	
			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\";filename=\"" + attachmentFileName + "\"" + crlf);
			request.writeBytes(crlf);
			
			// read from file, write to request
			FileInputStream input = new FileInputStream(f);
			byte[] buffer = new byte[4096];
			int n = 0;
			while((n = input.read(buffer)) != -1) {
				request.write(buffer, 0, n);
			}
			input.close();
			
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
	
			request.flush();
			request.close();
	
			// receive response
			InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
			BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
			String line = "";
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = responseStreamReader.readLine()) != null) {
			    stringBuilder.append(line).append("\n");
			}
			responseStreamReader.close();
	
			String response = stringBuilder.toString();
			
			responseStream.close();
			httpUrlConnection.disconnect();
			
			return response;
		}
		catch(IOException e) {
			
		}
		
		return "Upload failed";
	}

}
