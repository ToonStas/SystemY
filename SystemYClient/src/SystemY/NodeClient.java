package SystemY;

import java.rmi.*;

public class NodeClient {
	public static void main(String argv[]) {
		//TODO get IP address in discover
		try {
			String name = "//192.168.1.3/NamingServer";
			NamingServerInterface fi = (NamingServerInterface) Naming.lookup(name);
			System.out.println(fi.getFileLocation("woop woop"));
		} catch (Exception e) {
			System.err.println("FileServer exception: " + e.getMessage());
			e.printStackTrace();  
		}

	}

}
