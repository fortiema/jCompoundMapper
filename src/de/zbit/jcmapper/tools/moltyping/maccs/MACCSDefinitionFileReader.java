package de.zbit.jcmapper.tools.moltyping.maccs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import de.zbit.jcmapper.io.JarInput;


public class MACCSDefinitionFileReader {
	
	public ArrayList<MACCSSmartsPattern> readMACCSDefinitions() {
		final ArrayList<MACCSSmartsPattern> ret = new ArrayList<MACCSSmartsPattern>();
		try {
			JarInput jarinput = new JarInput("/resources/MACCS166-SMARTS-DEFINITIONS");
			final BufferedReader br = new BufferedReader(new StringReader(jarinput.getContent()));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}
				// Check if the line contains docu information
				if (line.charAt(0) == '#') {
					continue;
				}
				// Split the line into type smarts and description
				final String[] e = line.split("[\\t]");
				int pos = Integer.parseInt(e[0]);
				int freq = Integer.parseInt(e[2]);
				final MACCSSmartsPattern pat = new MACCSSmartsPattern(pos, e[1], freq, e[3]);
				ret.add(pat);
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/*public static void main(String [] args){
		MACCSDefinitionFileReader reader = new MACCSDefinitionFileReader();
		ArrayList<MACCSSmartsPattern> patts = reader.readMACCSDefinitions();
		System.out.println(patts.size());
		for (MACCSSmartsPattern pat : patts){
			int pos = pat.getPosition();
			int freq = pat.getFrequency();
			String pattern = pat.getSMARTS();
			String description = pat.getDescription();
			String res = "" + pos + '\t' + pattern + '\t' + freq + '\t' + description;
			System.out.println(res);
		}
	}*/

}
