package de.zbit.jcmapper.io.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;

public class ExporterLIBSVMSparseFrequency extends ExporterHashLinear {

	public ExporterLIBSVMSparseFrequency() {
		super();
	}

	public ExporterLIBSVMSparseFrequency(int hashSpace) {
		super(hashSpace);
	}

	protected int writeFingerprint(ArrayList<IFeature> fingerprint,
			FileWriter fw, String label) {
		int collisions = 0;
		int frequency = 1;
		try {

			fw.append(label);

			if (fingerprint.size() > 1) {
				Iterator<IFeature> iter = fingerprint.iterator();
				int lastUsedIndex = iter.next().hashCode();

				while (iter.hasNext()) {
					IFeature feature = iter.next();
					
					
					if (feature.hashCode() != lastUsedIndex) {
						fw.append(" " + lastUsedIndex + ":" + frequency);
						lastUsedIndex = feature.hashCode();
						frequency = 1;
					} else {
						frequency++;
					}
				}
				fw.append(" " + lastUsedIndex + ":" + frequency);
			}
			else if (fingerprint.size() == 1){
				Iterator<IFeature> iter = fingerprint.iterator();
				fw.append(" " + iter.next().hashCode() + ":" + 1);
			}
			fw.append("\n");

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return collisions;
	}

	@Override
	protected void writeHeader(FileWriter fw,
			EncodingFingerprint fingerprinter, String label,
			RandomAccessMDLReader reader) {
		// TODO Auto-generated method stub

	}
}