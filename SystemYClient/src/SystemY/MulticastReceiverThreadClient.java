package SystemY;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.util.TreeMap;

public class MulticastReceiverThreadClient extends Thread {
	private int port;
	private String multicastGroup;
	private MulticastSocket s;
	private TreeMap<Integer, String> nodeLijst; //hash, ip
	private int nextNode, previousNode, ownHash;
	NodeClient nodeClient;
	String serverIP;

	public MulticastReceiverThreadClient(TreeMap<Integer, String> nodeLijst, int nextNode, int previousNode, int ownHash, NodeClient nodeClient, String serverIP) {	
		this.nodeLijst = nodeLijst;
		this.nextNode = nextNode;
		this.previousNode = previousNode;
		this.ownHash = ownHash;
		this.nodeClient = nodeClient;
		this.serverIP = serverIP;
		port = 8769;
		multicastGroup = "224.1.1.1";
		try {
			s = new MulticastSocket(port); // Create the socket and bind it to port 'port'.
			s.joinGroup(InetAddress.getByName(multicastGroup)); // join the multicast group
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// Create a DatagramPacket and do a receive
		byte buf[] = new byte[1024];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);
		
		try {
			s.receive(pack);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String nameIp = new String(buf, 0, buf.length);
		nameIp = nameIp.replaceAll(Character.toString((char) 0), "");

		//Print out the received data with the info
		System.out.println("Received data from: " + pack.getAddress() + ":" + pack.getPort() + " with length: "
				+ pack.getLength());
		System.out.write(pack.getData(), 0, pack.getLength());
		System.out.println();
		
		String[] parts = nameIp.split(" ");
		int hash = calculateHash(parts[0]);
		if(parts[0]=="Server"){
			String serverIP = parts[1];
		}else{
			nodeLijst.put(hash, parts[1]);
		}
		
		//Check if this node is the first node, if so it shouldnt replace its first and last node and it shoudnt notify other nodes.
		try {
			if(nodeClient.ni.amIFirst() == 0){
				if(hash>ownHash & hash<nextNode){// if the new node lies between this node and the next node
					//TODO notify next node with his previous and next hash: TCP.notifyNext(ownHash /*previous hash*/, nextNode /*next hash*/)
					nodeClient.notifyNext(ownHash /*previous hash*/, nextNode /*next hash*/, hash /*of node to notify*/);
					nextNode = hash;
				}else if(previousNode<hash & hash<ownHash){// if the new node lies between this node and the previous node
					//TODO notify previous with next hash and previous hash: TCP.notifyPrevious(previousNode /*previous hash*/, ownHash /*next hash*/)
					nodeClient.notifyPrevious(previousNode /*previous hash*/, ownHash /*next hash*/, hash /*of node to notify*/);
					previousNode = hash;
				}
			}else if(nodeClient.ni.amIFirst() == 1){
				nextNode = 32769;
				previousNode = -1;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
			
		//receive another
		run();
	}

	public void close() {
		try {
			s.leaveGroup(InetAddress.getByName(multicastGroup));
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int calculateHash(String nodeNaam){ //Deze functie berekent de hash van een String als parameter.
        int tempHash = nodeNaam.hashCode();
        if (tempHash < 0)
            tempHash = tempHash * -1;
        tempHash = tempHash % 32768;
        return tempHash;
    }

}
