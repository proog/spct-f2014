package dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class TriggerServerClient extends AsyncTask<String, Void, Void> {
	public static final String TRIGGER_SERVER_HOST_ADDRESS = "172.20.10.3";
	public static final String TRIGGER_SERVER_PORT = "3345";

	@Override
	protected Void doInBackground(String... params) {
		String host = params[0];
		String port = params[1];
		String msg = params[2];
		try {
			Socket s = new Socket(host, Integer.parseInt(port));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write(msg);
			writer.flush();
			writer.close();
			s.getOutputStream().close();
			s.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
