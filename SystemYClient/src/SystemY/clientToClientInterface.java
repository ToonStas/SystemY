package SystemY;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface clientToClientInterface extends Remote {
	public void getNotified(int previousHash, int nextHash) throws RemoteException;
	public void getFile(InetAddress IPSource,String pathFile, String naamBestand, String pathBestand, int hashOwner, int hashReplicationNode, int fileSize, int fileID) throws RemoteException;
	public void sendReplicationToNewNode(int HashNewNode);
	public void activateAgent(Thread agent) throws RemoteException;
}