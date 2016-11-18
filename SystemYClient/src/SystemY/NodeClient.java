package SystemY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.rmi.*;
import java.util.TreeMap;

public class NodeClient {
	private TreeMap<Integer, String> nodeLijst = new TreeMap<>(); //hash, ipadres
	private TreeMap<String, Integer> bestandenLijst = new TreeMap<>(); //filename, hash
	String ip = "localhost";
	private int nextNode=32768;
	private int previousNode=0;
	private int ownHash;
	private Thread multicastReceiverThreadClient, tcpNotifyReceiverThread;

	public static void main(String args[]) {
		new NodeClient();
	}

	public NodeClient() {
		multicastReceiverThreadClient = new Thread(new MulticastReceiverThreadClient(nodeLijst, nextNode, previousNode, ownHash, this));
		tcpNotifyReceiverThread = new Thread(new TCPNotifyReceiverThread());
		TCP tcpSender;
		startUp();		
		consoleGUI();
	}
	
	private void consoleGUI(){
		System.out.println("What do you want to do?");
		System.out.println("[1] List local files");
		System.out.println("[2] Look for file");
		System.out.println("[4] Exit");
		
		int input = Integer.parseInt(readConsole());
		System.out.println("Your choice was: " + input);
		
		switch(input){
			case 1 : 	checkLocalFiles(new File("C:/TEMP")); 
						System.out.println("Local Files are: " + bestandenLijst);
			break;
			
			case 2 :	String location = getFileLocation("Enter file to look for: " + readConsole());
						System.out.println("The location is: " + location);
			break;
			
			case 4 :	shutdown();
			break;
		}
	}

	private void checkLocalFiles(File dir) {
		File[] filesList = dir.listFiles();
		for (File f : filesList) {
			bestandenLijst.put(f.getName(), calculateHash(f.getName()));
		}
	}

	private String getFileLocation(String fileName) {
		String location = "";
		// TODO get IP address in discover
		try {
			String name = "//"+ip+":1099/NamingServer";
			NamingServerInterface ni = (NamingServerInterface) Naming.lookup(name);
			location = ni.getFileLocation(fileName);
		} catch (Exception e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			e.printStackTrace();
		}
		return location;
	}

	private void startUp() {
		try {
			new MulticastSender(ownHash);
			multicastReceiverThreadClient.start();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void addNode(int hash, String ipadres) { // Bij het opstarten van een andere node wordt deze via deze methode aan de
													// lijst van gekende nodes toegevoegd
		nodeLijst.put(hash, ipadres);
	}

	private int calculateHash(String nodeNaam) { // Deze functie berekent de hash van een String als parameter.
		int tempHash = nodeNaam.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		return tempHash;
	}
	
	private String readConsole() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input="";
		try {
			input = br.readLine();
		} catch (IOException | NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			readConsole();
		}
		return input;
	}
	
	private void shutdown() { //Client (hashnumber) wants to shut down
		int hnnext; //hashnumber of the next node
		int hnprev; //hashnumber of the previous node
		//Get next node and previous node using the current nodes hash number
		hnnext = nodeLijst.higherKey(ownHash); //This returns the next neighbour 
		hnprev = nodeLijst.lowerKey(ownHash);
		//Send ID of next node to previous node
		//Change next node IN the previous node
		//if the hash is -1, the hash is not changed
		notifyNext(hnprev, -1, hnnext);
		//omwisselen --> nodeLijst.put(hnnext, ipprev); //Change next node of Previous node
		//Sent ID of previous node to next node
		//Change previous node INT in next node
		notifyPrevious(-1, hnnext, hnprev);
		//omwisselen --> nodeLijst.put(hnprev, ipnext); //Change previous node of next node
		//Remove node
	}
	
	//Notify next node via RMI
	public void notifyNext(int ownHash /*previous hash*/, int nextNodeHash /*next hash*/, int hash /*of node to notify*/) {
		//Notifies the new node that his previous node is this node and his next node is this node's former next node
		try {
			String name = nodeLijst.get(hash);
			clientToClientInterface ni = (clientToClientInterface) Naming.lookup(name);
			ni.getNotified(ownHash, nextNodeHash);
		} catch (Exception e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void notifyPrevious(int previousNodeHash, int ownHash, int hash) {
		//Notifies the new node that his previous node is this node's former previous node and his next node is this node
		try {
			String name = nodeLijst.get(hash);
			clientToClientInterface ni = (clientToClientInterface) Naming.lookup(name);
			ni.getNotified(previousNodeHash, ownHash);
		} catch (Exception e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	//Get notified via RMI
	public void getNotified(int previousHash, int nextHash){
		if(previousHash!=-1){
			previousNode = previousHash;
		}
		if(nextHash!=-1){
			nextNode = nextHash;
		}
	}
}
