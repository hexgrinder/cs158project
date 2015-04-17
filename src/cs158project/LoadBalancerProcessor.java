package cs158project;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

public class LoadBalancerProcessor implements Runnable {

    private final UUID id_ = UUID.randomUUID();

    private Socket request_;
    private ConnectionProtocol protocol_;

    // TODO: remove this
    public LoadBalancerProcessor() {}

    public LoadBalancerProcessor(Socket request, ConnectionProtocol protocol) {
	request_ = request;
	protocol_ = protocol;
    }

    @Override
    public void run() {

	Random r = new Random();

	try {

	    //Class HttpURLConnection.getHeaderField(int n)
	    //to use for finding and keeping track of sessioninfo


	    
	    System.out.println(String.format(">> Part 1 servicing: %s", id_));
	    Thread.sleep(r.nextInt(2500));
	    System.out.println(String.format(">> Part 2 servicing: %s", id_));
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

	    //process_(request_);
	    // TODO: Test multiple closes
	    //request_.close();
	    //request_.close();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	/*
	  catch (IOException e) {
	  // TODO Auto-generated catch block
	  e.printStackTrace();
	  }
	*/
	finally {

	}
    }

    private void process_(Socket request) throws InterruptedException {

	// query protocol for destination
	ConnectionConfiguration destination = protocol_.getResource();

	// TODO: create a connection between inbound and outbound
	Thread.sleep(500);
	Thread.yield();
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
