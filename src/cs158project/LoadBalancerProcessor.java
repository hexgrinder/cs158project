package cs158project;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

public class LoadBalancerProcessor implements Runnable {

	private final UUID id_ = UUID.randomUUID();
	
	private Socket request_;
	private ConnectionProtocol protocol_;
	
	public LoadBalancerProcessor(Socket request, ConnectionProtocol protocol) {
		request_ = request;
		protocol_ = protocol;
	}
	
	@Override
	public void run() {
		
		
		try {
			DEBUG_process_(request_);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				request_.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void DEBUG_process_(Socket request) throws InterruptedException {
		
		// query protocol for destination
		ConnectionConfiguration destination = protocol_.getResource();
		
		Random r = new Random();
		
		// TODO: create a connection between inbound and outbound
		System.out.println(String.format(">> Part 1 servicing: %s", id_));
		Thread.sleep(r.nextInt(2500));
		System.out.println(String.format(">> Part 2 servicing: %s", id_));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		request_.close();
		super.finalize();
	}
}
