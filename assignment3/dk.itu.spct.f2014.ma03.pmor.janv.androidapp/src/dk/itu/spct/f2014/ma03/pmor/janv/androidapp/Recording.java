package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

public class Recording {
	public final long timestamp;
	public final RecordingType type;
	public final String fileName;
	
	public Recording(long t, RecordingType l, String f) {
		timestamp = t;
		type = l;
		fileName = f;
	}
}
