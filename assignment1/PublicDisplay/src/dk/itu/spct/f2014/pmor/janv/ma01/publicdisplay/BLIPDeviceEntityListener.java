package dk.itu.spct.f2014.pmor.janv.ma01.publicdisplay;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.context.BLIPDeviceEntity;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.ContextItem;
import dk.pervasive.jcaf.EntityListener;
import dk.pervasive.jcaf.RemoteEntityListener;
import dk.pervasive.jcaf.impl.RemoteEntityListenerImpl;
import dk.pervasive.jcaf.item.Location;
import dk.pervasive.jcaf.relationship.Located;
import dk.pervasive.jcaf.util.AbstractContextClient;

public class BLIPDeviceEntityListener extends AbstractContextClient implements EntityListener, Serializable {
	private PublicDisplay display;
	private String displayLocation;
	private HashMap<String, BLIPDeviceEntity> currentEntities = new HashMap<>();
	private RemoteEntityListenerImpl listener;
	
	public BLIPDeviceEntityListener(String service_uri, PublicDisplay display, String displayLocation) {
		this(service_uri);
		this.display = display;
		this.displayLocation = displayLocation;
	}
	
	public BLIPDeviceEntityListener(String service_uri) {
		super(service_uri);
		
		try {
			listener = new RemoteEntityListenerImpl();
			listener.addEntityListener(this);
			this.getContextService().addEntityListener(listener, BLIPDeviceEntity.class);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextChanged(final ContextEvent arg0) {
		System.out.println("Event: " + arg0.getEventType() + " " + arg0.getEventTypeText());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BLIPDeviceEntity entity = (BLIPDeviceEntity) arg0.getEntity();
				String deviceId = entity.getId();
				ContextItem contextItem = arg0.getItem();
				if(!(contextItem instanceof Location)) {
					System.out.println("Context item: " + contextItem.getClass().getSimpleName());
					return;
				}
				
				String location = ((Location) contextItem).getId();
				boolean isInCurrentEntities = currentEntities.containsKey(deviceId);
				boolean isInThisLocation = location.contains(displayLocation);
				
				if(!isInCurrentEntities && isInThisLocation) {
					// add entity
					currentEntities.put(deviceId, entity);
					display.entityEnter(entity);
				}
				else if(isInCurrentEntities && !isInThisLocation) {
					// remove entity
					currentEntities.remove(deviceId);
					display.entityLeave(entity);
				}
			}
		});
	}

	@Override
	public void run() {
		// do nothing
		while(true);
	}
}
