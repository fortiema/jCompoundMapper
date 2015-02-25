package de.zbit.jcmapper.io.writer;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;


public class ExporterLIBSVMSparse extends ExporterHashLinear {

	public ExporterLIBSVMSparse() {
		super();
	}

	public ExporterLIBSVMSparse(int hashSpace) {
		super(hashSpace);
	}

	protected int writeFingerprint(ArrayList<IFeature> fingerprint, FileWriter fw, String label) {
		int collisions = 0;

		try {
			fw.append(label);

			int lastUsedIndex = -1;
			for (IFeature feature : fingerprint) {
				if (feature.hashCode() == lastUsedIndex) {
					collisions++;
				} else {
					fw.append(" " + feature.hashCode() + ":1");
					lastUsedIndex = feature.hashCode();
				}
			}
			fw.append("\n");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return collisions;
	}
	

	@Override
	protected void writeHeader(FileWriter fw, EncodingFingerprint fingerprinter,String label,RandomAccessMDLReader reader) {
		// TODO Auto-generated method stub
		
	}
}