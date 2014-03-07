package dk.itu.spct.SimpleTriggerProtocol;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.TriggerMessage;

public class TriggerServer extends Thread {
	public static final int PORT = 3345;
	
	/**
	 * The timeout for the accept call in the listening loop.
	 * This is done so the listen loop can periodically check if a server stop has been requested.
	 */
	private static final int timeout = 2000;
	
	private boolean stop = false;
	private ServerSocket serverSocket;
	private List<MessageReceivedObserver> observers;
	
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
	}
	
	@Override
	public void run() {
		startServer();
	}

	public static void main(String[] args) throws InterruptedException {
		try {
			TriggerServer ts = new TriggerServer();
			ts.addObserver(new MessagePrinter());
			ts.start();
			Thread.sleep(5000);
			ts.stopServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
