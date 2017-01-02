package SystemY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;

public class NamingServer extends UnicastRemoteObject implements ClientToNamingServerInterface {
	private static final long serialVersionUID = 1L;
	Nodelijst nodeLijst;
	private Thread multicastReceiverThread;
	TreeMap<Integer, NodeNamingServer> listOfNodes = new TreeMap<>();

	//Namingserver houdt enkel een lijst van nodes bij met hierin de naam, de hash en het ipadres. Niks meer!
	@SuppressWarnings("unused")
	public NamingServer() throws RemoteException{
		super();
		nodeLijst = new Nodelijst();
		multicastReceiverThread = new Thread(new MulticastReceiverThread(nodeLijst));
		multicastReceiverThread.start();
		
		MulticastSenderNamingServer multicastSenderNamingServer = new MulticastSenderNamingServer();
		
		nodeLijst.writeJSON();
		//nodeLijst.readJSON();
		
		listOfNodes = nodeLijst.getListOfNodes();	
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
		//if node is first
		if(listOfNodes.size()==1){
			return 1;
		//if node is second
		}else if(listOfNodes.size()==2){
			return 2;
		//if node is after that
		}else{
			return 0;
		}
	}
	
	//return the neigbours of a node specified by hashNode
	public int[] getNeigbours(int hashNode){
		int[] neighbours = new int[2]; //neigbours[0] = previous, 1 = next
		
		neighbours[0] = getPrevious(hashNode);
		neighbours[1] = getNext(hashNode);
		return neighbours;
	}

	//delete a node specified by hashNode
	//this method should be invoked by a different node when it detects the failing of another node
	public void deleteNode(int hashNode){
		nodeLijst.removeNode(hashNode);
	}

	//returns the ip for the node with hash: hashNode
	public String getIP(int hashNode){
		String ip="";
		listOfNodes = nodeLijst.getListOfNodes();	
		System.out.println(hashNode);
		try{
			ip = listOfNodes.get(hashNode).getIpAdress();
		}catch(NullPointerException e){
			System.out.println("An ip was requested for a node that doesn't exist. ");
			System.out.println("The queeried hash was: " + hashNode);
			e.printStackTrace();
		}
		
		return ip;	
	}
	
	//get hash of a node by it's ip
	public int getHash(String ip){
		int hash = -1;
		for(NodeNamingServer node : listOfNodes.values()){
			//return the hash if the ip exists
			if(node.getIpAdress() == ip){
				hash = node.getHash();
			}
		}
		
		return hash;
	}

	@SuppressWarnings("unused")
	public void activateAgent(int hashOfNode){
		int next = getNext(hashOfNode);
		//listOfNodes.get(next).getInterface().activateAgent();
	}

	//method to return the next node
	@SuppressWarnings("unused")
	private int getNext(int hashOfNode) {
		int next;
		if(listOfNodes.higherEntry(hashOfNode) == null){ //if there is no higher hash
			return next = listOfNodes.firstEntry().getValue().getHash();
		}else{
			return next = listOfNodes.higherEntry(hashOfNode).getValue().getHash();
		}		
	}
	
	@SuppressWarnings("unused")
	private int getPrevious(int hashNode) {
		int previous;
		//give hash of the first node < given hash
		if(listOfNodes.floorEntry(hashNode-1)==null){ //if there is no lowest node, return the highest node
			return previous = listOfNodes.lastEntry().getValue().getHash();
		}else{
			return previous = listOfNodes.floorEntry(hashNode-1).getValue().getHash(); //give the hash of the node below 
		}
	}
}