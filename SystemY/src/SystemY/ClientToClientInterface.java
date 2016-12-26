package SystemY;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientToClientInterface extends Remote {
	public void getNotified(int previousHash, int nextHash) throws RemoteException;
}