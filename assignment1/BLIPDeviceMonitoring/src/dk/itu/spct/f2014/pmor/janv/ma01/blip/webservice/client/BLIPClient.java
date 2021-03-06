package dk.itu.spct.f2014.pmor.janv.ma01.blip.webservice.client;

import java.io.IOException;
import java.util.Objects;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.RESTRequest;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model.BLIPDevice;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.webservice.IBLIPDeviceUpdateProvider;

/**
 * A concrete {@link IBLIPDeviceUpdateProvider} that queries the BLIP web
 * service for device information and returns this information wrapped in a
 * {@link BLIPDevice}.
 * 
 * @author Janus Varmarken
 * 
 */
public class BLIPClient implements IBLIPDeviceUpdateProvider<BLIPDevice> {
	public static final String DEFAULT_BASE_URL = "http://pit.itu.dk:7331/location-of/";

	/**
	 * Provides a utility method to easily fire a REST request.
	 */
	private final RESTRequest requester;

	public BLIPClient(String baseURL) {
		this.requester = new RESTRequest(Objects.requireNonNull(baseURL));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return A {@link BLIPDevice} containing the device data or {@code null}
	 *         if the device was not found in the BLIP system.
	 */
	@Override
	public BLIPDevice getDeviceUpdate(String deviceId) throws IOException {
		String json = requester
				.performRequest(Objects.requireNonNull(deviceId));
		return json != null ? BLIPDevice.createFromJson(json) : null;
	}
}
