package SystemY;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

public class TCPSendHandlerThread extends Thread {

	private TCP tcp;
	private TreeMap<Integer,ListedSendFile> wachtlijst;
	private Semaphore sem;
	public TCPSendHandlerThread(TCP thisTcp){
		tcp = thisTcp;
		wachtlijst = tcp.getSendList();
		sem = tcp.getSemSend();
	}
	
	public void run(){
		long timeOut = 100;
		int ID;
		while (true){
			try {
				Thread.sleep(timeOut);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!wachtlijst.isEmpty()){
				ID = wachtlijst.firstKey();
				clientToClientInterface ctci = wachtlijst.get(ID).ctci;
				
			}
			
			
		}
	}
}
