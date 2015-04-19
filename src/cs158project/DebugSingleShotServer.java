package cs158project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Debugging support class.
 * 
 * A listening server that runs on a local port.
 * 
 * This is a single shot server -- once it times out or terminates normally,
 * the server will need to be restarted again.
 * 
 * @author Michael L.
 */
public class DebugSingleShotServer implements Runnable {
	
	private ServerSocket server_;
	private Socket inbound_;
	private boolean running_;
	private final int wait_;
	
	/**
	 * Binds the server to a local port.
	 * 
	 * @param port Available port on the local machine.
	 * @param wait Number of milliseconds before the server times out.
	 * @throws IOException Exception thrown when assigning the port number.
	 */
	public DebugSingleShotServer(int port, int wait) throws IOException {
		server_ = new ServerSocket(port);
		wait_ = wait;
		running_ = false;
	}
	
	public boolean isRunning() {
		return running_;
	}
	
	/**
	 * Starts the listening. Subsequent listen() calls are silently ignored
	 * until the server terminates.
	 */
	public void listen() {
		if (running_) return;
		running_ = true;
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
			//System.out.println("[DEBUG ECHO] Echo send...");
			//ML_printToSocket_(inbound_);
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
			running_ = false;
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
		while (null != (str = inbufread.readLine())) {
			System.out.println(String.format("[DEBUG ECHO] %s", str));
		}
		
		System.out.println("[DEBUG ECHO] Closing in buffer.");
		
		inbufread.close();
	}
	
	private void ML_printToSocket_(Socket sock) throws IOException {
		
		if (!sock.isOutputShutdown()) {
			System.out.println("[DEBUG ECHO] Writing to output stream.");
			OutputStreamWriter pw = new OutputStreamWriter(sock.getOutputStream());
			pw.write("Hello from the echo server!\n");
			pw.flush();
			pw.close();
		} else {
			System.out.println("[DEBUG ECHO] Output stream is shutdown.");
		}
	}
}

