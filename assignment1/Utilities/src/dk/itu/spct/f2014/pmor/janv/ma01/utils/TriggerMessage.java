package dk.itu.spct.f2014.pmor.janv.ma01.utils;

import com.google.gson.*;

public class TriggerMessage {
	public static final String actionSpecifier = "action";
	public static final String deviceIdSpecifier = "deviceId";
	public static final String nameSpecifier = "name";
	
	public static final String startAction = "start";
	public static final String stopAction = "stop";
	
	private static Gson gson = new Gson();
	
	private String action = "";
	private String deviceId = "";
	private String name = "";
	
	public TriggerMessage() { }
	
	public TriggerMessage(String action, String deviceId, String name) {
		setAction(action);
		setDeviceId(deviceId);
		setName(name);
	}
	
	public static TriggerMessage fromJson(String json) {
		return gson.fromJson(json, TriggerMessage.class);
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	@Override
	public String toString() {
		return toJson();
	}
	
}
