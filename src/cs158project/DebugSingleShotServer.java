package cs158project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DebugSingleShotServer implements Runnable {
	
	ServerSocket server_;
	Socket inbound_;
	final int wait_;
	
	public DebugSingleShotServer(int port, int wait) throws IOException {
		server_ = new ServerSocket(port);
		wait_ = wait;
	}
	
	public void listen() {
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		
		if (null == server_)
			return;
		
		try {
			server_.setSoTimeout(wait_);
			System.out.println("[DEBUG ECHO] Start echo...");
			inbound_ = server_.accept();
			System.out.println("[DEBUG ECHO] Echo receive...");
			ML_printToConsole_(inbound_);
			System.out.println("[DEBUG ECHO] Echo send...");
			ML_printToSocket_(inbound_);
			System.out.println("[DEBUG ECHO] Exit echo.");
		} catch (IOException e) {
			System.out.println("[DEBUG ECHO] Echo timeout. Exit echo.");
			e.printStackTrace();
			System.out.println("\n");
		} finally {
			
			if (null != inbound_) {
				try {
					inbound_.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		if (null != server_)
			server_.close();
		
		if (null != inbound_)
			inbound_.close();
		
		super.finalize();
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
				"[DEBUG ECHO] *** source port: %d, source address: %s", 
				sock.getPort(),
				sock.getInetAddress()));
		
		BufferedReader inbufread = new BufferedReader(
			new InputStreamReader(sock.getInputStream()));
		
		// note: cannot read and then write at the same time
		
		String str;
		//while (null != (str = inbufread.readLine())) {
			str = inbufread.readLine();
			System.out.println(String.format("[DEBUG ECHO] %s", str));
			
		//}
		
		System.out.println("[DEBUG ECHO] Closing in buffer.");
		
		// NOTE: DO NOT CLOSE THE STREAM! CLOSING THE STREAM CLOSES THE CONNECTION!
		
	}
	
	private void ML_printToSocket_(Socket sock) throws IOException {
		
		if (!sock.isOutputShutdown()) {
			System.out.println("[DEBUG ECHO] Writing to output stream.");
			OutputStreamWriter pw = new OutputStreamWriter(sock.getOutputStream());
			pw.write("Hello from the echo server, AGAIN!\n");
			pw.flush();
		} else {
			System.out.println("[DEBUG ECHO] Output stream is shutdown.");
		}
		// NOTE: DO NOT CLOSE THE STREAM! CLOSING THE STREAM CLOSES THE CONNECTION!
	}
}

