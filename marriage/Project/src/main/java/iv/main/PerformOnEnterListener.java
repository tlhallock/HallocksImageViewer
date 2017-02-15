package iv.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

public class PerformOnEnterListener extends KeyAdapter
{
	JButton button;
	
	PerformOnEnterListener(JButton button)
	{
		this.button = button;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() != KeyEvent.VK_ENTER)
			return;
		button.doClick();
		button.transferFocus();
	}
}
