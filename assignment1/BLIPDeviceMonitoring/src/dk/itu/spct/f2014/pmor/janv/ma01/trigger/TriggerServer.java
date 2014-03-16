package dk.itu.spct.f2014.pmor.janv.ma01.trigger;

import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.*;

import dk.itu.spct.f2014.pmor.janv.ma01.blip.webservice.client.BLIPClient;
import dk.itu.spct.f2014.pmor.janv.ma01.context.monitoring.BLIPDeviceMonitor;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.TriggerMessage;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.context.BLIPDeviceEntity;

public class TriggerServer extends Thread {
	public static final int PORT = 3345;
	public static String contextServiceUri;
	
	/**
	 * The timeout for the accept call in the listening loop.
	 * This is done so the listen loop can periodically check if a server stop has been requested.
	 */
	private static final int timeout = 2000;
	
	private boolean stop = false;
	private ServerSocket serverSocket;
	private List<MessageReceivedObserver> observers;
	private HashMap<String, BLIPDeviceMonitor> monitors;
	
	/**
	 * Constructor for TriggerServer on port <pre>PORT</pre>.
	 * @throws IOException if a server socket could not be created.
	 */
	public TriggerServer() throws IOException {
		this(PORT);
	}
	
	/**
	 * Constructor for TriggerServer on a given port <pre>port</pre>.
	 * @param port The port on which to listen for incoming connections.
	 * @throws IOException if a server socket could not be created.
	 */
	public TriggerServer(int port) throws IOException {
		serverSocket = new ServerSocket(PORT);
		serverSocket.setSoTimeout(timeout);
		observers = new ArrayList<>();
		monitors = new HashMap<>();
	}
	
	/**
	 * Makes the server start listening for incoming connections until <pre>stop()</pre> is called.
	 */
	public void startServer() {
		while(!stop) {
			try {
				// wait for client to connect
				Socket socket = serverSocket.accept();
				
				// start reading the message asynchronously from the client
				Thread t = new TriggerServerThread(socket, this);
				t.start();
			} catch(SocketTimeoutException ste) {
				// we end up here if the accept timeout has elapsed
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			serverSocket.close();
			System.out.println("Server closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Signals the server to stop its listening loop.
	 * May take up to <pre>timeout</pre> milliseconds to take effect.
	 */
	public void stopServer() {
		stop = true;
	}
	
	public void addObserver(MessageReceivedObserver o) {
		observers.add(o);
	}
	
	public void removeObserver(MessageReceivedObserver o) {
		observers.remove(o);
	}
	
	/**
	 * Broadcasts a received message to all observers in <pre>observers</pre>.
	 * Called from each client communication thread spawned by this instance.
	 * @param m The message
	 */
	public synchronized void messageReceived(TriggerMessage m) {
		for(MessageReceivedObserver o : observers)
			o.messageReceived(m);
		
		if(m.getAction().equalsIgnoreCase(TriggerMessage.startAction)) {
			addMonitor(m);
		}
		else if(m.getAction().equalsIgnoreCase(TriggerMessage.stopAction)) {
			String deviceId = m.getDeviceId();
			BLIPDeviceMonitor monitor = monitors.get(deviceId);
			monitor.stopMonitoring();
			monitors.remove(deviceId);
		}
	}
	
	@Override
	public void run() {
		startServer();
	}
	
	private void addMonitor(TriggerMessage m) {
		try {
			BLIPClient client = new BLIPClient(BLIPClient.DEFAULT_BASE_URL);
			BLIPDeviceEntity entity = new BLIPDeviceEntity(m.getDeviceId(), m.getName());
			BLIPDeviceMonitor monitor = new BLIPDeviceMonitor(contextServiceUri, client, entity);
			Thread t = new Thread(monitor);
			monitors.put(m.getDeviceId(), monitor);
			t.start();
		} catch (RemoteException e) {
			System.out.println("Could not add a new monitor.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		try {
			/*TriggerServer ts = new TriggerServer();
			ts.addObserver(new MessagePrinter());
			ts.start();
			Thread.sleep(5000);
			ts.stopServer();*/
			if(args.length != 1) {
				System.out.println("Usage: java TriggerServer contextServiceUri");
				System.exit(1);
			}
			
			contextServiceUri = args[0];
			new TriggerServer().startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
