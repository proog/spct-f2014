package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class NetworkClient {
	public String serverIp;
	private int restPort = 9000;
	private int signalrPort = 9001;
	
	public NetworkClient(String ip) {
		this.serverIp = ip;
	}
	
	public void connectSignalR(byte tag) {
		Platform.loadPlatformComponent(new AndroidPlatformComponent());
		HubConnection con = new HubConnection("http://" + serverIp + ":" + signalrPort + "/signalr");
		HubProxy hub = con.createHubProxy("PhoneHub");
		hub.subscribe(new PhoneHubSubscriptionHandler());
		hub.invoke("SendTagId", tag);
	}
}
