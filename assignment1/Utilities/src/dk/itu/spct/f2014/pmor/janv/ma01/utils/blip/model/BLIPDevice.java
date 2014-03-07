package dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model;

import com.google.gson.annotations.SerializedName;

/**
 * A class used to wrap the {@code JSON} returned by the BLIP {@code REST} web
 * service.
 * 
 * @author Janus Varmarken
 * 
 */
public class BLIPDevice {

	@SerializedName("last-event-description")
	private String lastEventDescription;

	@SerializedName("last-event-timestamp")
	private long lastEventTimestamp;

	private String location;

	@SerializedName("major-class-of-device")
	private String majorClassOfDevice;

	@SerializedName("terminal-id")
	private String terminalId;

	public String getLastEventDescription() {
		return lastEventDescription;
	}

	public void setLastEventDescription(String lastEventDescription) {
		this.lastEventDescription = lastEventDescription;
	}

	public long getLastEventTimestamp() {
		return lastEventTimestamp;
	}

	public void setLastEventTimestamp(long lastEventTimestamp) {
		this.lastEventTimestamp = lastEventTimestamp;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMajorClassOfDevice() {
		return majorClassOfDevice;
	}

	public void setMajorClassOfDevice(String majorClassOfDevice) {
		this.majorClassOfDevice = majorClassOfDevice;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	@Override
	public String toString() {
		return String
				.format("lastEventDescription = '%s', lastEventTimestamp='%d', location='%s', majorClassOfDevice='%s', terminal-id='%s'",
						lastEventDescription, lastEventTimestamp, location,
						majorClassOfDevice, terminalId);
	}
}
