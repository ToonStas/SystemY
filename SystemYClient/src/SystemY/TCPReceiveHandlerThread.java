package SystemY;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

public class TCPReceiveHandlerThread extends Thread {
	private TCP tcp;
	private TreeMap<Integer,ListedReceiveFile> wachtlijst;
	private Semaphore sem;
	public TCPReceiveHandlerThread(TCP thisTcp){
		tcp = thisTcp;
		wachtlijst = tcp.getReceiveList();
		sem = tcp.getSemReceive();
	}
	
	public void run(){
		long timeOut = 100;
		int state = 0;
		int ID;
		int timeOutCounter = 0;
		int timeOutCounterSend = 0;
		Thread receiver = null;
		ListedReceiveFile listedFile = null;
		while (true){
			try {
				Thread.sleep(timeOut);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			switch (state) {
				case 0: //kijken of er een bestand kan verzonden worden
					if (wachtlijst.containsKey(tcp.getReceiveMessage())){
						ID = tcp.getReceiveMessage();
						listedFile = wachtlijst.get(ID);
						state = 1; 
					}
					else {
						state = 0;
					}
					timeOutCounter = 0;
					
					break;
				case 1: //bestand zit in de lijst, check semafoor
					if (tcp.getSemReceive().tryAcquire()){
						clientToClientInterface ctci = listedFile.ctci;
						try {
							ctci.setTCPSendMessage(listedFile.ID);
							receiver = tcp.StartReceiveFile(listedFile.path, listedFile.size,listedFile.ID);
							timeOutCounterSend = 0;
							state = 2;
						} catch (RemoteException e) {
							System.out.println("RMI error in tcp receiveHandler met ID "+listedFile.ID);
							e.printStackTrace();
							state = 0;
						}
					}
					else {
						if (timeOutCounter < 50){
							timeOutCounter++;
							state = 1;
						}
						else{
							state = 0;
							System.out.println("TimeOutCounter expired in receivehandler with ID "+listedFile.ID);
						}
					}
				
					break;
					
				case 2:
					if (timeOutCounterSend < 10 && receiver.isAlive()){
						timeOutCounterSend++;
						clientToClientInterface ctci = listedFile.ctci;
						try {
							ctci.setTCPSendMessage(listedFile.ID);
							state = 2;
						} catch (RemoteException e) {
							System.out.println("RMI error in tcp receiveHandler met ID "+listedFile.ID);
							e.printStackTrace();
							state = 0;
						}
					}
					else {
						state = 0;
					}
			
			}
			
			
			
		}
	}

}
