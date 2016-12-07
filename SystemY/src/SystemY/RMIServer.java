package SystemY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
	NamingServer namingServer;
	
	public static void main(String[] argv) throws RemoteException {
		new RMIServer();
	}
	
	public RMIServer() throws RemoteException{
		namingServer = new NamingServer();
		String bindLocation = "NamingServer";
		try {
			Registry reg = LocateRegistry.createRegistry(1099);
			reg.bind(bindLocation, namingServer);
			System.out.println("NamingServer is ready at: " + bindLocation);
			System.out.println("java RMI registry created.");
		} catch (AlreadyBoundException e) {
			System.out.println("java RMI registry already exists.");
		}
	
		
		while(true)
			consoleGUI();
	}
	
	//start the consolegui
		private void consoleGUI() throws RemoteException {
			System.out.println("What do you want to do?");
			System.out.println("[1] Print Nodes");
			System.out.println("[2] Look for file");
			System.out.println("[3] Open");
			System.out.println("[4] Open");
			System.out.println("[9] Exit");

			int input = Integer.parseInt(readConsole());
			System.out.println("Your choice was: " + input);

			switch (input) {
			case 1:
				System.out.println("The nodes are: ");
				namingServer.nodeLijst.listAllNodes();
				break;

			case 2:
				System.out.println("Enter file to look for: ");
				String location = namingServer.getFileLocation(readConsole());
				System.out.println("The location is: " + location);
				break;

			case 3:
				break;
				
			case 4:
				break;	
				
			case 666:
				System.out.println("------------------------");
				System.out.println("The number of the Beast!");
				System.out.println("------------------------");
				break;
				
			case 9:
				//shutdown();
				break;
			}
		}
		
		//for consolegui
		//read what user types in the console
		private String readConsole() {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = "";
			try {
				input = br.readLine();
			} catch (IOException | NumberFormatException e) {
				e.printStackTrace();
				readConsole();
			}
			return input;
		}
}