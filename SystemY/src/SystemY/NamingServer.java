package SystemY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;

public class NamingServer extends UnicastRemoteObject implements ClientToNamingServerInterface {
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
		
		MulticastSenderNamingServer multicastSenderNamingServer = new MulticastSenderNamingServer();
		
		nodeLijst.writeJSON();
		//nodeLijst.readJSON();
		
		listOfNodes = nodeLijst.getListOfNodes();
		
		nodeLijst.listAllNodes();
	}
	
	//ip adres opvragen van waar het bestand zich bevind
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
	
	//Ask where the file with fileName should be placed
	public String askLocation(String fileName){
		String location = "ipadres";
		int hash = nodeLijst.calculateHash(fileName);
		
		//give ip of the first node <= hash of the filename
		if(listOfNodes.floorEntry(hash)==null){ //if there is no lowest node, return the highest node
			location = listOfNodes.lastEntry().getValue().getIpAdress();
		}else{
			location = listOfNodes.floorEntry(hash).getValue().getIpAdress(); 
		}
		return location;
	}
	
	// methode voor na te gaan of je de eerste node bent van het netwerk
	public int amIFirst(){
		if(listOfNodes.size()==1){
			return 1;
		}else{
			return 0;
		}
	}
	
	//return the neigbours of a node specified by hashNode
	public int[] getNeigbours(int hashNode){
		int[] neighbours = new int[1]; //neigbours[0] = previous, 1 = next
		
		//give hash of the first node < given hash
		if(listOfNodes.floorEntry(hashNode-1)==null){ //if there is no lowest node, return the highest node
			neighbours[0] = listOfNodes.lastEntry().getValue().getHash();
		}else{
			neighbours[0] = listOfNodes.floorEntry(hashNode-1).getValue().getHash(); //give the hash of the node below 
		}
		
		if(listOfNodes.higherEntry(hashNode) == null){ //if there is no higher hash
			neighbours[1] = listOfNodes.firstEntry().getValue().getHash();
		}else{
			neighbours[1] = listOfNodes.higherEntry(hashNode).getValue().getHash();
		}
			
		return neighbours;
	}

	//delete a node specified by hashNode
	//this method should be invoked by a different node when it detects the failing of another node
	public void deleteNode(int hashNode){
		nodeLijst.removeNode(hashNode);
	}

	public String getIP(int hash){
		return listOfNodes.get(hash).getIpAdress();	
	}

}