package SystemY;

public class TCPFileReceiverThread implements Runnable {
	
	public void run() {
		System.out.println("test");
		
	}
	
	public static void main(String args[]){
		(new Thread(new TCPFileReceiverThread())).start();
	}
}
