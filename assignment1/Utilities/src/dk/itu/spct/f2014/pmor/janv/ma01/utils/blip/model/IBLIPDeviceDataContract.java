package dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model;

/**
 * Interface specifying the data contract that any class that models a device in
 * the BLIP system must obey to.
 * 
 * @author Janus Varmarken
 * 
 */
public interface IBLIPDeviceDataContract {

	/**
	 * Get a description of the last event.
	 * 
	 * @return A description of the last event.
	 */
	String getLastEventDescription();

	/**
	 * Get a time stamp for occurrence of the last event.
	 * 
	 * @return A time stamp for the occurrence the last event.
	 */
	long getLastEventTimestamp();

	/**
	 * Get the device location.
	 * 
	 * @return The device location.
	 */
	String getLocation();

	/**
	 * Get the class of the device.
	 * 
	 * @return The class of the device.
	 */
	String getMajorClassOfDevice();

	/**
	 * Get the device (terminal) ID.
	 * 
	 * @return The device (terminal) ID.
	 */
	String getTerminalId();
}
