package testietestie;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class TestMainReceive {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
				TCP tcp = new TCP();
				for (int i = 0;i<5;i++){
					while (tcp.ReceiveFile(100000000, "C:/java/lieke"+i+".mp3")==0){
						TimeUnit.SECONDS.sleep(1);
					}
				}

	}

}
