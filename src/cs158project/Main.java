package cs158project;

import java.io.IOException;

public class Main {

	// TODO: Use rmi to setup cli starts / stops
	
	public static void main(String[] args) {
		
		Debug.println("MAIN 1", "Init...");
		
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
