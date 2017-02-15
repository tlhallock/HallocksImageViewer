package iv.main;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

public class EverythingSelector implements FocusListener
{
	private JTextComponent component;

	EverythingSelector(JTextComponent component)
	{
		this.component = component;
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		component.setSelectionStart(0);
		component.setSelectionEnd(component.getText().length());
	}

	@Override
	public void focusLost(FocusEvent e) {}
}
