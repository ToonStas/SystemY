
public class ReceiverTest {

	public static void main(String[] args) {
		Thread thr = new Thread(new MulticastReceiverThread());
		thr.start();

		// Als receiver toch gesloten moet worden voer volgende code uit:
		// TODO werkt niet echt
		(new MulticastReceiverThread()).close();

	}
}
