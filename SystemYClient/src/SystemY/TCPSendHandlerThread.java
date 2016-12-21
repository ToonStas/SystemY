package SystemY;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

public class TCPSendHandlerThread extends Thread {

	private TCP tcp;
	private TreeMap<Integer,ListedSendFile> wachtlijst;
	private Semaphore sem;
	private int state;
	public TCPSendHandlerThread(TCP thisTcp){
		tcp = thisTcp;
		wachtlijst = tcp.getSendList();
		sem = tcp.getSemSend();
		state = 0;
	}
	
	public void run(){
		long timeOut = 100;
		int timeOutCounter = 0;
		int ID;
		ListedSendFile listedFile = null;
		while (true){
			try {
				Thread.sleep(timeOut);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch (state) {
				case 0: //check voor bestanden om te versturen
					if (!wachtlijst.isEmpty()){
						ID = wachtlijst.firstKey();
						listedFile = wachtlijst.get(ID);
						clientToClientInterface ctci = listedFile.ctci;
						try {
							ctci.setTCPReceiveMessage(ID);
						} catch (RemoteException e) {
							
							e.printStackTrace();
						}
						state = 1;
					}
					else{
						state = 0;	
					}
					timeOutCounter = 0;
					
					break;
					
				case 1: //kijken of ontvanger klaar is om te verzenden 
					if (tcp.getSendMessage() == listedFile.ID){
						clientToClientInterface ctci = listedFile.ctci;
						try {
							ctci.setTCPReceiveMessage(listedFile.ID);
						} catch (RemoteException e) {
							
							e.printStackTrace();
						}
						if (tcp.getSemSend().tryAcquire()){
							tcp.StartSendFile(listedFile.file, listedFile.IP);
							state = 0;
						}
						else {
							state = 2;
						}
					}
					else {
						if (timeOutCounter < 50){
							timeOutCounter++;
							state = 1;
						}
						else{
							System.out.println("TimeOutCounter expired for fileID "+listedFile.ID);
							state = 0;
						}
					}
					
					break;
				
				case 2: //wachten op semafoor
					if (tcp.getSemSend().tryAcquire()){
						tcp.StartSendFile(listedFile.file, listedFile.IP);
						state = 0;
					}
					else {
						if (timeOutCounter < 50){
							timeOutCounter++;
							state = 2;
						}
						else{
							System.out.println("TimeOutCounter expired for fileID "+listedFile.ID);
							state = 0;
						}
					}
					
					break;
			}
			
			
			
		}
	}
}
