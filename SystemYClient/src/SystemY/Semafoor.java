package SystemY;

public class Semafoor {
	private volatile boolean isAcquired;
	
	public Semafoor(){
		isAcquired = false;
	}
	
	public boolean tryAcquire(){
		if (isAcquired){
			return false;
		} else {
			isAcquired = true;
			return true;
		}
	}
	
	public boolean release(){
		if (isAcquired){
			isAcquired = false;
			return true;
		} else {
			return false;
		}
	}

}
