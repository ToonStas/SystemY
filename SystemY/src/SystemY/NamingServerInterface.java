package SystemY;

import java.rmi.Remote;

public interface NamingServerInterface extends Remote{
	public String getFileLocation(String fileName);

}
