package SystemY;

public class FailureNode {
	private int failingNode;
	private int leftNeighbour;
	private int rightNeighbour;
	
	public void failure(int hash){ 
		//method to call when exeption occurs
		failingNode = hash;
		getNeighbours();
		updateNeighbours();
		removeNode();
	}
	private void getNeighbours(){
		//ask naming server for hash from neighbours of failing node.
	}
	private void updateNeighbours(){
		//send neighbours from failing node updates about there new neigbours.
	}
	private void removeNode(){
		// send message to naming server to delete failing node from list.
	}
	
}
