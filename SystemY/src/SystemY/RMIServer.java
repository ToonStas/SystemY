package SystemY;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
	public static void main(String[] argv) throws RemoteException {
		NamingServer namingServer = new NamingServer();
		String bindLocation = "192.168.1.1";
		String name = "Server";
		
		try {
			LocateRegistry.createRegistry(1099);
			Naming.bind(bindLocation, namingServer);
			System.out.println("FileServer Server is ready at:" + bindLocation);
			System.out.println("java RMI registry created.");
		} catch (MalformedURLException | AlreadyBoundException e) {
			System.out.println("java RMI registry already exists.");
		}
	}
}
