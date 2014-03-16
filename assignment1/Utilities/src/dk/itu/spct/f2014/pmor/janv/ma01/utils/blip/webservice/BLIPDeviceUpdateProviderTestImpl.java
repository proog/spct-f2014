package dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.webservice;

import java.io.IOException;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model.BLIPDevice;

/**
 * An implementation of {@link IBLIPDeviceUpdateProvider} that clients of
 * IBLIPDeviceUpdateProvider can use for testing. This is an example of how the
 * used dependency injection pattern enhances testability.
 * 
 * @author Janus Varmarken
 * 
 */
public class BLIPDeviceUpdateProviderTestImpl implements
		IBLIPDeviceUpdateProvider<BLIPDevice> {

	private final BLIPDevice device = new BLIPDevice();

	private final String[] testLocations = new String[]{ "itu.zone1", "itu.zone2", "itu.zone3", "itu.zone4", "LOCATION_UNKNOWN" };
	
	private int count = 0;
	
	public BLIPDeviceUpdateProviderTestImpl() {
		this.device.setTerminalId("123456789123");
		this.device.setLastEventDescription("test");
		this.device.setLastEventTimestamp(42L);
		this.device.setMajorClassOfDevice("artificial");
		
	}

	@Override
	public BLIPDevice getDeviceUpdate(String deviceId) throws IOException {
		this.device.setLocation(this.testLocations[count]);
		count = ++count % testLocations.length; // cycle back to first location
		return this.device;
	}
}
