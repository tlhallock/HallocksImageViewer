package iv.main;

public abstract class Logger
{
	public final void log(String message) {
		System.out.println(message);
		
		if (message.length() < Settings.MAX_LOG_LENGTH)
		{
			doLog(message);
			return;
		}
		
		StringBuilder msg = new StringBuilder(Settings.MAX_LOG_LENGTH);

		int newLength = Settings.MAX_LOG_LENGTH / 2;
		msg.append(message.substring(0, newLength - 3));
		msg.append("...");
		msg.append(message.substring(message.length() - newLength, message.length()));
		
		doLog(msg.toString());
	}
	
	public abstract void setProgress(int soFar, int maximum);

	protected abstract void doLog(String message);
	
	public static final Logger EMPTY_LOGGER = new Logger()
	{
		@Override
		public void setProgress(int soFar, int maximum) {}
		@Override
		protected void doLog(String message) {}
	};
}
