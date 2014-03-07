package dk.itu.spct.SimpleTriggerProtocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.TriggerMessage;

public class TriggerClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TriggerMessage m = new TriggerMessage("start", "bt-o32042-sdfsjeoq", "hillemænd");
			Socket socket = new Socket("localhost", TriggerServer.PORT);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(m.toJson());
			writer.flush();
			writer.close();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
