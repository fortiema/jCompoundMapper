package de.zbit.jcmapper.tools.moltyping.pharmacophore;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import de.zbit.jcmapper.io.JarInput;


public class PPPDefinitionFileReader {

	public ArrayList<PotentialPharmacophorePoint> readPharmacophoreDefinitions() {
		final ArrayList<PotentialPharmacophorePoint> ret = new ArrayList<PotentialPharmacophorePoint>(10);
		try {
			JarInput jarinput = new JarInput("/resources/CATS2D-SMARTS-DEFINITIONS");
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
				final PotentialPharmacophorePoint ppp = new PotentialPharmacophorePoint(e[0], e[1], e[2]);
				ret.add(ppp);
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
