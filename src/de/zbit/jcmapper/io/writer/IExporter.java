package de.zbit.jcmapper.io.writer;


import java.io.File;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;

public interface IExporter {
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag);
}