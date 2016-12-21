package SystemY;

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
		while (true){
			try {
				Thread.sleep(timeOut);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}

}
