package SystemY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;

public class NamingServer extends UnicastRemoteObject implements NamingServerInterface {
	private static final long serialVersionUID = 1L;
	private Nodelijst nodeLijst;
	private Thread multicastReceiverThread;
	TreeMap<Integer, NodeNamingServer> listOfNodes = new TreeMap<>();

	//Namingserver houdt enkel een lijst van nodes bij met hierin de naam, de hash en het ipadres. Niks meer!
	public NamingServer() throws RemoteException{
		super();
		nodeLijst = new Nodelijst();
		multicastReceiverThread = new Thread(new MulticastReceiverThread(nodeLijst));
		multicastReceiverThread.start();
		
		nodeLijst.addNode("Matthias 192.168.1.4");
		nodeLijst.addNode("Floris 192.168.1.2");
		int val = nodeLijst.addNode("Matthias 192.168.1.4");
		//nodeLijst.writeJSON();
		//nodeLijst.readJSON();
		
		listOfNodes = nodeLijst.getListOfNodes();
		
		nodeLijst.listAllNodes();
	}
	
	public String getFileLocation(String fileName){
		String location = "ipadres";
		int hash = nodeLijst.calculateHash(fileName);
		
		if(listOfNodes.floorEntry(hash)==null){ //geeft het ipadres van de 1ste node <= de waarde van de hash
			location = listOfNodes.lastEntry().getValue().getIpAdress();
		}else{
			location = listOfNodes.floorEntry(hash).getValue().getIpAdress(); 
		}
		
		return location;
	}
	
	public int amIFirst(){
		if(listOfNodes.size()<1){
			return 1;
		}else{
			return 0;
		}
	}

}