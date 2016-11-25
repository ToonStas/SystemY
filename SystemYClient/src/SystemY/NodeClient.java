package SystemY;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class NodeClient extends UnicastRemoteObject implements clientToClientInterface {
	private TreeMap<Integer, String> nodeLijst = new TreeMap<>(); // hash, ipadres
	private TreeMap<String, Integer> bestandenLijst = new TreeMap<>(); // filename, hash
	private int nextNode = 32768;
	private int previousNode = 0;
	private int ownHash;
	private Thread multicastReceiverThreadClient;
	NamingServerInterface ni;
	String serverIP;
	boolean goAhead = false; //the thread should wait untill the interface has been made before communicating via it

	public static void main(String args[]) {
		try {
			new NodeClient();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public NodeClient() throws RemoteException {
		String nameNode = readConsoleName();
		multicastReceiverThreadClient = new Thread(
				new MulticastReceiverThreadClient(nodeLijst, nextNode, previousNode, ownHash, this, serverIP, goAhead));

		startUp(this, nameNode);

		System.out.println(ownHash);

		// oneindige while lus voor gui
		while (true)
			consoleGUI();
	}

	private void consoleGUI() {
		System.out.println("What do you want to do?");
		System.out.println("[1] List local files");
		System.out.println("[2] Look for file");
		System.out.println("[3] Print neighbours");
		System.out.println("[4] Exit");

		int input = Integer.parseInt(readConsole());
		System.out.println("Your choice was: " + input);

		switch (input) {
		case 1:
			checkLocalFiles(new File("C:/TEMP"));
			System.out.println("Local Files are: " + bestandenLijst);
			break;

		case 2:
			String location = getFileLocation("Enter file to look for: " + readConsole());
			System.out.println("The location is: " + location);
			break;

		case 3:
			System.out.println("Previous hash: "+previousNode);
			System.out.println("Next hash: "+nextNode);
			break;

		case 4:
			shutdown();
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
			location = ni.getFileLocation(fileName);
		} catch (Exception e) {
			System.err.println("NodeClient exception: " + e.getMessage());
			e.printStackTrace();
		}
		return location;
	}

	private void startUp(NodeClient nodeClient, String nameNode) {
		// connect RMI to NamingServer
		try {
			multicastReceiverThreadClient.start();
			while(serverIP == null){
				//wait untill we know the servers ip
				TimeUnit.SECONDS.sleep(2);
				System.out.println(serverIP);
			}
			new MulticastSender(ownHash, nameNode);
			String name = "//" + serverIP + ":1099/NamingServer";
			ni = (NamingServerInterface) Naming.lookup(name);
			ownHash = calculateHash(nameNode);
			
			//the interface has been made so the thread can continue
			goAhead = true;

			// make registry to establish RMI between nodes
			String bindLocation = "nodeClient";
			Registry reg = LocateRegistry.createRegistry(1100);
			reg.bind(bindLocation, nodeClient);
			System.out.println("ClientRegistery is ready at: " + bindLocation);
			System.out.println("java RMI registry created.");
		} catch (MalformedURLException | RemoteException | NotBoundException | UnsupportedEncodingException  e) {
			e.printStackTrace();
		} catch(AlreadyBoundException | InterruptedException e){
			System.out.println("Registry already in use");
			e.printStackTrace();
		}
	}

	// gebeurt ook afzonderlijk in de multicastreceiverthread
	private void addNode(int hash, String ipadres) { // Bij het opstarten van een andere node wordt
														// deze via deze methode
														// aan de
														// lijst van gekende
														// nodes toegevoegd
		nodeLijst.put(hash, ipadres);
	}

	private int calculateHash(String nodeNaam) { // Deze functie berekent de
													// hash van een String als
													// parameter.
		int tempHash = nodeNaam.hashCode();
		if (tempHash < 0)
			tempHash = tempHash * -1;
		tempHash = tempHash % 32768;
		return tempHash;
	}

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

	private void shutdown() { // Client (hashnumber) wants to shut down
		int hnnext; // hashnumber of the next node
		int hnprev; // hashnumber of the previous node
		// Get next node and previous node using the current nodes hash number
		hnnext = nodeLijst.higherKey(ownHash); // This returns the next
												// neighbour
		hnprev = nodeLijst.lowerKey(ownHash);
		// Send ID of next node to previous node
		// Change next node IN the previous node
		// if the hash is -1, the hash is not changed
		notifyNext(hnprev, -1, hnnext);
		// omwisselen --> nodeLijst.put(hnnext, ipprev); //Change next node of
		// Previous node
		// Sent ID of previous node to next node
		// Change previous node INT in next node
		notifyPrevious(-1, hnnext, hnprev);
		// omwisselen --> nodeLijst.put(hnprev, ipnext); //Change previous node
		// of next node
		// Remove node
		System.exit(0);
	}

	// Notify next node via RMI
	public void notifyNext(int ownHash /* previous hash */,
			int nextNodeHash /* next hash */, int hash /* of node to notify */) {
		// Notifies the new node that his previous node is this node and his
		// next node is this node's former next node
		try {
			String name = nodeLijst.get(hash);
			clientToClientInterface ctci = (clientToClientInterface) Naming.lookup("//" + name + ":1100/nodeClient");
			ctci.getNotified(ownHash, nextNodeHash);
		} catch (Exception e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// notify previous node via RMI
	public void notifyPrevious(int previousNodeHash, int ownHash, int hash) {
		// Notifies the new node that his previous node is this node's former
		// previous node and his next node is this node
		try {
			String name = nodeLijst.get(hash);
			clientToClientInterface ctci = (clientToClientInterface) Naming.lookup("//" + name + ":1100/nodeClient");
			ctci.getNotified(hash, ownHash);
		} catch (Exception e) {
			System.err.println("NamingServer exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Get notified via RMI
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
}
