package iv.main.history;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;

import javax.swing.JPanel;

public class HistoryPanel extends JPanel
{
	public static final int HEIGHT = 30;
	
	public ArrayList<String> strings = new ArrayList<>();
	int highlighted = -1;

        Font FONT = new Font("Courier", Font.BOLD, 20);
	
	
	@Override
	public void paint(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.red);
		g.fillRect(0, 0, 1, getHeight());
		g.fillRect(0, getHeight() - 1, getWidth(), 1);
		g.fillRect(0, 1, getWidth(), 1);
		g.fillRect(getWidth() - 1, 0, getWidth(), getHeight());
		
		if (highlighted >= 0)
		{
			g.setColor(Color.YELLOW);
			g.fillRect(1, highlighted * HEIGHT + 1, getWidth() - 2, HEIGHT - 2);
		}

		g.setColor(Color.BLACK);
		int y = 0;
		for (String str : strings)
		{
			AttributedString string = new AttributedString(str);
			string.addAttribute(TextAttribute.FONT, FONT);
			g.drawString(string.getIterator(), 1, y + HEIGHT - 5);
			y += HEIGHT;
		}
	}

	public void up()
	{
		highlighted = Math.max(-1, highlighted - 1);
		repaint();
		System.out.println(highlighted);
	}

	public void down()
	{
		highlighted = Math.min(highlighted + 1, strings.size() - 1);
		System.out.println(highlighted);
		repaint();
	}
	
	public String getHighlighted()
	{
		if (highlighted < 0)
			return null;
		return strings.get(highlighted);
	}

	public void highlight(String last)
	{
		highlighted = -1;
		if (last == null)
			return;
		
		for (int i = 0; i < strings.size(); i++)
		{
			if (!last.equals(strings.get(i)))
				continue;
			highlighted = i;
			break;
		}
	}
}
