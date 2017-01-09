package SystemY;


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MenuGUI extends JFrame implements ActionListener {

	JFrame frame;
	JButton enter = new JButton("ENTER");
	JButton set = new JButton("SET");
	JTextField name = new JTextField();
	NodeClient node;
	
public MenuGUI(NodeClient nodeClient)
{
	node = nodeClient;
	frame = new JFrame("Welkom");
    frame.setBackground(Color.LIGHT_GRAY);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new GridLayout(3,1));
   
    JPanel p = new JPanel();
    p.setLayout(new GridLayout(1,1));
    p.add(name);
    frame.add(p);
    frame.pack();
    
    JPanel p1 = new JPanel();
    p1.setLayout(new GridLayout(1,1));
    enter.addActionListener(this);
    p1.add(set);
    frame.add(p1);
    frame.pack();
    
    JPanel p2 = new JPanel();
    p2.setLayout(new GridLayout(1,1));
    enter.addActionListener(this);
    p2.add(enter);
    frame.add(p2);
    frame.pack();
    }

	public void actionPerformed(ActionEvent e){
	JButton b = (JButton) e.getSource();
	if(b == set)
	{
		String str = name.getText(); 
		node.setName(str);
	}
	if(b == enter)
	{
		node.startUp();
	}
	}

}


