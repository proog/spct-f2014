package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class NetworkClient {
	public static String serverIp;
	private int signalrPort = 9001;
	private HubConnection con;
	private HubProxy hub;
	private byte tag;
	
	public NetworkClient(String ip, byte tag) {
		NetworkClient.serverIp = ip;
		this.tag = tag;
	}
	
	public void connectSignalR() {
		Platform.loadPlatformComponent(new AndroidPlatformComponent());
		con = new HubConnection("http://" + serverIp + ":" + signalrPort + "/signalr");
		hub = con.createHubProxy("PhoneHub");
		hub.subscribe(new PhoneHubSubscriptionHandler());
		try {
			con.start().get();
			hub.invoke("SendTagId", tag).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnectSignalR() {
		hub.invoke("SendDisconnectSignal", tag);
		con.stop();
	}
}
