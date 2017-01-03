package SystemY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.swing.text.html.HTMLDocument.Iterator;

public class NodeClient extends UnicastRemoteObject implements ClientToClientInterface, NamingServerToClientInterface{
	private static final long serialVersionUID = 1L;
	//private TreeMap<String, Integer> fileList = new TreeMap<>(); // filename, hash
	private FileManager fileManager;
	private int nextNode; //hash for next node
	private int previousNode; //hash for previous node
	private int ownHash; //hash of this node
	private Thread multicastReceiverThreadClient; //threaed to receive multicasts by other nodes
	ClientToNamingServerInterface ni; 
	String serverIP;
	private String name;
	volatile boolean goAhead = false; //the thread should wait untill the interface has been made before communicating via it
	Thread agent; //our agent
	//TreeMap<String, Boolean> allFiles = new TreeMap<>(); //name, isLocked; has all files in the system provided by the agent
	//HashSet<String> owned = new HashSet<>(); //contains all files this node is the owner of
	//HashSet<String> locked = new HashSet<>(); //contains all files that should be locked
	//HashSet<String> unLocked = new HashSet<>(); //contains all files that should be unlocked
	private TCP tcp;
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
		name = readConsoleName();
		multicastReceiverThreadClient = new Thread(
				new MulticastReceiverThreadClient(ownHash, this));

		GUI gui = new GUI(this);

		startUp();

		System.out.println("This nodes hash is: "+ownHash);
		

		
		// infinite while loop for the gui
		while (true){
			consoleGUI(); 
		}
			
	}
	
	
	
	//start the consolegui
	private void consoleGUI() throws RemoteException {
		System.out.println("What do you want to do?");
		System.out.println("[1] List local files");
		System.out.println("[2] Look for file");
		System.out.println("[3] Print neighbours");
		System.out.println("[4] Ask Location");
		System.out.println("[5] Surprise");
		System.out.println("[9] Exit");

		int input = Integer.parseInt(readConsole());
		System.out.println("Your choice was: " + input);

		switch (input) {
		case 1:
			System.out.println("Printing files: ");
			fileManager.printAllFiles();
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
	
	private void startUp() {
		try {
			//make registry to establish communication of server to client
			String bindLocation = "Client"+name;
			Registry reg = LocateRegistry.createRegistry(1200);
			reg.bind(bindLocation, this);
			System.out.println("Registry is ready at: " + bindLocation);

			new MulticastSender(ownHash, name);
			multicastReceiverThreadClient.start();
			while(serverIP == null){
				//wait until we know the servers ip
				TimeUnit.SECONDS.sleep(2);
			}
			
			//make interface for communication with namingserver
			String name = "//" + serverIP + ":1099/NamingServer";
			ni = (ClientToNamingServerInterface) Naming.lookup(name);
			ownHash = calculateHash(name);
			
			//get our neighbours
			refreshNeighbours();
			
			//the interface has been made so the multicastreceiverthread can continue
			goAhead = true;

			// make registry to establish RMI between nodes
			bindLocation = "nodeClient";
			reg = LocateRegistry.createRegistry(1100);
			reg.bind(bindLocation, this);
			System.out.println("ClientRegistery is ready at: " + bindLocation);
			
			//starting the tcp socket
			tcp = new TCP(this);
			//start with loading files and executing replication
			fileManager = new FileManager(this); //this automatically loads the local files and start the replication
			
			
			
		} catch (MalformedURLException | RemoteException | NotBoundException | UnsupportedEncodingException | InterruptedException e) {
			e.printStackTrace();
		} catch(AlreadyBoundException e){
			System.out.println("Registry already in use");
			e.printStackTrace();
		}
	}

	
	
	//returns the location where a file should be located and returns the ip
	public String getFileLocation(String fileName) {
		String location = "";
		try {
			location = ni.getFileLocation(fileName);
		} catch (RemoteException e) {
			System.err.println("NodeClient couldn't fetch filelocation: " + e.getMessage());
			e.printStackTrace();
		}
		return location;
	}
	
	public int getHashLocation(String fileName) {
		int hash = -1;
		try {
			hash = ni.askHashLocation(fileName);
		} catch (RemoteException e) {
			System.err.println("NodeClient couldn't fetch filelocation: " + e.getMessage());
			e.printStackTrace();
		}
		return hash;
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
	
	//method to call when the node wants to shut down (2 steps: 1: replicate/move files, 2: remove node from server
	private void shutdown() {
		//STEP 1: replicate/move files
		
		
		
		
		
		
		
		
		
		
		
		
		//STEP 2: remove node from server
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
	public void failure(int failingHash){
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


	//for the thread to know when an RMI has been set up with the namingserver
	public boolean getGoAhead() {return goAhead;}
	
	public void setNeighbours(int previous, int next){
		previousNode = previous;
		nextNode = next;
	}

	public void setNext(int hash) {nextNode=hash;}

	public void setPrevious(int hash) {previousNode=hash;}

	public int getPreviousNode() {refreshNeighbours(); return previousNode;}

	public int getNextNode() {refreshNeighbours(); return nextNode;}
	
	//make sure your neighbours are correct,
	//Should be invoked everytime you try to connect with neighbours
	public void refreshNeighbours() {
		int[] neighbours = new int[2];
		try {
			neighbours=ni.getNeigbours(ownHash);
			previousNode = neighbours[0];
			nextNode = neighbours[1];
		} catch (RemoteException e) {
			System.out.println("Couldn't refresh neighbours");
			e.printStackTrace();
		} catch (NullPointerException e){
			//error in hash
			e.printStackTrace();
		}
		
	}
	
	public Integer getOwnHash(){return ownHash;}
	
	public void setAllFiles(TreeMap<String, Boolean> allFiles){this.allFiles = allFiles;}
	
	public void setLocked(HashSet<String> locked2){this.locked = locked2;}
	
	//this method sets a file send request in this node and a file receive request in the receiving node, the TCP class handles the reqeusts
	public void sendFile(Bestand fileToSend, int receiverHash){
		
		String ip="";
		Random ran = new Random();
		int fileID = ran.nextInt(20000); //The file ID is used in the file receive and send requests, they are compared to know if they are transmitting the right file
		try {
			ip = ni.getIP(receiverHash);
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			failure(receiverHash); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		int fileSize = ((int) fileToSend.getFile().length())+1000;
		
		
		try {
			SendFileRequest sendRequest = new SendFileRequest(fileToSend.getFile(),InetAddress.getByName(ip),fileID,receiverHash);
			ReceiveFileRequest receiveRequest = new ReceiveFileRequest(InetAddress.getLocalHost(),fileToSend.getName(), fileToSend.getFullPath(), fileSize, fileID, fileToSend.getHashOwner(), fileToSend.getHashReplicationNode());
			ClientToClientInterface ctci = makeCTCI(receiverHash);
			ctci.setReceiveRequest(receiveRequest);
			ctci = null;
			tcp.addSendRequest(sendRequest);
			
		} catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(receiverHash); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//sets a receive request in the tcp receive file buffer (used by the file sender via RMI)
	public void setReceiveRequest(ReceiveFileRequest request) throws RemoteException{
		try {
			tcp.addReceiveRequest(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//checks if TCP is ready to receive a file with the following file ID
	public int checkReceiveAvailable(int fileID) throws RemoteException{
		return tcp.checkReceiveAvailable(fileID);
	}
	
	
	
	// toevoegen van alle bestanden in lokale folder
	private void loadFilesStartUp() throws NumberFormatException, RemoteException{
		File dir = new File("C:/TEMP/");
		if (ni.amIFirst()!=1){
			for (File f : dir.listFiles()) {
				int hashReplicationNode = ni.getHash(ni.askLocation(f.getName()));
				if(hashReplicationNode == ownHash){
					hashReplicationNode = getPreviousNode();
				}
				String name = f.getName();
				fileManager.addBestand(name ,dir.toString(),ownHash,hashReplicationNode);
				System.out.println("At loadFilesStartUp: trying to send file "+fileManager.getBestand(name).getName()+" to hash "+hashReplicationNode);
				sendFile(fileManager.getBestand(name),hashReplicationNode);
			}
		}
		fileManager.listAllFiles();
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
			ClientToClientInterface ctci = makeCTCI(previousNode);
			ctci.sendReplicationToNewNode(ownHash);
			
		}catch (RemoteException e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			failure(previousNode); //when we can't connect to the node we assume it failed.
			e.printStackTrace();
		}
		
	}
	public void sendReplicationToNewNode(int hashNewNode) throws RemoteException {
		ArrayList<Bestand> temp = fileManager.getFilesWithSmallerHash(hashNewNode);
		if (temp!=null){
			int size = temp.size();
			for (int i=0; size>i; i++){
				sendFile(temp.get(0),hashNewNode);
			}
		}
	}
	
	public void shutDownReplication(){
		int size = fileManager.getSize();
		for(int i=0; size>i; i++){
			if(fileManager.getIndex(i).getHashReplicationNode()== ownHash){ //als dit deze node replicatienode is
				fileManager.getIndex(i).setReplicationNode(previousNode);
				sendFile(fileManager.getIndex(i),previousNode);
			}
			else if (fileManager.getIndex(i).getHashOwner()== ownHash){ //als deze node eigenaar is
				
			}
		}
	}
	

	public void setOwned(HashSet<String> owned) {this.owned = owned;}
	
	//Return true if file exists in owned

	public boolean checkOwned(String fileName, HashSet<String> owned){
		
		for (String s : owned) {
		    if(s == fileName ){
		    	return true;
		    }
		}
		return false;
	}
	
	

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
	public ClientToClientInterface makeCTCI(int hash){
		ClientToClientInterface ctci = null;
		
		String ip="";
		try {
			ip = ni.getIP(hash);
		} catch (RemoteException e1) {
			System.out.println("Couldn't fetch IP from Namingserver");
			failure(hash); //when we can't fetch te ip it's likely the node shut down unexpectedly
			e1.printStackTrace();
		}
		
		try {
			ctci = (ClientToClientInterface) Naming.lookup("//" + ip + ":1100/nodeClient");
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
	
	//makes an interface for the specified hash for RMI between nodes
	public ClientToClientInterface makeCTCIByName(String nodeName){
		ClientToClientInterface ctci = null;
		int hash;
		try {
			hash = ni.getHashByName(nodeName);
			ctci = makeCTCI(hash);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return ctci;
	}
		
				
	public FileManager getFileManager(){
		return fileManager;
	}
	
	public ClientToNamingServerInterface getNI(){
		return ni;
	}

	public String getName(){
		return name;
	}
	
}
