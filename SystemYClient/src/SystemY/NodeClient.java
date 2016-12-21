package SystemY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class NodeClient extends UnicastRemoteObject implements clientToClientInterface, NamingServerToClientInterface{
	private static final long serialVersionUID = 1L;
	private TreeMap<String, Integer> fileList = new TreeMap<>(); // filename, hash
	private BestandenLijst bestandenLijst = new BestandenLijst();
	private int nextNode; //hash for next node
	private int previousNode; //hash for previous node
	private int ownHash; //hash of this node
	private Thread multicastReceiverThreadClient; //threaed to receive multicasts by other nodes
	ClientToNamingServerInterface ni; 
	String serverIP;
	volatile boolean goAhead = false; //the thread should wait untill the interface has been made before communicating via it
	Thread agent; //our agent
	TreeMap<String, Boolean> allFiles = new TreeMap<>(); //name, isLocked; has all files in the system provided by the agent
	HashSet<String> owned = new HashSet<>(); //contains all files this node is the owner of
	HashSet<String> locked = new HashSet<>(); //contains all files that should be locked
	HashSet<String> unLocked = new HashSet<>(); //contains all files that should be unlocked
	private TCP tcp = new TCP();
	Serializer s = new Serializer(); //class to serialize our agent
	private boolean first = true; //to know if the agent should be made
	
	public static void main(String args[]) {
		try {
			new NodeClient();
		} catch (RemoteException e) {
			System.out.println("Couldn't create Client");
			e.printStackTrace();
		}
	}

	public NodeClient() throws RemoteException{
		String nameNode = readConsoleName();
		multicastReceiverThreadClient = new Thread(
				new MulticastReceiverThreadClient(ownHash, this));

		startUp(this, nameNode);

		System.out.println("This nodes hash is: "+ownHash);

		// infinite while loop for the gui
		while (true) 
			consoleGUI(); 
	}
	
	//start the consolegui
	private void consoleGUI() throws RemoteException {
		System.out.println("What do you want to do?");
		System.out.println("[1] List local files");
		System.out.println("[2] Look for file");
		System.out.println("[3] Print neighbours");
		System.out.println("[4] Ask Location");
		System.out.println("[5] Surprise");
		System.out.println("[6] Load fole");
		System.out.println("[9] Exit");

		int input = Integer.parseInt(readConsole());
		System.out.println("Your choice was: " + input);

		switch (input) {
		case 1:
			checkLocalFiles(new File("C:/TEMP"));
			System.out.println("Local Files are: " + fileList);
			break;

		case 2:
			System.out.println("Enter file to look for: ");
			String location = getFileLocation(readConsole());
			System.out.println("The location is: " + location);
			break;

		case 3:
			refreshNeighbours();
			System.out.println("Previous hash: "+previousNode);
			System.out.println("Next hash: "+nextNode);
			break;
			
		case 4:
			try {
				System.out.println("Enter file to ask for: ");
				String Filelocation = ni.askLocation((readConsole()));
				System.out.println("The location is: " + Filelocation);
			} catch (RemoteException e) {
				System.out.println("Couldn't get location. ");
				e.printStackTrace();
			}
			break;	
			
		case 5:
			System.out.println("------------");
			System.out.println("|Send Nudes|");
			System.out.println("------------");
			break;
		
		case 6:
			System.out.println("FileName: ");
			String fileName = System.console().readLine();
			loadFile(fileName);
			
		case 666:
			System.out.println("------------------------");
			System.out.println("The number of the Beast!");
			System.out.println("------------------------");
			break;
			
		case 9:
			shutdown();
			break;
		}
	}
	
	private void startUp(NodeClient nodeClient, String nameNode) {
		try {
			//make registry to establish communication of server to client
			String bindLocation = "Client"+nameNode;
			Registry reg = LocateRegistry.createRegistry(1200);
			reg.bind(bindLocation, nodeClient);
			System.out.println("Namingserver registry is ready at: " + bindLocation);

			new MulticastSender(ownHash, nameNode);
			multicastReceiverThreadClient.start();
			while(serverIP == null){
				//wait until we know the servers ip
				TimeUnit.SECONDS.sleep(2);
			}
			
			//make interface for communication with namingserver
			String name = "//" + serverIP + ":1099/NamingServer";
			ni = (ClientToNamingServerInterface) Naming.lookup(name);
			ownHash = calculateHash(nameNode);
			System.out.println(ni.getIP(ownHash));
			
			//the interface has been made so the multicastreceiverthread can continue
			goAhead = true;

			// make registry to establish RMI between nodes
			bindLocation = "nodeClient";
			reg = LocateRegistry.createRegistry(1100);
			reg.bind(bindLocation, nodeClient);
			System.out.println("ClientRegistery is ready at: " + bindLocation);

			loadFilesStartUp();
			getReplicationNewNode();
			
		} catch (MalformedURLException | RemoteException | NotBoundException | UnsupportedEncodingException | InterruptedException e) {
			e.printStackTrace();
		} catch(AlreadyBoundException e){
			System.out.println("Registry already in use");
			e.printStackTrace();
		}
	}

	//puts all local files in fileList: name, hash
	private void checkLocalFiles(File dir) {
		File[] filesList = dir.listFiles();
		for (File f : filesList) {
			fileList.put(f.getName(), calculateHash(f.getName()));
		}
	}
	
	// toevoegen van één bestand van de lokale folder (filename + extentie)
	private void loadFile(String fileName) throws NumberFormatException, RemoteException
	{
		File dir = new File("C:/TEMP");
		int hashReplicationNode = Integer.valueOf(ni.askLocation(fileName));
		if(hashReplicationNode == ownHash)
		{
			hashReplicationNode = previousNode;
		}
		bestandenLijst.addBestand(fileName,dir.toString(),ownHash,hashReplicationNode);
		sendFile(bestandenLijst.getBestand(fileName),hashReplicationNode);
	}
	
	//returns the location where a file should be located and returns the ip
	private String getFileLocation(String fileName) {
		String location = "";
		try {
			location = ni.getFileLocation(fileName);
		} catch (RemoteException e) {
			System.err.println("NodeClient couldn't fetch filelocation: " + e.getMessage());
			e.printStackTrace();
		}
		return location;
	}
	
	//calculate hash for a certain name
	public int calculateHash(String nodeNaam) {
		int tempHash = nodeNaam.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		return tempHash;
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
	
	//method to call when the node wants to shut down
	private void shutdown() {
		//if you're the first (and this case last) node, you shouldn't notify yourself)
		try {
			if(ni.amIFirst()!=1){
				//Tell nextNode his previous, is what yout previous was
				// if the hash is -1, the hash is not changed in the recipient node
				notifyNext(previousNode, -1, nextNode);
				// Tell previousNode his next, is what your next was
				notifyPrevious(-1, nextNode, previousNode);
			}
		} catch (RemoteException e1) {
			System.out.println("Can't get amIFirst");
			e1.printStackTrace();
		}	
		try {
			ni.deleteNode(ownHash);
		} catch (RemoteException e) {
			System.out.println("Couldn't delete this node from namingserver.");
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	//when a connectionexception occurs when communicting with another node this method should be invoked
	//ask nameserver next and previous of failing node, update these nodes with gained info
	private void failure(int failingHash){
		int[] neighbours = new int[2];
		// Get next node and previous node using the current nodes hash number
		try {
			neighbours = ni.getNeigbours(failingHash);// return previous 0 end next 1 neighbour
			ni.deleteNode(failingHash); //delete te node from the nodelist of the server
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		//send the next node the hash of te new previous node
		notifyNext(neighbours[0],-1,neighbours[1]);
		
		//send the previous node the hash of the new next node
		notifyPrevious(-1, neighbours[1], neighbours[0]);
	}

	// Notify next node via RMI
	public void notifyNext(int ownHash /*previous hash*/, int nextNodeHash /*next hash*/, int hash /*of node to notify*/) {
		// Notifies the node (hash) that his previous node is this node and his next node is this node's former next node
		
		try{
			makeCTCI(hash).getNotified(ownHash, nextNodeHash);
		} catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(hash); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		}
	}

	// notify previous node via RMI
	public void notifyPrevious(int previousNodeHash, int ownHash, int hash) {
		// Notifies the new node that his previous node is this node's former
		// previous node and his next node is this node
		
		try {
			makeCTCI(hash).getNotified(hash, ownHash);
		} catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(hash); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		}
	}

	// Get notified of previous and next node via RMI
	public void getNotified(int previousHash, int nextHash) {
		if (previousHash != -1) {
			previousNode = previousHash;
		}
		if (nextHash != -1) {
			nextNode = nextHash;
		}
		System.out.println("Vorige node: " + previousNode);
		System.out.println("Volgende node: " + nextNode);
	}
	
	//when this node starts ask for user input for the name
	private String readConsoleName() {
		String naam = null;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Naam Node: ");
			naam = br.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (naam == "\n") {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		ownHash = calculateHash(naam);
		System.out.println(ownHash);
		return naam;
	}
	
	//for the server to set his ip on this node
	public void setServerIP(String IP) {serverIP = IP;}

	//return the list of files the node has
	public TreeMap<String, Integer> getFileList(){
		//make sure we have the latest files
		checkLocalFiles(new File("C:/TEMP"));
		return fileList;
	}

	//for the thread to know when an RMI has been set up with the namingserver
	public boolean getGoAhead() {return goAhead;}
	
	public void setNeighbours(int previous, int next){
		previousNode = previous;
		nextNode = next;
	}

	public void setNext(int hash) {nextNode=hash;}

	public void setPrevious(int hash) {previousNode=hash;}

	public int getPreviousNode() {return previousNode;}

	public int getNextNode() {return nextNode;}
	
	//make sure your neighbours are correct,
	//Should be invoke everytime you try to connect with neighbours
	public void refreshNeighbours() {
		int[] neighbours = new int[2];
		try {
			neighbours=ni.getNeigbours(ownHash);
			previousNode = neighbours[0];
			nextNode = neighbours[1];
		} catch (RemoteException e) {
			System.out.println("Couldn't refresh neighbours");
			e.printStackTrace();
		}
		
	}
	
	public Integer getOwnHash(){return ownHash;}
	
	public void setAllFiles(TreeMap<String, Boolean> allFiles){this.allFiles = allFiles;}
	
	public void setLocked(HashSet<String> locked2){this.locked = locked2;}
	
	public void sendFile(Bestand fileToSend, int recieverHash){
		String ip="";
		String pathFile = fileToSend.getFullPath();
		int fileSize = ((int) fileToSend.getFile().length())+1000;
		Random ran = new Random();
		int fileID = ran.nextInt(10000);
		try {
			ip = ni.getIP(recieverHash);
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			failure(recieverHash); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		
		try {
			clientToClientInterface ctci = (clientToClientInterface) Naming.lookup("//" + ip + ":1100/nodeClient");
			ctci.getFile(InetAddress.getLocalHost(),pathFile,fileToSend.getNaam(),fileToSend.getPath(),fileToSend.getHashOwner(),fileToSend.getHashReplicationNode(),fileSize,fileID);
			tcp.SendFile(ctci, fileToSend.getFile(), InetAddress.getByName(ip), fileID);
			
		} catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(recieverHash); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Registry not bound");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getFile(InetAddress IPSource, String pathFile, String naamBestand, String pathBestand, int hashOwner, int hashReplicationNode, int fileSize,int fileID) {
		String ip = IPSource.toString();
		try {
			clientToClientInterface ctci = (clientToClientInterface) Naming.lookup("//" + ip + ":1100/nodeClient");
			tcp.ReceiveFile(ctci, pathFile, fileSize, fileID);
			bestandenLijst.addBestand(naamBestand, pathBestand, hashOwner, hashReplicationNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setTCPSendMessage(int message){
		tcp.setSendMessage(message);
	}
	
	public void setTCPReceiveMessage(int message){
		tcp.setReceiveMessage(message);
	}
	
	// toevoegen van alle bestanden in lokale folder
	private void loadFilesStartUp() throws NumberFormatException, RemoteException
	{
		File dir = new File("C:/TEMP");
		for (File f : dir.listFiles()) {
			int hashReplicationNode = Integer.valueOf(ni.askLocation(f.getName()));
			if(hashReplicationNode == ownHash)
			{
				hashReplicationNode = previousNode;
			}
			bestandenLijst.addBestand(f.getName(),dir.toString(),ownHash,hashReplicationNode);
			sendFile(bestandenLijst.getBestand(f.getName()),hashReplicationNode);
		}
	}
	
	// replicatie van van bestanden met grotere hash dan deze node en met kleinere hash vorige node
	public void getReplicationNewNode(){
		String ip="";
		try {
			ip = ni.getIP(previousNode);
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			failure(previousNode); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		try {
			clientToClientInterface ctci = (clientToClientInterface) Naming.lookup("//" + ip + ":1100/nodeClient");
			ctci.sendReplicationToNewNode(ownHash);
			
		}catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(previousNode); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (NotBoundException e) {
			System.out.println("Registry not bound");
			e.printStackTrace();
		}
	}
	public void sendReplicationToNewNode(int hashNewNode){
		ArrayList<Bestand> temp = bestandenLijst.getFilesWithSmallerHash(hashNewNode);
		int size = temp.size();
		for (int i=0; size>i; i++){
			sendFile(temp.get(0),hashNewNode);
		}
	}
	
	public void shutDownReplication(){
		int size = bestandenLijst.getSize();
		for(int i=0; size>i; i++){
			if(bestandenLijst.getIndex(i).getHashReplicationNode()== ownHash){
				bestandenLijst.getIndex(i).setReplicationNode(previousNode);
				sendFile(bestandenLijst.getIndex(i),previousNode);
			}
			else if (bestandenLijst.getIndex(i).getHashOwner()== ownHash){
				
			}
		}
	}
	

	public void setOwned(HashSet<String> owned) {this.owned = owned;}

	public HashSet<String> getUnlocked() {return unLocked;}

	//method called on this client, that the agent should be started
	public void activateAgent(Thread agent) throws RemoteException {
		//if the agent already exists
		if(agent != null){
			this.agent = agent;
		//the agent should be made
		}else{
			this.agent = new Thread(new Agent(this));
		}
	}
	
	public void nextAgent(){
		int nextHash = getNextNode();
		
		try {
			makeCTCI(nextHash).activateAgent(agent);
		} catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(nextHash); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		}
		
	}
	
	//makes an interface for the specified hash for RMI between nodes
	public clientToClientInterface makeCTCI(int hash){
		clientToClientInterface ctci = null;
		
		String ip="";
		try {
			ip = ni.getIP(hash);
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			failure(hash); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		
		try {
			ctci = (clientToClientInterface) Naming.lookup("//" + ip + ":1100/nodeClient");
		}catch(RemoteException e){
			System.out.println("Couldn't make interface");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Reg was not bound");
			e.printStackTrace();
		}
		
		return ctci;
	}
	
}
