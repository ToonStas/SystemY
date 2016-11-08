package SystemY;

import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
	public static void main(String[] argv) throws RemoteException {
		NamingServer namingServer = new NamingServer();
		String bindLocation = "NamingServer";
		try {
			Registry reg = LocateRegistry.createRegistry(1099);
			reg.bind(bindLocation, namingServer);
			System.out.println("FileServer Server is ready at:" + bindLocation);
			System.out.println("java RMI registry created.");
		} catch (AlreadyBoundException e) {
			System.out.println("java RMI registry already exists.");
		}
	}
}
