package dk.itu.spct.f2014.pmor.janv.ma01.utils.context;

import dk.pervasive.jcaf.entity.GenericEntity;

public class BLIPDeviceEntity extends GenericEntity {
	private static final long serialVersionUID = -3967760150398588415L;
	private String name;
	
	public BLIPDeviceEntity(String deviceId) {
		super(deviceId);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
