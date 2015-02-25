package de.zbit.jcmapper.io.writer;


import java.io.FileWriter;
import java.util.ArrayList;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;


public abstract class ExporterLinear implements IExporter {

	protected abstract void writeHeader(FileWriter fw,EncodingFingerprint fingerprinter,String label,RandomAccessMDLReader reader);

	protected abstract int writeFingerprint(ArrayList<IFeature> fingerprint, FileWriter fw, String label);
	
}
