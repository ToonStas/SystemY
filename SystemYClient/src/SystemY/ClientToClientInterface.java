package SystemY;

import java.io.Serializable;
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
	public void removeLocationFromFile(String fileName, String nodeNameToRemove) throws RemoteException;
	public void addLocationToFile(String fileName, String nodeNameToAdd) throws RemoteException;
	public void transferOwnerShip(FileFiche fiche) throws RemoteException;
	public void removeFileFromNetwork(String fileName, int hashOriginalNode) throws RemoteException;
	public void startAgent(FileWithoutFileList allFiles) throws RemoteException;
	public boolean sendFileTo(String fileName, int hashNodeToSend) throws RemoteException;
	public boolean canFileBeDeleted(String fileName) throws RemoteException;
}