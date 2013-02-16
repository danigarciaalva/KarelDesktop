package UI;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class KEditor extends JPanel{

	private JTextArea code;
	
	public KEditor(int height){
		setPreferredSize(new Dimension(500,height));
		setLayout(null);
		code = new JTextArea();
		code.setBounds(0, 0, 500, height);
		add(code);
	}
	
	public String getText(){
		return code.getText();
	}
}
