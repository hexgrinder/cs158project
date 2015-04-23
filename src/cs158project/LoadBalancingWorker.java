package cs158project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class LoadBalancingWorker extends Thread {

	private final SocketChannel channel_;
	private final ConnectionProtocol protocol_;
	
	public LoadBalancingWorker(
		SocketChannel channel,
		ConnectionProtocol protocol) 
	{
		channel_ = channel;
		protocol_ = protocol;
	}

	@Override
	public void run() {
		try {
			process_(channel_, protocol_);
		} catch (IOException e) {
			if (null != channel_ && channel_.isOpen()) {
				try {
					channel_
						.shutdownInput()
						.shutdownOutput()
						.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (null != channel_ && channel_.isOpen()) {
			channel_
				.shutdownInput()
				.shutdownOutput()
				.close();
		}
		super.finalize();
	}

	private void process_(
		SocketChannel channel, 
		ConnectionProtocol protocol) throws IOException 
	{
		Debug.println(
			"WORKER",
			String.format(
				"Processing inbound from host %s", 
				channel.getRemoteAddress()));
		
		Debug.println("WORKER", "writing.");
		
		ConnectionConfiguration dest = protocol.getResource();
		
		InetSocketAddress destaddr = new InetSocketAddress(
			InetAddress.getByName(dest.host), dest.port);
			
		(new CircuitConnection())
			.send(channel, destaddr);
		
		Debug.println("WORKER","process end.");
		
	}
}
