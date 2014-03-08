package dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.webservice;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model.BLIPDevice;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model.IBLIPDeviceDataContract;

/**
 * Interface specifying methods for querying the BLIP web service for device
 * information. Can be used for dependency injection in BLIP client code.
 * 
 * @param <DEVICE_MODEL>
 *            A user defined model class that wraps the BLIP device information
 *            that is returned when querying the BLIP web service for
 *            information of a particular device.
 * @author Janus Varmarken
 * 
 */
public interface IBLIPDeviceUpdateProvider<DEVICE_MODEL extends IBLIPDeviceDataContract> {

	/**
	 * Query the BLIP system asking if it currently contains a device with an ID
	 * equal to the given {@code deviceId}.
	 * 
	 * @param deviceId
	 *            The ID of the device to search for.
	 * @return A {@link BLIPDevice} containing device information if the device
	 *         is found in the BLIP system. {@code null} if the device was not
	 *         found in the BLIP system.
	 */
	DEVICE_MODEL getDeviceUpdate(String deviceId);
}
