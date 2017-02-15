package iv.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel
{
	private String text = "loading...";
	private BufferedImage image;

	public void setImage(BufferedImage image)
	{
		this.image = image;
		this.text = null;
		repaint();
	}

	public void setText(String text)
	{
		this.text = text;
		this.image = null;
		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		int w = getWidth();
		int h = getHeight();

		g.setColor(Color.black);
		g.fillRect(0, 0, w, h);

		if (text != null)
		{
			g.setColor(Color.WHITE);
			g.drawString(text, w / 2, h / 2);
		}
		else
		{
			g.drawImage(image, 0, 0, w, h, 0, 0, image.getWidth(), image.getHeight(), this);
		}
	}
}
