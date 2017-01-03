package SystemY;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientToNamingServerInterface extends Remote{
	public String getFileLocation(String fileName) throws RemoteException;
	public String askLocation(String fileName) throws RemoteException;
	public int amIFirst() throws RemoteException;
	public String getIP(int hash) throws RemoteException;
	public int getHashByName(String nodeName) throws RemoteException;
	public int askHashLocation(String fileName) throws RemoteException;
	public int[] getNeigbours(int hashNode) throws RemoteException;
	public void deleteNode(int hashNode) throws RemoteException;
	public void activateAgent(int ownHash) throws RemoteException;
	public int getHash(String ip) throws RemoteException;
}
