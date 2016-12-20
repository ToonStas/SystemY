package SystemY;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.TreeMap;

public interface NamingServerToClientInterface extends Remote {
	public void setServerIP(String IP) throws RemoteException;
	public TreeMap<String, Integer> getFileList() throws RemoteException;
	public void activateAgent() throws RemoteException;
}
