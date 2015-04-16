package cs158project;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadBalancerService implements Runnable {
	
	private final static int DEFAULT_BACK_LOG_SIZE = 10;
	
	private final ConnectionConfiguration serviceConfig_;
	
	private AtomicBoolean running_ = new AtomicBoolean(false);
	
	private ServerSocket serverSocket_;
	private ConnectionProtocol protocol_;
	private Thread service_;
	private ExecutorService workers_;
	
	public LoadBalancerService(ConnectionConfiguration config, ConnectionProtocol protocol) {
		serviceConfig_ = config;
		protocol_ = protocol;
		workers_ = Executors.newFixedThreadPool(DEFAULT_BACK_LOG_SIZE);
	}
	
	public void start() {
		
		if (null != service_) {
			// service already running
			return;
		}
		
		service_ = new Thread(this);
		service_.start();
	}
	
	public void stop() {
		running_.set(false);
		service_ = null;
	}
	
	public boolean isRunning() {
		return running_.get();
	}
	
	@Override
	public void run() {
		
		Socket request;
		
		running_.set(true);
		
		try {
			
			//serverSocket_ = new ServerSocket(serviceConfig_.port);
		
			while (running_.get()) {
				
				DEBUG_print();
				
				request = null; //serverSocket_.accept();
				// handle request
				// TODO: use a thread factory to build this thread. you can exchange factories!
				workers_.execute(
					new LoadBalancerProcessor(request, protocol_));
			}
			
			//serverSocket_.close();
			
		} catch (InterruptedException e) {
		
		} finally {
			running_.set(false);
			workers_.shutdown();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		
		running_.set(false);
		
		if (null != service_) {
			service_.interrupt();
		}
		
		if (null != serverSocket_ && !serverSocket_.isClosed()) {
			serverSocket_.close();
		}
		
		if (null != workers_ && !workers_.isTerminated()) {
			workers_.shutdownNow();
		}
		
		super.finalize();
	}

	private void DEBUG_print() throws InterruptedException {
		System.out.println("I'm servicing stuff...");
		Thread.sleep(10);
	}
}
