package cs158project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
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
			ML_printToConsole_(request_);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		
		BufferedReader inbufread = new BufferedReader(
			new InputStreamReader(sock.getInputStream()));
		
		String str;
		while (null != (str = inbufread.readLine())) {
			System.out.println(str);
		}
		
		inbufread.close();
	}
	
	private void ML_process_(Socket inbound) throws UnknownHostException, IOException {
		
		// query protocol for destination
		ConnectionConfiguration destination = protocol_.getResource();
				
		Socket outbound = new Socket(destination.host, destination.port);
		
		BufferedReader inbufread = new BufferedReader(
			new InputStreamReader(inbound.getInputStream()));
			
		OutputStreamWriter osw = new OutputStreamWriter(
			outbound.getOutputStream());
		
		String val;
		while ((val = inbufread.readLine()) != null) {
			osw.write(val);
			osw.flush();
		}
		
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
