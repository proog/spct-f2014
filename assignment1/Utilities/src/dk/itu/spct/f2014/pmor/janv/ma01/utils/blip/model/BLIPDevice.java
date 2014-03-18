package dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model;

import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

/**
 * A class used to wrap the JSON that is returned by the BLIP REST web service
 * when querying it for information about a particular terminal (device).
 * 
 * @author Janus Varmarken
 * 
 */
public class BLIPDevice implements IBLIPDeviceDataContract {

	@SerializedName("last-event-description")
	private String lastEventDescription;

	@SerializedName("last-event-timestamp")
	private long lastEventTimestamp;

	private String location;

	@SerializedName("major-class-of-device")
	private String majorClassOfDevice;

	@SerializedName("terminal-id")
	private String terminalId;

	/**
	 * Utility factory method to create a {@code BLIPDevice} from a JSON string.
	 * 
	 * @param json
	 *            A JSON string containing data that can be decoded to a
	 *            {@link BLIPDevice}.
	 * @return The {@link BLIPDevice} created from the provided JSON or
	 *         {@code null} if the provided JSON cannot be parsed to an instance
	 *         of {@link BLIPDevice}.
	 * @throws NullPointerException
	 *             if {@code json} is {@code null}.
	 */
	public static BLIPDevice createFromJson(String json) {
		/*
		 * TODO: It seems that JsonSyntaxException is only raised if the
		 * provided JSON is not valid JSON syntax. However, the
		 * Gson.fromJson(String, Class<T>) doc stipulates that this exception
		 * will also be raised if the provided JSON is valid JSON but doesn't
		 * match an instance of the class parameter. It seems that a string
		 * parameter containing valid JSON that however does not match the class
		 * parameter with regards to instance creation results in instances of
		 * the class parameter that have their instance fields set to the
		 * default values. Needs more thorough testing.
		 */
		try {
			JsonElement elem = new JsonParser().parse(json);
			if(!elem.isJsonObject() || elem.getAsJsonObject().get("terminal-id") == null)
				return null;
			
			return new Gson().fromJson(Objects.requireNonNull(json),
					BLIPDevice.class);
		}
		catch(JsonSyntaxException jse) {
			System.err.println("JSON error: " + jse.getMessage());
			jse.printStackTrace();
			return null;
		}
	}

	@Override
	public String getLastEventDescription() {
		return lastEventDescription;
	}

	public void setLastEventDescription(String lastEventDescription) {
		this.lastEventDescription = lastEventDescription;
	}

	@Override
	public long getLastEventTimestamp() {
		return lastEventTimestamp;
	}

	public void setLastEventTimestamp(long lastEventTimestamp) {
		this.lastEventTimestamp = lastEventTimestamp;
	}

	@Override
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String getMajorClassOfDevice() {
		return majorClassOfDevice;
	}

	public void setMajorClassOfDevice(String majorClassOfDevice) {
		this.majorClassOfDevice = majorClassOfDevice;
	}

	@Override
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
