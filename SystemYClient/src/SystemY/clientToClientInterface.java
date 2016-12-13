package SystemY;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface clientToClientInterface extends Remote {
	public void getNotified(int previousHash, int nextHash) throws RemoteException;
	
	public void getFile(String pathFile) throws RemoteException;
}