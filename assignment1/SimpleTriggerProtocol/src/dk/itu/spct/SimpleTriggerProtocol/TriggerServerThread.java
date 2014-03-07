package dk.itu.spct.SimpleTriggerProtocol;

import java.io.*;
import java.net.*;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.TriggerMessage;

public class TriggerServerThread extends Thread {
	private Socket socket;
	private TriggerServer callback;
	private BufferedReader reader;
	
	public TriggerServerThread(Socket socket, TriggerServer callback) {
		this.socket = socket;
		this.callback = callback;
	}
	
	/**
	 * Read a message from the input stream and send it back to the callback TriggerServer.
	 */
	private void readMessage() {
		StringBuilder sb = new StringBuilder();
		String line = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			while((line = reader.readLine()) != null)
				sb.append(line);
			
			reader.close();
			socket.close();
			
			// tell the TriggerServer a message has been received
			if(callback != null)
				callback.messageReceived(TriggerMessage.fromJson(sb.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// start reading an incoming message
		readMessage();
	}
}
