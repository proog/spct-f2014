package dk.itu.spct.f2014.pmor.janv.ma01.trigger;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.TriggerMessage;

public interface MessageReceivedObserver {
	public void messageReceived(TriggerMessage m);
}
