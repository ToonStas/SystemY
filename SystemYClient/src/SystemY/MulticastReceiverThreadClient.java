package SystemY;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MulticastReceiverThreadClient extends Thread {
	private int port;
	private String multicastGroup;
	private MulticastSocket s;
	//private TreeMap<Integer, String> nodeLijst; // hash, ip
	private int nextNode, previousNode, ownHash;
	NodeClient nodeClient;
	volatile boolean goAhead; 

	public MulticastReceiverThreadClient(int nextNode, int previousNode,
			int ownHash, NodeClient nodeClient, boolean goAhead) {
		//this.nodeLijst = nodeLijst;
		this.nextNode = nextNode;
		this.previousNode = previousNode;
		this.ownHash = ownHash;
		this.nodeClient = nodeClient;
		this.goAhead = goAhead;
		port = 8769;
		multicastGroup = "224.1.1.1";
		try {
			s = new MulticastSocket(port); // Create the socket and bind it to
											// port 'port'.
			s.joinGroup(InetAddress.getByName(multicastGroup)); // join the
																// multicast
																// group
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

		// Print out the received data with the info
		System.out.println("Received data from: " + pack.getAddress() + ":" + pack.getPort() + " with length: "
				+ pack.getLength());
		System.out.write(pack.getData(), 0, pack.getLength());
		System.out.println();

		String[] parts = nameIp.split(" ");
		int hash = nodeClient.calculateHash(parts[0]);
		// Check if this node is the first node, if so it shouldn't replace its
		// first and last node and it shouldn't notify other nodes.
		try {
			// wait until the class nodeClient says the interface is made and you can continue
			while (goAhead == false) {
				// wait
				goAhead = nodeClient.getGoAhead();
				TimeUnit.SECONDS.sleep(2);
			}
			if (nodeClient.ni.amIFirst() == 0) {
				if (hash > ownHash & hash < nextNode) {// if the new node lies between this node and the next node
					// TODO notify next node with his previous and next hash
					nodeClient.notifyNext(ownHash /* previous hash */, nextNode /* next hash */, hash /* of node to notify */);
					nextNode = hash;
				} else if (previousNode < hash & hash < ownHash) {// if the new node lies between this node and the previous node
					//nodeClient.notifyPrevious(previousNode /* previous hash */,
					//		-1 /* next hash */, hash /* of node to notify */); //next hash -1 because notify only what his previous should be
					previousNode = hash;
				}
			}else if (nodeClient.ni.amIFirst() == 1) {
				nodeClient.notifyPrevious(hash, hash, hash); //only adjust yourself so one of the methods is fine
			}
		} catch (RemoteException | InterruptedException e) {
			e.printStackTrace();
		}

		// receive another
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

}
