package SystemY;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientToNamingServerInterface extends Remote{
	public String getNameFileLocation(String fileName) throws RemoteException;
	public String getIPFile(String fileName) throws RemoteException;
	public int amIFirst() throws RemoteException;
	public String getIPNode(int hash) throws RemoteException;
	public int getHashNodeByNodeName(String nodeName) throws RemoteException;
	public int getHashFileLocation(String fileName) throws RemoteException;
	public int[] getNeigbours(int hashNode) throws RemoteException;
	public void deleteNode(int hashNode) throws RemoteException;
	public void activateAgent(int ownHash) throws RemoteException;
	public int getHashNodeByIP(String ip) throws RemoteException;
	public String getNameNode(int hashNode) throws RemoteException;
}
