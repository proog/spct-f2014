package dk.itu.spct.f2014.ma03.pmor.janv.androidapp;

public class Recording {
	public final long timestamp;
	public final String label;
	public final Object[] data;
	
	public Recording(long t, String l, Object[] d) {
		timestamp = t;
		label = l;
		data = d;
	}
}
