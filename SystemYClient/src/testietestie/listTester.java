package testietestie;

import java.util.ArrayList;

public class listTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("6");
		list.add("7");
		list.add("8");
		list.add("9");
		list.add("10");
		
		list.remove(0);
		list.remove(3);
		String sr = list.toString();
		System.out.println(list.get(0));
		
	}

}
