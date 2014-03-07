package dk.itu.spct.SimpleTriggerProtocol;

public class MessagePrinter implements MessageReceivedObserver {

	@Override
	public void messageReceived(Message m) {
		System.out.println(m.toString());
	}

}
