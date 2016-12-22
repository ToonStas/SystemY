package SystemY;

import java.rmi.RemoteException;


public class TCPSendHandlerThread extends Thread {

	private TCP tcp;
	private SendBuffer sendBuffer;
	private int state;
	private NodeClient node;
	private SendFileRequest request;
	private ClientToClientInterface ctci;
	public TCPSendHandlerThread(TCP thisTcp, NodeClient nodeClient){
		tcp = thisTcp;
		node = nodeClient;
		state = 0;
	}
	
	public void run(){
		long sleepTime = 100;
		int message;
		int currentID = -600; //random value
		
		while (true){
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			switch(state) {
				case 0: //standard state
					sendBuffer = tcp.getSendBuffer();
					if (sendBuffer.isNext() && tcp.getSemSend().tryAcquire()){
						request = sendBuffer.getNext();
						if (request.ID != currentID) { //if not, the thread wil start multiple send Threads for 1 file
							
							ctci = node.makeCTCI(request.hashReceiver);
							try {
								message = ctci.checkReceiveAvailable(request.ID);
								if (message == request.ID){ //ready to receive
									tcp.StartSendFile(request);
									currentID = request.ID;
									ctci = null;
									state = 0;
								}
								else if (message == -1) { //semaphore not available
									state = 1;
									currentID = request.ID;
								}
								else if (message == -2) { //file does not exist
									state = 2;
									currentID = request.ID;
								}
								else {
									state = 0;
									ctci = null;
								}
								
							} catch (RemoteException e) {
								ctci = null;
								node.failure(request.hashReceiver);
								sendBuffer.remove(request.ID);
								tcp.getSemSend().release();
								e.printStackTrace();
							}
						}

						
					}
					break;
					
				case 1: //waiting on semaphore receiver
					if (request.checkSemTOC()) {
						try {
							
							message = ctci.checkReceiveAvailable(request.ID);
							if (message == request.ID){
								tcp.StartSendFile(request);
								ctci = null;
								state = 0;
							} 
							else if (message == -1){
								state = 1;
							}
							else {
								state = 0;
								ctci = null;
								sendBuffer.remove(request.ID);
								tcp.getSemSend().release();
							}
						
						} catch (RemoteException e) {
							ctci = null;
							node.failure(request.hashReceiver);
							sendBuffer.remove(request.ID);
							tcp.getSemSend().release();
							e.printStackTrace();
						}
					}
					else { //semaphore time out counter expired
						System.out.println("Semphore timeout counter expired from file request with ID "+request.ID);
						ctci = null;
						sendBuffer.remove(request.ID);
						tcp.getSemSend().release();
						state = 0;
					}
					break;
					
				case 2: //receiver didn't find the file in his buffer
					if (request.checkFileTOC()) {
						try {
							
							message = ctci.checkReceiveAvailable(request.ID);
							if (message == request.ID){
								tcp.StartSendFile(request);
								ctci = null;
								state = 0;
							} 
							else if (message == -1){
								state = 1;
							}
							else if (message == -2){
								state = 2;
							}
							else {
								state = 0;
								ctci = null;
								sendBuffer.remove(request.ID);
								tcp.getSemSend().release();
							}
						
						} catch (RemoteException e) {
							ctci = null;
							node.failure(request.hashReceiver);
							sendBuffer.remove(request.ID);
							tcp.getSemSend().release();
							e.printStackTrace();
						}
					}
					else { //file time out counter expired
						System.out.println("File not found timeout counter expired from file request with ID "+request.ID);
						ctci = null;
						sendBuffer.remove(request.ID);
						tcp.getSemSend().release();
						state = 0;
					}
					break;
				
					
				default:
					state = 0;

			}
			
			
			
			
		}
	}
}
