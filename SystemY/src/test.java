
import java.io.IOException;
import java.net.*;

public class test {

	private int port;
	private String multicastGroup;
	private MulticastSocket s;
	
	public test(){
		port = 5000;
		multicastGroup = "192.168.1.15";
		try {
			s = new MulticastSocket(port);				 		 // Create the socket and bind it to port 'port'.
			s.joinGroup(InetAddress.getByName(multicastGroup));	 // join the multicast group
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		// Create a DatagramPacket and do a receive
		byte buf[] = new byte[1024];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);
		try 
		{
			s.receive(pack);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Finally, let us do something useful with the data we just received,
		// like print it on stdout :-)
		System.out.println("Received data from: " + pack.getAddress().toString() +
				    ":" + pack.getPort() + " with length: " +
				    pack.getLength());
		System.out.write(pack.getData(),0,pack.getLength());
		System.out.println();
	}

	public void close(){
		// And when we have finished receiving data leave the multicast group and
		// close the socket
		try 
		{
			s.leaveGroup(InetAddress.getByName(multicastGroup));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();
	}

}
