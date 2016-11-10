
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class MulticastReceiverThread implements Runnable {

	private int port;
	private String multicastGroup;
	private MulticastSocket s;

	public MulticastReceiverThread() {
		port = 8769;
		multicastGroup = "224.1.1.1";
		try {
			s = new MulticastSocket(port); // Create the socket and bind it to port 'port'.
			s.joinGroup(InetAddress.getByName(multicastGroup)); // join the multicast group
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// Create a DatagramPacket and do a receive
		byte buf[] = new byte[1024];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);
		
		try {
			s.receive(pack);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String nameIp = new String(buf, 0, buf.length);
		nameIp = nameIp.replaceAll(Character.toString((char) 0), "");

		//Print out the received data with the info
		System.out.println("Received data from: " + pack.getAddress() + ":" + pack.getPort() + " with length: "
				+ pack.getLength());
		System.out.write(pack.getData(), 0, pack.getLength());
		System.out.println();
		
		//receive another
		run();
	}

	public void close() {
		try {
			s.leaveGroup(InetAddress.getByName(multicastGroup));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s.close();
	}

}
