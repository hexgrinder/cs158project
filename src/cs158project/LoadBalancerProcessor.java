package cs158project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;

/**
 * Processor for handling connections to the LoadBalancerService class.
 * 
 * @author Michael L., Torjus D.
 */
public class LoadBalancerProcessor implements Runnable {

	private final UUID id_ = UUID.randomUUID();
	
	private Socket request_;
	private final ConnectionProtocol protocol_;
	
	public LoadBalancerProcessor(Socket request, ConnectionProtocol protocol) {
		request_ = request;
		protocol_ = protocol;
	}
	
	@Override
	public void run() {
		
		try {
/*			
			//Class HttpURLConnection.getHeaderField(int n)
			//to use for finding and keeping track of sessioninfo
			
			//ADD call to getResource()
			connectionConfiguration serverConnection = protocol_.getResource();
			InetAddress clientAddress = request_.getInetAddress();
			
			//fetch IP of connection client
			String address = clientAddress.toString();
			int portNumber = request_.getPort();
			connectionConfiguration clientConnection = new ConnectionConfiguration(address, portNumber, "client");
			
			Socket serverSocket = new Socket(serverConnection.host, serverConnection.port);
			
			//10.10.10.1
			//10.10.10.2
			//10.10.10.3
			//port 80
*/
			//ML_printToConsole_(request_);
			ML_process_(request_);
			
		} catch (UnknownHostException e) {
			System.out.println("[DEBUG] Unknown host exception.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[DEBUG] IO exception.");
			e.printStackTrace();
		} finally {
			try {
				System.out.println("[DEBUG] Closing request connection.");
				request_.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Debugging function. Prints to the console.
	 * 
	 * @param sock Socket object.
	 * @throws IOException Thrown when there is an issue accessing the 
	 * socket object.
	 */
	private void ML_printToConsole_(Socket sock) throws IOException {
		
		// retreives sources address and port
		System.out.println(
			String.format(
				"[DEBUG] source port: %d, source address: %s", 
				sock.getPort(),
				sock.getInetAddress()));
		
		BufferedReader inbufread = new BufferedReader(
			new InputStreamReader(sock.getInputStream()));
		
		String str;
		while (null != (str = inbufread.readLine())) {
			System.out.println(str);
		}
		
		System.out.println("[DEBUG] Closing in buffer.");
		
		inbufread.close();
	}
	
	private void ML_makeConnection(Socket inbound) {
		
		// query protocol for destination
		ConnectionConfiguration destination = protocol_.getResource();
		
		System.out.println(
			String.format(
				"[DEBUG] Trying to connect to outbound host: %s, port %d.",
				destination.host,
				destination.port));
		
		BufferedReader destinbufread = null;
		OutputStreamWriter inwrite = null;
		
		try {
			
			// open an http connection to the destination
			URL dest = new URL(destination.host);
			HttpURLConnection desthttp = (HttpURLConnection) dest.openConnection();
			
			// setup input from dest
			destinbufread = new BufferedReader(
				new InputStreamReader(desthttp.getInputStream()));
			
			// setup output to inbound
			inwrite = new OutputStreamWriter(
				inbound.getOutputStream());	
				
			// write to inbound
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != destinbufread) {
				try {
					destinbufread.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (null != inwrite) {
				try {
					inwrite.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void ML_process_(Socket inbound) throws UnknownHostException, IOException {
		
		// query protocol for destination
		ConnectionConfiguration destination = protocol_.getResource();
		
		System.out.println(
			String.format(
				"[DEBUG] Trying to connect to outbound host: %s, port %d.",
				destination.host,
				destination.port));
/*
		OutputStreamWriter osw = new OutputStreamWriter(
			inbound.getOutputStream());
		osw.write("You have hit the load balancer.");
		osw.close();
*/
		
		// note: creating a new socket will open a connection
		Socket outbound = new Socket(destination.host, destination.port);
		
		if (outbound.isConnected()) {
			System.out.println(
				String.format(
					"[DEBUG] Connected outbound: %s.", 
					outbound.getRemoteSocketAddress()));
			
			BufferedReader inbufread = new BufferedReader(
				new InputStreamReader(inbound.getInputStream()));
			OutputStreamWriter inwrite = new OutputStreamWriter(
				inbound.getOutputStream());	
			
			BufferedReader outbufread = new BufferedReader(
				new InputStreamReader(outbound.getInputStream()));
			OutputStreamWriter outwrite = new OutputStreamWriter(
				outbound.getOutputStream());
		
			String val;
			// BUG: read is blocking
			// BUG: halts after header section -> it hits a 1013 set then stops

			while ((val = inbufread.readLine()) != null) {
				// HOW TO DEAL WITH BREAK?
				if ("\n" != val) {
					// inbound -> outbound
					System.out.println(
						String.format("[DEBUG PROCESS IN->OUT] %s", val));
					outwrite.write(String.format("%s\n", val));
					outwrite.flush();
				} else {
					System.out.println("[DEBUG PROCESS IN->OUT] HALT");
				}
			}
// uncomment the following to send response from server to client
/*
			
			while ((val = outbufread.readLine()) != null) {
				// outbound -> inbound
				System.out.println(
					String.format("[DEBUG PROCESS OUT->IN] %s", val));
				inwrite.write(val);
				inwrite.flush();
			}
*/
			inbufread.close();
			outbufread.close();
			inwrite.close();
			outwrite.close();
			
			
		} else {
			System.out.println("[DEBUG] Outbound not connected.");
		}
		
		System.out.println("[DEBUG] Closing outbound.");
		outbound.close();
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
