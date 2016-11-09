package SystemY;

import java.rmi.*;

public class NodeClient {
	public static void main(String argv[]) {
		//TODO get IP address in discover
		try {
			String name = "//localhost:1099/NamingServer";
			NamingServerInterface ni = (NamingServerInterface) Naming.lookup(name);
			System.out.println(ni.getFileLocation("woop woop"));
		} catch (Exception e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			e.printStackTrace();  
		}

	}

}
