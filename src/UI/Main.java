package UI;

import javax.swing.UIManager;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.getCause();
        }
		@SuppressWarnings("unused")
		MainUI m = new MainUI();
	}

}
