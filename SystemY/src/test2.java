import java.io.IOException;
import java.net.*;

public class test2 {
	
	private int port = 8769; //Poort naarwaar we verzenden
	private String group = "224.1.1.1";
	//private int ttl = 1;		--> TTL weglaten?
	private MulticastSocket s;
	
	public test2(){
		try {
			s = new MulticastSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sending();
		close();
	}

	public void sending(){
		byte buf[] = new byte[10];
	
		for(int i=0 ; i<buf.length; i++){
			buf[i] = (byte)i;
		}
		DatagramPacket pack = new DatagramPacket(buf, 10);
		try {
			pack = new DatagramPacket(buf, buf.length,InetAddress.getByName(group), port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			s.send(pack); 	//,(byte)ttl); --> TTL weglaten?
			System.out.println("Pakket is verzonden.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void close(){
		s.close();
	}	
}
