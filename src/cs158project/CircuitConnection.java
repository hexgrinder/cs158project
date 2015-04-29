package cs158project;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

/**
 * Represents a circuit between two TCP endpoints. The circuit contains
 * no intermediary hops.
 * 
 * This class is not thread-safe. A shared instance is not guaranteed to
 * operate correctly without proper synchronization.
 * 
 * @author Michael L.
 */
public class CircuitConnection {

	private static final int DEFAULT_TIMEOUT = 15000; 		// milliseconds
	private static final int DEFAULT_BUFFER_SIZE = 2048;	// bytes

	private final ByteBuffer originBuffer_;
	private final ByteBuffer destinationBuffer_;
	
	public CircuitConnection() throws IOException {
		originBuffer_ = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		destinationBuffer_ = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * Initiates a blocking, read-write relay between the origin
	 * socket and the destination address.
	 *   
	 * Invoking this method does not shutdown or close the origin 
	 * endpoint.
	 * 
	 * @param origin Origin socket
	 * @param destinationAddr IP destination address.
	 * @throws IOException I/O exception. Will close down the destination
	 * connection upon error.
	 */
	public void send(SocketChannel origin, SocketAddress destinationAddr) 
	throws IOException
	{
		originBuffer_.clear();
		destinationBuffer_.clear();
		
		relay_(origin, destinationAddr);
	}
	
	/**
	 * Creates a relaying connection as illustrated:
	 * 
	 *                              Circuit
	 *         origin              Connection          destination
	 *                              Buffers
	 *        --------            -------------       -------------            
	 *            | --- read ---> (destination) -- write -> |
	 *            | <---- write ---- (origin) <--- read --- |
	 *
	 * @param origin Origin socket
	 * @param destinationAddr IP destination address.
	 * @throws IOException I/O exception. Will close down the destination
	 * connection upon error.
	 */
	private void relay_(SocketChannel origin, SocketAddress destinationAddr)
	throws IOException 
	{
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

		try {
			Debug.println("CIRCUIT", "starting.");
			
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
			Channels.newInputStream(destination);	
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
			
			Debug.println("CIRCUIT", "ending.");
			
		} catch (IOException e) {
			throw e;
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
