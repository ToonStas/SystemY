package SystemY;

import java.rmi.*;

public class NodeClient {
	public static void main(String argv[]) {
		try {
			String name = "//localhost/NamingServer";
			NamingServerInterface fi = (NamingServerInterface) Naming.lookup(name);
			System.out.println(fi.getFileLocation("woop woop"));
		} catch (Exception e) {
			System.err.println("FileServer exception: " + e.getMessage());
			e.printStackTrace(); 
		}

	}

}
