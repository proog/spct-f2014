package dk.itu.spct.f2014.pmor.janv.ma01.context.monitoring;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import dk.itu.spct.f2014.pmor.janv.ma01.blip.webservice.client.BLIPClient;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.model.IBLIPDeviceDataContract;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.blip.webservice.IBLIPDeviceUpdateProvider;
import dk.itu.spct.f2014.pmor.janv.ma01.utils.context.BLIPDeviceEntity;
import dk.pervasive.jcaf.ContextService;
import dk.pervasive.jcaf.Entity;
import dk.pervasive.jcaf.RemoteContextClient;
import dk.pervasive.jcaf.item.Location;
import dk.pervasive.jcaf.relationship.Located;
import dk.pervasive.jcaf.util.AbstractMonitor;

/**
 * <p>
 * Class that monitors an individual BLIP device and feeds the device location
 * data to a {@link ContextService}.
 * </p>
 * <p>
 * The {@code BLIPDeviceMonitor} uses an implementation of
 * {@link IBLIPDeviceUpdateProvider} to retrieve device data. This allows for
 * dependency injection: the implementation details of the
 * {@link IBLIPDeviceUpdateProvider} are abstracted away and hence it is free to
 * retrieve the data in any way it wants (e.g. one could implement an
 * {@link IBLIPDeviceUpdateProvider} that runs on the same system as the BLIP
 * system runs on and hence retrieves its data locally, or one could implement
 * an {@link IBLIPDeviceUpdateProvider} that queries the BLIP web service to
 * retrieve its data, see {@link BLIPClient} for an example of this).
 * </p>
 * 
 * @author Janus Varmarken
 */
public class BLIPDeviceMonitor extends AbstractMonitor {

	/**
	 * Used as location string when a device is not found (i.e. its entity
	 * representation should be removed).
	 */
	public static final String LOCATION_UNKNOWN = "LOCATION_UNKNOWN";

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -1318891287337945814L;

	/**
	 * Provides an interface to access the BLIP web service.
	 */
	private final IBLIPDeviceUpdateProvider<? extends IBLIPDeviceDataContract> blipService;

	/**
	 * ID of the device monitored by this {@code BLIPDeviceMonitor}.
	 */
	private final BLIPDeviceEntity entity;

	/**
	 * The time (in milliseconds) between each entity update sent from this
	 * monitor to its associated {@link ContextService}. TODO: this value should
	 * not be hard coded but rather read from configuration file.
	 */
	private final long updateInterval = 5000;

	/**
	 * Counter maintaining how many times the BLIP system has returned that it
	 * is not able to find the requested device. This counter should be reset
	 * when it reaches {@link #notFoundThreshold}.
	 */
	private int notFoundCount = 0;

	/**
	 * Threshold defining how many times the BLIP system should return
	 * "device not found" before this {@code BLIPDeviceMonitor} considers the
	 * device to have left the BLIP system. The intention of the threshold is to
	 * allow this {@code BLIPDeviceMonitor} to cope with the instability of the
	 * BLIP system: even stationary devices sometimes appear and disappear
	 * randomly in the BLIP system. TODO: this value should not be hard coded
	 * but rather read from a configuration file.
	 */
	private final int notFoundThreshold = 5;

	/**
	 * Specifies if this monitor is still active. Use {@link #locker} for
	 * synchronizing access.
	 */
	private boolean alive = true;

	/**
	 * Provides synchronization for fields that are concurrently accessed by
	 * more threads, e.g. {@link #alive}.
	 */
	private ReentrantReadWriteLock locker = new ReentrantReadWriteLock(true);

	public BLIPDeviceMonitor(
			String service_uri,
			IBLIPDeviceUpdateProvider<? extends IBLIPDeviceDataContract> blipService,
			BLIPDeviceEntity entity) throws RemoteException {
		super(service_uri);
		this.blipService = Objects.requireNonNull(blipService);
		this.entity = Objects.requireNonNull(entity);
		// Register self as monitor of location changes.
		this.getContextService().addContextClient(
				RemoteContextClient.TYPE_MONITOR, Location.class, this);
	}

	@Override
	public void monitor(String entityId) throws RemoteException {
		System.out.println("monitor() called");
		
		try {
			// Get device from BLIP system
			IBLIPDeviceDataContract device = this.blipService
					.getDeviceUpdate(this.entity.getId());
			if (device != null) {
				// Device found in BLIP system.
				this.onDeviceFound(device);
			} else {
				// Device not found in the BLIP system.
				/*
				 * If this is an explicit call to monitor the device, we remove
				 * the entity from the ContextService right away if it is not
				 * found. This is contrary to the scheduled automatic updates in
				 * run() where we allow the device to move in and out of the
				 * system (using a "not found counter" with a threshold) in
				 * order to compensate for the instability of the BLIP system.
				 */
				/*
				 * Fire context changed with unknown location to allow listeners
				 * to respond to the device being removed from the
				 * ContextService.
				 */
				this.addLocationContextItem(LOCATION_UNKNOWN);
				// Remove the entity from the ContextService
				this.removeEntity();
			}
		} catch (IOException e) {
			/*
			 * TODO: how to cope with error? Logging?
			 */
			System.err.println(String.format(
					"%s: %s occurred in monitor(String).", this.getClass()
							.getSimpleName(), e.getClass().getSimpleName()));
			e.printStackTrace();
		}
	}

	/**
	 * Stops this monitor so that it will no longer feed its associated
	 * {@link ContextService} with device information.
	 */
	public void stopMonitoring() {
		this.locker.writeLock().lock();
		this.alive = false;
		this.locker.writeLock().unlock();
	}

	@Override
	public void run() {
		// TODO add ability to call stop()
		while (this.isAlive()) {
			try {
				IBLIPDeviceDataContract device = this.blipService
						.getDeviceUpdate(this.entity.getId());
				if (device != null) {
					// Device found in BLIP system.
					this.onDeviceFound(device);
					notFoundCount = 0;
				} else {
					// Device not found in BLIP system.
					boolean remove = ++notFoundCount >= notFoundThreshold;
					if (remove) {
						/*
						 * Fire context changed with unknown location to allow
						 * listeners to respond to the device being removed from
						 * the ContextService.
						 */
						this.addLocationContextItem(LOCATION_UNKNOWN);
						// Remove the entity from the ContextService.
						this.removeEntity();
						// Reset counter.
						notFoundCount = 0;
						
						System.out.println("Blip monitor: removing entity");
					}
				}
			} catch (RemoteException e) {
				/*
				 * TODO: Handle error: logging?
				 */
				System.err
						.println(this.getClass().getSimpleName()
								+ ": "
								+ e.getClass().getSimpleName()
								+ " occurred in run() - could not remove Entity from ContextService.");
				e.printStackTrace();
			} catch (IOException e) {
				/*
				 * TODO: Handle error: logging?
				 */
				System.err
						.println(this.getClass().getSimpleName()
								+ ": "
								+ e.getClass().getSimpleName()
								+ " occurred in run() - could not fetch data from BLIP system.");
				e.printStackTrace();
			}
			
			try {
				// Sleep till next update
				Thread.sleep(this.updateInterval);
			} catch (InterruptedException e) {
				/*
				 * TODO: Handle error: logging? Also, is this thread interrupted
				 * if there is a remote call to monitor(String)?
				 */
				System.err.println(this.getClass().getSimpleName()
						+ " was interrupted in run()");
				e.printStackTrace();
			}
		}
		this.removeFromContextService();
	}

	/**
	 * Provides thread safe access to {@link #alive}.
	 * 
	 * @return {@code true} if this monitor is still alive, i.e. if it still
	 *         feeds its associated {@link ContextService} with device
	 *         information. {@code false} if this monitor is no longer alive.
	 */
	private boolean isAlive() {
		try {
			this.locker.readLock().lock();
			return this.alive;
		} finally {
			this.locker.readLock().unlock();
		}
	}

	/**
	 * Updates the bound {@link ContextService} with new context information for
	 * the {@link Entity} monitored by this {@code BLIPDeviceMonitor}.
	 * 
	 * @param device
	 *            Device data containing context information.
	 */
	private void onDeviceFound(IBLIPDeviceDataContract device) {
		assert this.entity.getId().equals(device.getTerminalId());
		Entity entity = null;
		try {
			// TODO is this call blocking? Figure out what to do if it isn't.
			entity = this.getContextService().getEntity(this.entity.getId());
			// Is the Entity currently present in the ContextService?
			if (entity == null) {
				// Not present, we need to add a new entity
				this.getContextService().addEntity(this.entity);
			}
			// Entity should be present now, so we add the location information.
			this.addLocationContextItem(device.getLocation());
		} catch (RemoteException e) {
			/*
			 * TODO Log error and/or provide feedback to client through return
			 * type?
			 */
			System.err
					.println(this.getClass().getSimpleName()
							+ ": "
							+ e.getClass().getSimpleName()
							+ " occurred in onDeviceFound() - could not add/update Entity in ContextService.");
			e.printStackTrace();
		}
	}

	/**
	 * Utility method for feeding the {@link ContextService} with location
	 * information for the {@link Entity} monitored by this
	 * {@code BLIPDeviceMonitor} (the {@link Entity} is identified in the
	 * {@link ContextService} using the {@link #deviceId} of this
	 * {@code BLIPDeviceMonitor}).
	 * 
	 * @param location
	 *            A {@link String} describing the location.
	 * @throws RemoteException
	 *             If the RMI call to the {@link ContextService} fails.
	 * @throws NullPointerException
	 *             if {@code location} is {@code null}.
	 */
	private void addLocationContextItem(String location) throws RemoteException {
		Objects.requireNonNull(location);
		this.getContextService().addContextItem(this.entity.getId(), new Located(this.entity.getId()),
				new Location(location));
	}

	/**
	 * Utility method for removing the {@link Entity} managed by this
	 * {@code BLIPDeviceMonitor} from the {@link ContextService}.
	 * 
	 * @throws RemoteException
	 *             If the RMI call to the {@link ContextService} fails.
	 */
	private void removeEntity() throws RemoteException {
		this.getContextService().removeEntity(this.entity.getId());
	}

	/**
	 * Utility method for removing this monitor from its associated
	 * {@link ContextService}.
	 */
	private void removeFromContextService() {
		try {
			this.getContextService().removeContextClient(
					RemoteContextClient.TYPE_MONITOR, Location.class, this);
		} catch (RemoteException e) {
			/*
			 * TODO: Handle error: logging?
			 */
			System.err
					.print(this.getClass().getSimpleName()
							+ " end lifecycle: error when attempting to remove self from ContextService");
			e.printStackTrace();
		}
	}
}
