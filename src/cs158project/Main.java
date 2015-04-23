package cs158project;

import java.io.IOException;

public class Main {

	// TODO: Use rmi to setup cli starts / stops
	
	public static void main(String[] args) {
		
		System.out.println("[DEBUG] Init...");
		
		// setup web server endpoints
		ResourcePool resources = new ResourcePool();
		resources.register(new ResourcePlugin("10.10.10.2", 80, "WebServer_1"));
		resources.register(new ResourcePlugin("10.10.10.3", 80, "Webserver_2"));
		
		// set connection protocol
		//LoadBalancer.setProtocol(new DebugConnectionProtocol());
		
		try {
			LoadBalancingService svc = new LoadBalancingService(
				80, new DebugConnectionProtocol());
		
			// start echo
			//(new DebugSingleShotServer(6666, 15000)).listen();
		
			System.out.println("Start balancing...");
			//LoadBalancer.start();
			svc.start();
			System.out.println("Main thread pause...");
			Thread.sleep(60000);
			System.out.println("Main thread restart...");
			svc.stop();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//LoadBalancer.stop();
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
