package SystemY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.*;
import java.util.TreeMap;

public class NodeClient {
	private TreeMap<Integer, String> nodeLijst = new TreeMap<>(); //hash, ipadres
	private TreeMap<String, Integer> bestandenLijst = new TreeMap<>(); //filename, hash

	public static void main(String argv[]) {
		new NodeClient();
	}

	private Thread multicastReceiverThreadClient;

	public NodeClient() {
		multicastReceiverThreadClient = new Thread(new MulticastReceiverThreadClient(nodeLijst));
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
			String name = "//localhost:1099/NamingServer";
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
			new MulticastSender();
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
	}
	
	private void shutdown() { //als de client stopt
		
	}
}
