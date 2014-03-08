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
	
	/**
	 * Provides a utility method to easily fire a REST request.
	 */
	private final RESTRequest requester;

	public BLIPClient(String baseURL) {
		this.requester = new RESTRequest(Objects.requireNonNull(baseURL));
	}

	@Override
	public BLIPDevice getDeviceUpdate(String deviceId) throws IOException {
		String json = requester
				.performRequest(Objects.requireNonNull(deviceId));
		return json != null ? BLIPDevice.createFromJson(json) : null;
	}
}
