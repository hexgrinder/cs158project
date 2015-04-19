package cs158project;

public class Main {

	// TODO: Use rmi to setup cli starts / stops
	
	public static void main(String[] args) {
		
		System.out.println("Init...");
		
		// setup web server endpoints
		ResourcePool resources = new ResourcePool();
		resources.register(new ResourcePlugin("10.10.10.2", 80, "WebServer_1"));
		resources.register(new ResourcePlugin("10.10.10.3", 80, "Webserver_2"));
		
		// set connection protocol
		LoadBalancer.setProtocol(new DebugConnectionProtocol(resources));
		
		System.out.println("Start balancing...");
		LoadBalancer.start();
		try {
			System.out.println("Main thread pause...");
			// sleep while i do tests
			// http request to a test server:
			// curl --data hello http://10.10.10.1  
			Thread.sleep(15000);
			System.out.println("Main thread restart...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LoadBalancer.stop();
		System.out.println("Stop balancing...");
/*		
		//System.out.println("Restart...");
		LoadBalancer.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LoadBalancer.stop();
*/
		System.out.println("Stopping...");
	}

}
