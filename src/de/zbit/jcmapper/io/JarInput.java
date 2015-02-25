package de.zbit.jcmapper.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class JarInput {

	private final String content;

	public String getContent() {
		return content;
	}

	public JarInput(String input) {
		InputStream is = getResourceAsStream(input);
		content = getResourceAsString(is);
 	}

	/**
	 * 
	 * get input from jar
	 * 
	 * @param name
	 * @return
	 */
	private InputStream getResourceAsStream(String name) {
		URL url = this.getClass().getResource(name);
		try {
			return url != null ? url.openStream() : null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
 
	/**
	 * 
	 * converts an input stream into an String object
	 * 
	 * @param is
	 * @return
	 */
	private String getResourceAsString(InputStream is) {

		java.io.BufferedReader din = new BufferedReader(new InputStreamReader(new java.io.DataInputStream(is)));
		StringBuffer sb = new StringBuffer();
		try {
			String line = null;
			while ((line = din.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception ex) {
			ex.getMessage();
		} finally {
			try {
				is.close();
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}
}
