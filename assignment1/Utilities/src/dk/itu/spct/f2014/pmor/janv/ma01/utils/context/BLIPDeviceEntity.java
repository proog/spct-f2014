package dk.itu.spct.f2014.pmor.janv.ma01.utils.context;

import java.io.Serializable;

import dk.pervasive.jcaf.entity.GenericEntity;

public class BLIPDeviceEntity extends GenericEntity implements Serializable {
	private static final long serialVersionUID = -3967760150398588415L;
	private String name;
	
	public BLIPDeviceEntity(String deviceId) {
		super(deviceId);
	}
	
	public BLIPDeviceEntity(String deviceId, String name) {
		super(deviceId);
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.getName() + " (" + this.getId() + ")";
	}
}
