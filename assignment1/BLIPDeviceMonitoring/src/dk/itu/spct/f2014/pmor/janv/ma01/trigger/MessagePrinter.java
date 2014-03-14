package dk.itu.spct.f2014.pmor.janv.ma01.trigger;

import dk.itu.spct.f2014.pmor.janv.ma01.utils.TriggerMessage;

public class MessagePrinter implements MessageReceivedObserver {

	@Override
	public void messageReceived(TriggerMessage m) {
		System.out.println(m.toString());
	}

}
