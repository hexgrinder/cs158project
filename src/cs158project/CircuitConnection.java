package cs158project;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CircuitConnection {

	private static final int DEFAULT_TIMEOUT = 15000; 		// milliseconds
	private static final int DEFAULT_BUFFER_SIZE = 2048;	// bytes

	private final ByteBuffer originBuffer_;
	private final ByteBuffer destinationBuffer_;
	
	public CircuitConnection() throws IOException {
		originBuffer_ = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		destinationBuffer_ = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
	}
	
	public void send(SocketChannel origin, SocketAddress destinationAddr) {
		
		SocketChannel destination = null;
		
		// TODO: Timeouts on reads and writes. right now it is 
		// hanging the thread.

		/*
		 
		 http://stackoverflow.com/questions/2866557/timeout-for-socketchannel-doesnt-work
		 
		 SocketChannel socketChannel;
		 socketChannel.socket().setSocketTimeout(DEFAULT_TIMEOUT);
		 InputStream inStream = socketChannel.socket().getInputStream();
		 ReadableByteChannel wrappedChannel = Channels.newChannel(inStream);
		 
		 */
		originBuffer_.clear();
		destinationBuffer_.clear();
		
		try {
			
			// open connection to destination
			destination = SocketChannel.open();
			destination.configureBlocking(true);
			destination.connect(destinationAddr);
			
			// block origin
			origin.configureBlocking(true);
			
			// read from origin to write to destination
			origin.read(destinationBuffer_);			
			
			Debug.println("CIRCUIT", "writing to destination...");
			
			destinationBuffer_.flip();
			Debug.println(
					"CIRCUIT", 
					String.format(
						"wrote %d to destination.", 
						destination.write(destinationBuffer_)));
				
			// read from destination into origin buffer
			destination.read(originBuffer_);
			
			destination
				.shutdownOutput()
				.close();
			
			// write to source
			Debug.println("CIRCUIT", "writing to origin...");
			
			originBuffer_.flip();
			
			Debug.println(
				"CIRCUIT", 
				String.format(
					"wrote %d to origin.", 
					origin.write(originBuffer_)));
			
			origin
				.shutdownInput()
				.shutdownOutput()
				.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != destination && destination.isOpen()) {
				try {
					destination.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
