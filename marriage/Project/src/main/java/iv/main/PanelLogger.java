package iv.main;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class PanelLogger extends Logger {

	private final JLabel label;
	private final JProgressBar bar;

	public PanelLogger(JLabel label, JProgressBar bar) {
		this.label = label;
		this.bar = bar;
	}

	@Override
	public void doLog(String message) {
		label.setText(message);
	}

	@Override
	public void setProgress(int soFar, int maximum) {
		bar.setMaximum(maximum);
		bar.setValue(soFar);
	}
}
