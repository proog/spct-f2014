package dk.itu.spct.f2014.pmor.janv.ma01.blip.webservice.client;

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

	@Override
	public BLIPDevice getDeviceUpdate(String deviceId) {
		return null;
	}
}
