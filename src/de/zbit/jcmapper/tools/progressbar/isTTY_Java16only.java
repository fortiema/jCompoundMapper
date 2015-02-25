package de.zbit.jcmapper.tools.progressbar;

/**
 * 
 * @author wrzodek
 */
public class isTTY_Java16only {
	/**
	 * Returns if underlying System.out stream is a console or not. DO NOT USE
	 * THIS FUNCTION WITH JAVA 1.5. THIS OR SIMILAR FUNCTIONS ARE ONLY AVAILABLE
	 * VIA A NATIVE INTERFACE AND THUS NEARLY IMPOSSIBLE IN JDK5 !!!
	 * 
	 * @return
	 */
	public static boolean isTty() throws Throwable {
		if ((System.console() == null) || (System.console().writer() == null)) {
			return false;
		}
		return true;
	}
}
