package cs158project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * This is a background thread that processes the inbound request 
 * received by the service. This class obtains the destination 
 * resource from the load distribution protocol and opens the 
 * connection between the inbound request and the destination 
 * resource. 
 * 
 * @author Michael L., Torjus D.
 */
public class LoadBalancingWorker extends Thread {

	private final SocketChannel channel_;
	private final ConnectionProtocol protocol_;
	
	/**
	 * Constructor.
	 * 
	 * @param channel - The in-bound origin socket.
	 * @param protocol - The connection protocol that determines the
	 * destination IP.
	 */
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
				"Processing inbound from origin %s", 
				channel.socket().getRemoteSocketAddress()));
		
		//found in ConnectionProtocol interface for loadbalancingalgo
		ConnectionConfiguration dest = protocol.getResource(
			new ConnectionConfiguration(
				channel.socket().getInetAddress().toString(),
				channel.socket().getPort()));
		
		InetSocketAddress destaddr = new InetSocketAddress(
			InetAddress.getByName(dest.host), dest.port);
		
		// start the connection
		(new CircuitConnection())
			.send(channel, destaddr);
		
		Debug.println("WORKER","Processing end.");
		
	}
}
