package SystemY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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

import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument.Iterator;

public class NodeClient extends UnicastRemoteObject implements ClientToClientInterface, NamingServerToClientInterface{
	private static final long serialVersionUID = 1L;
	private FileManager fileManager;
	private int nextNode; //hash for next node
	private int previousNode; //hash for previous node
	private int ownHash; //hash of this node
	private Thread multicastReceiverThreadClient; //threaed to receive multicasts by other nodes
	String serverIP; //the ip of the namingserver
	private String name; // this nodes name
	volatile boolean goAhead = false; //the thread should wait untill the interface has been made before communicating via it
	private TCP tcp; //the tcp socket
	
	public static void main(String args[]) {
		try {
			new NodeClient();
		} catch (RemoteException e) {
			System.out.println("Couldn't create Client");
			e.printStackTrace();
		}
	}

	public NodeClient() throws RemoteException{
		//INSTRUCTIES VOOR GUIMAKER THIJS
		
		//stap 1: maak een GUI waar je de naam van de node kan ingeven, nu gebeurd door de methode hieronder:
		//new MenuGUI(this);
		//name = readConsoleName();
		//stap 2: nadat de naam in NodeClient is ingegeven kan je beginnen met hetopstarten van de node, methode "startUp();" 
		//			laat ondertussen op de gui verschijnen dat we aan het opstarten zijn.
		//startUp();
		//stap 3: start de "hoofdgui" (als startup compleet is) met de lijst van de bestanden en refresh deze gui om de zoveel (200 ongeveer) milliseconden

		//infinite while loop for the gui
		while (true){
			consoleGUI();
		}
			
	}
	
	
	
	//start the consolegui
	private void consoleGUI() throws RemoteException {
		String name;
		System.out.println("What do you want to do?");
		System.out.println("[1] List local files");
		System.out.println("[2] Open file: ");
		System.out.println("[3] Print neighbours");
		System.out.println("[4] List all the files in the network: ");
		System.out.println("[5] List all owner files from this node.");
		System.out.println("[6] Delete a file locally.");
		System.out.println("[5] Delete a file from the network (hard delete).");
		System.out.println("[9] Exit");
		
		String inputString = readConsole();
		int input = stringToInt(inputString);

		System.out.println("Your choice was: " + input);

		switch (input) {
		case 1:
			System.out.println("Printing files: ");
			fileManager.printAllFiles();
			break;

		case 2:
			System.out.println("Enter file to look for: ");
			name = readConsole();
			fileManager.openFile(name);
			System.out.println("");
			break;

		case 3:
			refreshNeighbours();
			System.out.println("");
			System.out.println("PRINTING NEIGHBOURS: ");
			System.out.println("Previous hash: "+previousNode);
			System.out.println("Next hash: "+nextNode);
			System.out.println("This nodes hash is: "+ownHash);
			break;
			
		case 4:
			System.out.println("");
			System.out.println("Listing all files in the network: ");
			System.out.println("");
			fileManager.printAllFilesInTheNetwork();
			System.out.println("");
			break;	
			
		case 5:
			System.out.println("");
			System.out.println("Listing all ownerfiles from this node: ");
			System.out.println("");
			fileManager.getAllNodeOwnedFiles().printAllFiles();
			System.out.println("");
			break;
			
		case 6: //deleting a file locally
			System.out.println("Enter the name of the file you want to remove locally: ");
			name = readConsole();
			fileManager.deleteFileLocally(name);
			System.out.println("");
			break;
			
		case 7: //deleting file from the whole network, even locally files
			System.out.println("Enter the name of the file you want to remove from the network.");
			name = readConsole();
			fileManager.deleteFileFromNetwork(name);
			System.out.println("");
			break;
		
		case 258:
			System.out.println("Your input wasn't a number, try again: ");
			
		case 666:
			System.out.println("------------------------");
			System.out.println("The number of the Beast!");
			System.out.println("------------------------");
			break;
			
		case 9:
			shutdown();
			break;
		
		default:
			System.out.println("Wrong number, try again: ");
			break;
		}
	}
	
	private int stringToInt(String input){
		int output;
		try { 
	        output = Integer.parseInt(input); 
	    } catch(NumberFormatException e) { 
	        output = 258;
	    } catch(NullPointerException e) {
	        output = 258;
	    } catch(ClassCastException e) {
	    	output = 258;
	    }
		return output;
	}
	
	public void startUp() {
		try {
			multicastReceiverThreadClient = new Thread(
					new MulticastReceiverThreadClient(ownHash, this));
	
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
			fileManager = new FileManager(this); 
			fileManager.loadLocalFiles(); //this automatically loads the local files and start the replication
			fileManager.updateOwnedFiles();
			
			ClientToNamingServerInterface ni = makeNI();
			int numberOfNodes = ni.amIFirst();
			ni = null;
			
			if (numberOfNodes != 1){
				checkReplicationPreviousNode(); //this checks the replication from the previous node
				System.out.println("Replicating files to other nodes...");
				long sleepTime = 100;
				while(tcp.threadRunning()){
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if (numberOfNodes == 2){
				startUpAgent();
			}
			
			fileManager.startCheckLocalFilesThread();
			
			System.out.println("Startup Completed");
		} catch (RemoteException | UnsupportedEncodingException | InterruptedException e) {
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
			ClientToNamingServerInterface ni = makeNI();
			location = ni.getNameFileLocation(fileName);
			ni = null;
		} catch (RemoteException e) {
			System.err.println("NodeClient couldn't fetch filelocation: " + e.getMessage());
			e.printStackTrace();
		}
		return location;
	}
	
	public int getHashLocation(String fileName) {
		int hash = -1;
		try {
			ClientToNamingServerInterface ni = makeNI();
			hash = ni.getHashFileLocation(fileName);
			ni = null;
			//System.out.println("There was a hash requested to the server for filename "+fileName+", this hash was given: "+hash);
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
	public void shutdown() {
		//STEP 1: replicate/move files
		fileManager.stopCheckLocalFilesThread();
		fileManager.shutDownReplication();

		
		//STEP 2: remove node from server
		//if you're the first (and this case last) node, you shouldn't notify yourself)
		ClientToNamingServerInterface ni = makeNI();
		try {
			if(ni.amIFirst()!=1){
				//Tell nextNode his previous, is what yout previous was
				// if the hash is -1, the hash is not changed in the recipient node
				notifyNext(previousNode, -1, nextNode);
				// Tell previousNode his next, is what your next was
				notifyPrevious(-1, nextNode, previousNode);
				ni = null;
			}
		} catch (RemoteException e1) {
			System.out.println("Can't get amIFirst");
			ni = null;
			e1.printStackTrace();
		}	
		ni = makeNI();
		try {
			ni.deleteNode(ownHash);
			ni = null;
		} catch (RemoteException e) {
			System.out.println("Couldn't delete this node from namingserver.");
			ni = null;
			e.printStackTrace();
		}
		System.out.println("The node has succesfully shut down.");
		System.exit(0);
	}
	
	//when a connectionexception occurs when communicting with another node this method should be invoked
	//ask nameserver next and previous of failing node, update these nodes with gained info
	public void failure(int failingHash){
		int[] neighbours = new int[2];
		// Get next node and previous node using the current nodes hash number
		try {
			ClientToNamingServerInterface ni = makeNI();
			neighbours = ni.getNeigbours(failingHash);// return previous 0 end next 1 neighbour
			ni.deleteNode(failingHash); //delete te node from the nodelist of the server
			ni = null;
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
	
	public void setName(String name){
		this.name = name;
	}
	//when this node starts ask for user input for the name
	public String readConsoleName() {
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
	
	public int getPreviousPreviousNode(){
		int previousPreviousHash = -1;
		ClientToClientInterface ctci = makeCTCI(getPreviousNode());
		try {
			previousPreviousHash = ctci.getPreviousNode();
		} catch (RemoteException e) {
			failure(previousNode);
			e.printStackTrace();
		}
		return previousPreviousHash;
	}

	public int getNextNode() {refreshNeighbours(); return nextNode;}
	
	//make sure your neighbours are correct,
	//Should be invoked everytime you try to connect with neighbours
	public void refreshNeighbours() {
		int[] neighbours = new int[2];
		try {
			ClientToNamingServerInterface ni = makeNI();
			neighbours=ni.getNeigbours(ownHash);
			ni = null;
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
	
	//public void setAllFiles(TreeMap<String, Boolean> allFiles){this.allFiles = allFiles;}
	
	//public void setLocked(HashSet<String> locked2){this.locked = locked2;}
	
	
	//sets a receive request in the tcp receive file buffer (used by the file sender via RMI)
	public void setReceiveRequest(TCPReceiveFileRequest request) throws RemoteException{
		try {
			tcp.addReceiveRequest(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//checks if TCP is ready to receive a file with the following file ID
	public int checkReceiveAvailable(int fileID) throws RemoteException{
		int message = tcp.checkReceiveAvailable(fileID);
		return message;
	}
	
	public void checkReplicationPreviousNode(){
		refreshNeighbours();
		ClientToClientInterface ctci = makeCTCI(previousNode);
		try {
			ctci.checkReplicationFromNextNode();
		} catch (RemoteException e) {
			failure(previousNode);
			e.printStackTrace();
		}
	}
	
	public void checkReplicationFromNextNode(){
		fileManager.checkReplication();
	}
	
	
	
	//makes an interface for the specified hash for RMI between nodes
	public ClientToClientInterface makeCTCI(int hash){
		ClientToClientInterface ctci = null;
		
		String ip="";
		try {
			ClientToNamingServerInterface ni = makeNI();
			ip = ni.getIPNode(hash);
			ni = null;
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
	public ClientToClientInterface makeCTCI(String nodeName){
		ClientToClientInterface ctci = null;
		int hash;
		try {
			ClientToNamingServerInterface ni = makeNI();
			hash = ni.getHashNodeByNodeName(nodeName);
			ni = null;
			ctci = makeCTCI(hash);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return ctci;
	}
		
				
	public FileManager getFileManager(){
		return fileManager;
	}
	
	public ClientToNamingServerInterface makeNI(){
		
		ClientToNamingServerInterface ni = null;
		String name = "//" + serverIP + ":1099/NamingServer";
		try {
			ni = (ClientToNamingServerInterface) Naming.lookup(name);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("Couldn't create ClientToNamingServerInterface." );
			e.printStackTrace();
		}
		return ni;
	}

	public String getName(){
		return name;
	}
	
	public TCP getTCP(){
		return tcp;
	}
	
	public void removeLocationFromFileFromOwnerNode(String fileName, String nodeNameToRemove){
		int hashOwner = -1;
		ClientToNamingServerInterface ni = makeNI();
		try {
			hashOwner = ni.getHashFileLocation(fileName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ni = null;
		System.out.println("remove location message : removing location from file "+fileName+", hash node: "+hashOwner);
		ClientToClientInterface ctci = makeCTCI(hashOwner);
		try {
			ctci.removeLocationFromFile(fileName,nodeNameToRemove);
		} catch (RemoteException e) {
			if (hashOwner != -1){
				failure(hashOwner);
			}

			e.printStackTrace();
		}
		ctci = null;
	}
	
	public void removeLocationFromFile(String fileName, String nodeNameToRemove){
		fileManager.removeLocationFromFile(fileName,nodeNameToRemove);
	}
	
	public void addLocationToFile(String fileName, String nodeNameToAdd){
		fileManager.addFileLocation(fileName,nodeNameToAdd);
	}
	
	public void transferOwnerShipToNode(String nodeName, FileFiche fiche){
		int hash = -1;
		ClientToNamingServerInterface ni = makeNI();
		try {
			hash = ni.getHashNodeByNodeName(nodeName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ni = null;
		ClientToClientInterface ctci = makeCTCI(hash);
		try {
			ctci.transferOwnerShip(fiche);
		} catch (RemoteException e) {
			if (hash != -1){
				failure(hash);
			}

			e.printStackTrace();
		}
		ctci = null;
	}
	
	public void transferOwnerShipToNode(int hashNode, FileFiche fiche){
		ClientToClientInterface ctci = makeCTCI(hashNode);
		try {
			ctci.transferOwnerShip(fiche);
		} catch (RemoteException e) {
			if (hashNode != -1){
				failure(hashNode);
			}

			e.printStackTrace();
		}
		ctci = null;
	}
	
	public void transferOwnerShip(FileFiche fiche){
		fileManager.transferOwnerShip(fiche);
	}
	
	public void removeRepFileFromNetwork(String fileName){
		removeRepFileFromNetwork(fileName,0);
	}
	
	public void removeRepFileFromNetwork(String fileName, int hashOriginalNode){
		if (hashOriginalNode == 0){
			ClientToClientInterface ctci = makeCTCI(nextNode);
			try {
				ctci.removeRepFileFromNetwork(fileName,ownHash);
			} catch (RemoteException e) {
				failure(nextNode);
				e.printStackTrace();
			}
			ctci = null;
		} else if (hashOriginalNode == ownHash){
			//do nothing, ring command ends
		} else {
			fileManager.removeRepFile(fileName);
			ClientToClientInterface ctci = makeCTCI(nextNode);
			try {
				ctci.removeRepFileFromNetwork(fileName,hashOriginalNode);
			} catch (RemoteException e) {
				failure(nextNode);
				e.printStackTrace();
			}
			ctci = null;
		}
	}
	
	public void removeFileFromNetwork(String fileName){
		removeFileFromNetwork(fileName,0);
	}
	
	public void removeFileFromNetwork(String fileName, int hashOriginalNode){
		if (hashOriginalNode == 0){
			ClientToClientInterface ctci = makeCTCI(nextNode);
			try {
				ctci.removeFileFromNetwork(fileName,ownHash);
			} catch (RemoteException e) {
				failure(nextNode);
				e.printStackTrace();
			}
			ctci = null;
		} else if (hashOriginalNode == ownHash){
			//do nothing, ring command ends
		} else {
			fileManager.removeFileWithFile(fileName);
			ClientToClientInterface ctci = makeCTCI(nextNode);
			try {
				ctci.removeFileFromNetwork(fileName,hashOriginalNode);
			} catch (RemoteException e) {
				failure(nextNode);
				e.printStackTrace();
			}
			ctci = null;
		}
	}

	public void addLocationToFileFromOwnerNodeByHash(String fileName, int hashNodeToAdd) {
		ClientToNamingServerInterface ni = makeNI();
		String nodeName = null;
		try {
			nodeName = ni.getNameNode(hashNodeToAdd);
		} catch (RemoteException e) {
			System.out.println("Couldn't reach namingserver");
			e.printStackTrace();
		}
		ni = null;
		addLocationToFileFromOwnerNodeByName(fileName,nodeName);
	}
	
	public void addLocationToFileFromOwnerNodeByName(String fileName, String nameNodeToAdd) {
		ClientToNamingServerInterface ni = makeNI();
		int hashOwner = -1;
		try {
			hashOwner = ni.getHashFileLocation(fileName);
		} catch (RemoteException e) {
			System.out.println("Couldn't reach namingserver");
			e.printStackTrace();
		}
		ni = null;
		
		ClientToClientInterface ctci = makeCTCI(hashOwner);
		try {
			ctci.addLocationToFile(fileName,nameNodeToAdd);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ctci = null;
		
	}
	
	public void passAgent(FileWithoutFileList list) {
		refreshNeighbours();
		ClientToClientInterface ctci = makeCTCI(nextNode);
		try {
			ctci.startAgent(list);
		} catch (RemoteException e) {
			failure(nextNode);
			e.printStackTrace();
		}
		ctci = null;
	}
	
	public void startAgent(FileWithoutFileList list){
		new Thread(new Agent(this,list)).start();
	}
	
	public void startUpAgent(){
		FileWithoutFileList list = new FileWithoutFileList();
		startAgent(list);
	}
	
	public boolean sendFileTo(String fileName, int hashNodeToSend){
		boolean isFound = false;
		if (!fileManager.hasFile(fileName)){
			return false;
		} else {
			isFound = true;
			fileManager.downloadRequestTo(fileName,hashNodeToSend);
		}
		
		return isFound;
	}
	
	public boolean canFileBeDeleted(String fileName){
		return fileManager.canFileBeDeleted(fileName);
	}

	public void addFileNameToDeleteListAllNodes(String fileName) {
		addFileToDeleteListNextNode(fileName,0);
	}
	
	public void addFileToDeleteListNextNode(String fileName, int hashOriginalNode){
		if (hashOriginalNode == 0){
			ClientToClientInterface ctci = makeCTCI(nextNode);
			try {
				ctci.addFileToDeleteListNextNode(fileName,ownHash);
			} catch (RemoteException e) {
				failure(nextNode);
				e.printStackTrace();
			}
			ctci = null;
		} else if (hashOriginalNode == ownHash){
			//do nothing, ring command ends
		} else {
			fileManager.addToDeleteListByNodeClient(fileName);
			ClientToClientInterface ctci = makeCTCI(nextNode);
			try {
				ctci.addFileToDeleteListNextNode(fileName,hashOriginalNode);
			} catch (RemoteException e) {
				failure(nextNode);
				e.printStackTrace();
			}
			ctci = null;
		}
	}
	
	public void startMainGUI(){
		new GUI(this);
	}
	
}
