package de.zbit.jcmapper.tools.progressbar;

/**
 * Draws a nice graphical ASCII/ANSI Prograss bar on the console. Auto detects
 * if output is piped to a file or virtual console (e.g. Eclipse Output window)
 * and simply outputs percentages in this case.
 * 
 * @author wrzodek
 */
public class ProgressBar extends AbstractProgressBar {
	private static final long serialVersionUID = 2073719565121276629L;

	private int lastPerc = -1;
	private boolean isWindows = (System.getProperty("os.name").toLowerCase().contains("windows")) ? true : false;
	protected boolean useSimpleStyle = useSimpleStyle();

	/**
	 * Initialize the progressBar object
	 * 
	 * @param totalCalls
	 *            - how often you are planning to call the "DisplayBar" method.
	 */
	public ProgressBar(int totalCalls) {
		setNumberOfTotalCalls(totalCalls);
	}

	/**
	 * Initialize the progressBar object
	 * 
	 * @param totalCalls
	 *            - how often you are planning to call the "DisplayBar" method.
	 */
	public ProgressBar(long totalCalls) {
		setNumberOfTotalCalls(totalCalls);
	}

	/**
	 *
	 * @param estimateTime
	 */
	public ProgressBar(int totalCalls, boolean estimateTime) {
		this(totalCalls);
		setEstimateTime(estimateTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zbit.util.aProgressBar#drawProgressBar(int, double,
	 * java.lang.String)
	 */
	protected synchronized void drawProgressBar(int percent, double miliSecondsRemaining, String additionalText) {
		String percString = percent + "%";

		// Calculate time remaining
		String ETA = "";
		if (getEstimateTime() && miliSecondsRemaining >= 0) {
			ETA = " ETR: " + Utils.getTimeString((long) miliSecondsRemaining);
		}

		// Simples File-out oder Eclipse-Output-Window tool. Windows Console
		// unterstuetzt leider auch kein ANSI.
		if (useSimpleStyle) {
			if (percent != lastPerc) {
				System.out.println(percString + ETA + (additionalText != null && (additionalText.length() > 0) ? " " + additionalText : ""));
				lastPerc = percent;
			}
			return;
		}

		// Nice-and cool looking ANSI ProgressBar ;-)
		String anim = "|/-\\";
		StringBuilder sb = new StringBuilder();
		int x = percent / 2;
		sb.append("\r\033[K"); // <= clear line, Go to beginning
		sb.append("\033[107m"); // Bright white bg color
		int kMax = 50;
		for (int k = 0; k < kMax; k++) {
			if (x == k)
				sb.append("\033[100m"); // grey like bg color

			/*
			 * // % Zahl ist immer am "Farbschwellwert" (klebt am rechten
			 * bankenrand) if (x<percString.length()) { if (x<=k &&
			 * k<x+percString.length())
			 * sb.append("\033[93m"+percString.charAt(k-x)); // yellow else
			 * sb.append(" "); } else { if (k<x && (x-percString.length())<=k)
			 * sb
			 * .append("\033[34m"+percString.charAt(1-(x-percString.length()-k+
			 * 1))); // blue else sb.append(" "); }
			 */

			// %-Angabe zentriert
			int pStart = kMax / 2 - percString.length() / 2;
			int pEnd = kMax / 2 + percString.length() / 2;
			if (k >= pStart && k <= pEnd) {
				char c = ' ';
				if (k - pStart < percString.length())
					c = percString.charAt(k - pStart);
				if (x <= k)
					sb.append("\033[93m" + c);
				if (x > k)
					sb.append("\033[34m" + c);
			} else
				sb.append(" ");

		}

		sb.append("\033[0m "); // Reset colors and stuff.
		sb.append("\033[93m" + anim.charAt((int) (getCallNumber() % anim.length())) + " \033[1m" + ETA.trim() + (ETA.length() > 0 ? " " : "") + (additionalText != null && (additionalText.length() > 0) ? additionalText : ""));
		sb.append("\033[0m");

		// \033[?25l <=hide cursor.
		// \033[?25h <=show cursor.

		try {
			// System.console().writer().print(sb.toString()); // Not supported
			// in Java 1.5!
			// System.console().flush();
			System.out.print(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return; // sb.toString();
	}

	/**
	 * Determines if ANSI compliance console commands can be used, based on java
	 * version, os type and outputStream Type.
	 * 
	 * @return
	 */
	protected boolean useSimpleStyle() {
		boolean useSimpleStyle = false;
	if (isWindows) {
		      useSimpleStyle = true; // MS Windows has (by default) no ANSI capabilities.
		      return useSimpleStyle;
		    }

		// is TTY Check is only available for java 1.6. So a wrapper to
		// determine java version is needed for Java 1.5 compatibility.
		String v = System.getProperty("java.version");
		if (v != null && v.length() > 2) {
			try {
				double d = Double.parseDouble(v.substring(0, 3));
				if (d < 1.6)
					useSimpleStyle = true;
				else
					useSimpleStyle = !isTTY_Java16only.isTty();
			} catch (Throwable e) {
				useSimpleStyle = true;
			}
		}

		return useSimpleStyle;
	}

	public void finished() {
		if (!useSimpleStyle)
			System.out.println();
	}

}
