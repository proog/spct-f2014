package dk.itu.spct.f2014.ma01.pmor.janv.androidapp.network;

/**
 * Utility class for creating JSON messages (using Gson) that are targeted at
 * the TriggerServer.
 * 
 * @author Janus Varmarken
 * 
 */
public class TriggerServerMessage {

	public static final String ACTION_START = "start";
	
	public static final String ACTION_STOP = "stop";
	
	private final String action;

	private final String name;

	private final String deviceId;

	public TriggerServerMessage(String action, String name, String deviceId) {
		this.action = action;
		this.name = name;
		this.deviceId = deviceId;
	}

	public String getAction() {
		return this.action;
	}

	public String getName() {
		return this.name;
	}

	public String getDeviceId() {
		return this.deviceId;
	}
}
