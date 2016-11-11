package SystemY;

import java.io.UnsupportedEncodingException;
import java.rmi.*;

public class NodeClient {
	public static void main(String argv[]) {
		new NodeClient();
	}
	
	public NodeClient(){
		startUp();
		String location = getFileLocation("test.txt");
		System.out.println("Locatie van het bestand is: " + location);
	}
	
	public String getFileLocation(String fileName){
		String location = "";
		//TODO get IP address in discover
		try {
			String name = "//localhost:1099/NamingServer";
			NamingServerInterface ni = (NamingServerInterface) Naming.lookup(name);
			location = ni.getFileLocation(fileName);
		} catch (Exception e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			e.printStackTrace();  
		}
		return location;
	}
	
	public void startUp(){
		try {
			new MulticastSender();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
