package cs158project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Distributes in-bound requests according to a connection protocol.
 * 
 * Implements the Runnable interface. Creates and manages its own separate 
 * thread to service in-bound requests. 
 * 
 * Note: This class was designed as a 'lone-wolf' service. Unpredictable 
 * behavior may result if implemented as a shared resources among 
 * concurrent processes.
 * 
 * @author Michael L., Torjus D.
 */
public class LoadBalancingService implements Runnable {
	

	private final static int DEFAULT_WORKER_POOL_SIZE = 25;
	private final static int DEFAULT_BACKLOG_SIZE = 10;
	private final static int DEFAULT_PORT = 80;
	
	private final ConnectionConfiguration serviceConfig_;
	
	private AtomicBoolean running_ = new AtomicBoolean(false);
	
	private ServerSocketChannel serverSocket_;
	private ConnectionProtocol protocol_;
	private Thread service_;
	private ExecutorService workers_;
	
	public LoadBalancingService(int port, ConnectionProtocol protocol) throws IOException {
		this(new ConnectionConfiguration(
			InetAddress.getLocalHost().getHostAddress(), 
			port), 
		protocol);
	}
	
	/**
	 * Constructor. 
	 * 
	 * Binds the load balancer to a connection port and a connection 
	 * distribution algorithm.
	 * 
	 * @param config Connection settings
	 * @param protocol Connection distribution protocol
	 */
	public LoadBalancingService(ConnectionConfiguration config, ConnectionProtocol protocol) {
		serviceConfig_ = config;
		protocol_ = protocol;
		workers_ = Executors.newFixedThreadPool(DEFAULT_WORKER_POOL_SIZE);
	}
	
	
	/**
	 * Starts the service.
	 *
	 * Note: Successive start() calls are silently ignored. A stop() 
	 * must be called prior for this method to take effect.
	 */
	public void start() {
		
		if (null != service_) {
			// service already running
			return;
		}
		
		// TODO: put on executor class
		service_ = new Thread(this);
		service_.start();
		running_.set(true);
	
	}
	
	/**
	 * Attempts to stop the service in an orderly matter. Stops listening for
	 * incoming requests.  Allows existing requests to complete. 
	 * 
	 * Once this method is called, the service cannot be restarted.
	 * 
	 * Note: Successive stop() calls are silently ignored. A start() 
	 * must be called prior for this method to take effect.
	 */
	public void stop() {
		
		if (null == service_) {
			// service already stopped
			return;
		}
		
		running_.set(false);
		workers_.shutdown();
		service_ = null;
		
		try {
			if (null != serverSocket_ && serverSocket_.isOpen()) {
				serverSocket_.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("\n[DEBUG] STOP -> close server socket.");
			e.printStackTrace();
		} finally {
			serverSocket_ = null;
		}
	}
	
	/**
	 * Returns the service run status.
	 * 
	 * @return TRUE if the server is running, FALSE otherwise.
	 */
	public boolean isRunning() {
		return running_.get();
	}
	
	@Override
	public void run() {
		
		Debug.println("SERVICE", "Service starting...");
		// does this keep it open?
		SocketChannel request;
		
		try {
			
			serverSocket_ = ServerSocketChannel.open();
/* // java 6
			serverSocket_.socket().bind(new InetSocketAddress(
					InetAddress.getLocalHost(), serviceConfig_.port), 
				DEFAULT_BACKLOG_SIZE);
*/			
			Debug.println(
				"SERVICE", 
				String.format(
					"Running on address %s", 
					serverSocket_.socket().getLocalSocketAddress()));
//java 8		
			serverSocket_.bind(
				new InetSocketAddress(
					InetAddress.getLocalHost(), serviceConfig_.port), 
				DEFAULT_BACKLOG_SIZE);
			
			// this loop halts when:
			// 1) accept() throws a SocketException (serverSocket closed)
			// 2) accept() throws any other exception
			while (true) {
				
				// listening will block until stop() method is called 
				request = serverSocket_.accept();
				
				
				
				// in-bound requests on a separate thread
				workers_.execute(
					new LoadBalancingWorker(request, protocol_));
				
				request = null;
			}
			
			
		} catch (SocketException e) {
			// DEBUG
			System.out.println("[DEBUG] RUN -> SocketExceoption: Socket Closed.");
			e.printStackTrace();
		} catch (IOException e) {
			// DEBUG
			System.out.println("[DEBUG] RUN -> IOExceoption.");
			e.printStackTrace();
		} finally {
			System.out.println("[DEBUG] Trying to close server socket.");
			if (null != serverSocket_ && serverSocket_.isOpen()) {
				System.out.println("[DEBUG] Closing server socket.");
				try {
					serverSocket_.close();
				} catch (IOException e) {
					System.out.println("[DEBUG] Exception closing server socket.");
					// DEBUG
					e.printStackTrace();
				} finally {
					System.out.println("[DEBUG] RUN -> Terminating processor thread.");
				}
			} else {
				System.out.println("[DEBUG] RUN -> Already closed. Terminating processor thread.");
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		
		running_.set(false);
		
		if (null != serverSocket_ && serverSocket_.isOpen()) {
			serverSocket_.close();
		}
		
		if (null != workers_ && !workers_.isTerminated()) {
			workers_.shutdownNow();
		}
		
		super.finalize();
	}
	
	public static void main(String[] args) {
		
		Debug.println("MAIN 2", "Init...");
		
		// setup web server endpoints
		ResourcePool resources = new ResourcePool();
		resources.register(new ResourcePlugin("10.10.10.2", 80, "WebServer_1"));
		resources.register(new ResourcePlugin("10.10.10.3", 80, "Webserver_2"));
		
		//interface ConnectionProtocol, implemented in LoadBalancingAlgoritmh
		LoadBalancingAlgorithm LBA = new LoadBalancingAlgorithm(resources);
		
		try {
			LoadBalancingService svc = new LoadBalancingService(
				80, LBA);
		
			// uncomment to start a listening server: starts echo server
			//(new DebugSingleShotServer(6666, 15000)).listen();
		
			Debug.println("MAIN", "Start balancing...");
			svc.start();
			
			/*
			Debug.println("MAIN", "Main thread pause...");
			Thread.sleep(20000);
			Debug.println("MAIN", "Main thread restart...");
			svc.stop();
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		*/
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		Debug.println("MAIN", "Stop balancing.");
	}

}
