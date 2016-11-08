package SystemY;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NamingServerInterface extends Remote{
	public String getFileLocation(String fileName) throws RemoteException;
}
