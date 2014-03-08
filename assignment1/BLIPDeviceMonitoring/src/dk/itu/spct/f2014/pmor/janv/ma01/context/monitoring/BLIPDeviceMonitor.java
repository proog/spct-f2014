package dk.itu.spct.f2014.pmor.janv.ma01.context.monitoring;

import java.rmi.RemoteException;
import java.util.Objects;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model.IBLIPDeviceDataContract;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.webservice.IBLIPDeviceUpdateProvider;
import dk.pervasive.jcaf.util.AbstractMonitor;

public class BLIPDeviceMonitor<DEVICE_MODEL extends IBLIPDeviceDataContract>
		extends AbstractMonitor {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -1318891287337945814L;

	/**
	 * Provides an interface to access the BLIP web service.
	 */
	private final IBLIPDeviceUpdateProvider<DEVICE_MODEL> blipService;
	
	public BLIPDeviceMonitor(String service_uri,
			IBLIPDeviceUpdateProvider<DEVICE_MODEL> blipService)
			throws RemoteException {
		super(service_uri);
		this.blipService = Objects.requireNonNull(blipService);
	}

	@Override
	public void monitor(String arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
