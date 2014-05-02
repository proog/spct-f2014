package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Recording {
	public final RecordingType type;
	public final String fileName;
	
	private boolean checked;
	
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public Recording(RecordingType l, String f) {
		type = l;
		fileName = f;
	}
	
	public void setChecked(boolean checked) {
		this.lock.writeLock().lock();
		this.checked = checked;
		this.lock.writeLock().unlock();
	}
	
	public boolean isChecked() {
		try {
			this.lock.readLock().lock();
			return checked;
		} finally {
			this.lock.readLock().unlock();
		}
	}
}
