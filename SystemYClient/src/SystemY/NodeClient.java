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
		Object tcpSender;
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
		String ip = "localhost";
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
	
	private void shutdown() { //als de client stopt
		
	}

	public void notifyNext(int ownHash /*previous hash*/, int nextNodeHash /*next hash*/, int hash /*of node to notify*/) {
		//Notifies the new node that his previous node is this node and his next node is this node's former next node
		//tcpSender.notifyNextAdd(ownHash, nextNodeHash, InetAddress.getByName(nodeLijst.get(hash)));  
	}

	public void notifyPrevious(int previousNodeHash, int ownHash, int hash) {
		//Notifies the new node that his previous node is this node's former previous node and his next node is this node
		//tcpSender.notifyPrevious(previousNodeHash, ownHash, InetAddress.getByName(nodeLijst.get(hash)));
	}
}
