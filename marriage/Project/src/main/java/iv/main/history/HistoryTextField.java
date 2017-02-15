package iv.main.history;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Need to find this in another library...
 * @author thallock
 *
 */

public class HistoryTextField extends JTextField implements DocumentListener, FocusListener, ActionListener, KeyListener
{

	SubstringsSet history = new SubstringsSet();
	HistoryPanel panel = new HistoryPanel();
	HistoryFrame frame = new HistoryFrame(panel);
	
	{
		getDocument().addDocumentListener(this);
		addFocusListener(this);
		addActionListener(this);
		
		frame.setSize(50, 50);
		addKeyListener(this);
	}

	public void addAll(Collection<String> collectAllLocations)
	{
		history.addAll(collectAllLocations);
	}

	public void clear()
	{
		history.clear();
	}

	public TreeSet<String> getHistory()
	{
		return history.getHistory();
	}

	private void changed()
	{
		updateShowing();
	}


	@Override
	public void insertUpdate(DocumentEvent e)
	{
		changed();
	}


	@Override
	public void removeUpdate(DocumentEvent e)
	{
		changed();
	}


	@Override
	public void changedUpdate(DocumentEvent e)
	{
		changed();
	}
	
	
	public synchronized void updateShowing()
	{
		if (!isVisible() || !this.hasFocus())
		{
			stopShowing();
			return;
		}
		
		String last = panel.getHighlighted();
		panel.strings.clear();
		history.collectSubstrings(getText(), panel.strings);
		panel.highlight(last);
		
		final Point locationOnScreen = getLocationOnScreen();
		frame.setFocusableWindowState(false);
		frame.setBounds(
			locationOnScreen.x, locationOnScreen.y + getHeight(),
			getWidth(), panel.strings.size() * panel.HEIGHT);
		frame.setVisible(true);
		panel.repaint();
	}
	public void stopShowing()
	{
		frame.setVisible(false);
	}


	@Override
	public void focusGained(FocusEvent e) {}
	@Override
	public void focusLost(FocusEvent e)
	{
		stopShowing();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		history.add(getText());
		transferFocus();
	}


	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e)
	{
		System.out.println(e);
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
			panel.down();
			e.consume();
			break;
		case KeyEvent.VK_UP:
			panel.up();
			e.consume();
			break;
		case KeyEvent.VK_ENTER:
			if (panel.highlighted < 0)
				break;
			e.consume();
			String str = panel.getHighlighted();
			if (str != null)
				setText(str);
			transferFocus();
		case KeyEvent.VK_DELETE:
			if (panel.highlighted < 0)
				break;
			e.consume();
			history.remove(panel.getHighlighted());
			updateShowing();
		}
	}
}
