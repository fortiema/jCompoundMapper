package de.zbit.jcmapper.io.writer;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;


public class ExporterHashWeka extends ExporterHashLinear {
	private int labelThreshold = 5;

	public void setLabelThreshold(int labelThreshold) {
		// System.out.println("yes it works:"+labelThreshold);
		this.labelThreshold = labelThreshold;
	}

	public ExporterHashWeka() {
		super();
	}

	public ExporterHashWeka(int hashSpace) {
		super(hashSpace);
	}

	@Override
	protected int writeFingerprint(ArrayList<IFeature> fingerprint, FileWriter fw, String label) {
		int collisions = 0;
		int fingerPrintPos = 0;
		try {

		fw.append("{");
			for (int hash = 0; hash <= super.getHashSpace(); hash++) {

				if (fingerPrintPos < fingerprint.size()) {

					if (fingerprint.get(fingerPrintPos).hashCode() == hash) {
						fw.append(hash+" 1, ");
						fingerPrintPos++;
					} else if (fingerprint.get(fingerPrintPos).hashCode() < hash) {
						collisions++;
						fingerPrintPos++;
						hash--;
					} else{
						//fw.append("0,");
					}
				} else {
				}

			}
			fw.append(this.getHashSpace()+1 +" "+label);
			fw.append("}\n");

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return collisions;
	}

	@Override
	protected void writeHeader(FileWriter fw, EncodingFingerprint fingerprinter, String label, RandomAccessMDLReader reader) {
		try {
			/*
			 * there is a possibility to comment the export file
			 */
			// fw.append("%");
			fw.append("@relation\tMOLECULE\n");
			for (int i = 0; i <= super.getHashSpace(); i++) {
				fw.append("@ATTRIBUTE\t" + "HASH-" + (i + "").trim() + "\t{0,1}\n");
			}
			fw.append("@ATTRIBUTE\tLABEL\t" + getLabels(label, reader) + "\n");

			fw.append("\n@DATA\n");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	protected String getLabels(String label, RandomAccessMDLReader reader) {
		String labels = "NUMERIC";

		Set<String> labelSet = reader.getAllLabelClasses(label);
		ArrayList<String> listLabels = new ArrayList<String>(labelSet);
		Collections.sort(listLabels);

		int labelsCount = labelSet.size();
		System.out.println("Labels found: "+ labelsCount);

		if (labelsCount < labelThreshold) {
			System.out.println("Below threshold, exporting as nominal classes instead of numeric class...");
			labels = "{";

			int c = 0;
			for (String l : listLabels) {
				if (c == 0) {
					labels += l;
				} else {
					labels += "," + l;
				}
				c++;
			}

			labels += "}";
		}
		return labels;
	}
}