package SystemY;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientToClientInterface extends Remote {
	public void getNotified(int previousHash, int nextHash) throws RemoteException;
	public void setReceiveRequest(TCPReceiveFileRequest request) throws RemoteException;
	//public void sendReplicationToNewNode(int HashNewNode) throws RemoteException;
	public void checkReplicationFromNextNode() throws RemoteException;
	//public void activateAgent(Thread agent) throws RemoteException;
	public int checkReceiveAvailable(int fileID) throws RemoteException;
	public int getPreviousNode() throws RemoteException;
	
}