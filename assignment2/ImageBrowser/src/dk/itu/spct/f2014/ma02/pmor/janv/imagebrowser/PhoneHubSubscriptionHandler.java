package dk.itu.spct.f2014.ma02.pmor.janv.imagebrowser;

import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;

public class PhoneHubSubscriptionHandler implements SubscriptionHandler {
	@Override
	public void run() { }
	
	public void DownloadImage(String url) {
		
	}
	
	public void UploadImage(String filename) {
		
	}
	
	public void SendTagId(byte tagValue) {
		// shouldn't be called
	}
}
