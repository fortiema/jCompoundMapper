package de.zbit.jcmapper.io.writer;


import java.io.File;
import java.text.DecimalFormat;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Median;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;

public class ExporterBenchmark implements IExporter {

	@Override
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag) {
		DecimalFormat df = new DecimalFormat();
		double[] features = new double[reader.getSize()];
		
		Long start = System.currentTimeMillis();
		for (int indexMol = 0; indexMol < reader.getSize(); indexMol++) {
			if ((indexMol != 0) && (indexMol % 1000 == 0))
				System.out.println("encodings/s = " 
						+ df.format(((double) indexMol) / ((double) ((System.currentTimeMillis() - start) / 1000))) 
						+ "\t(mappings so far = " + indexMol + ", @" 
						+ df.format(((double) indexMol / (double) reader.getSize()) * 100) + "%)");

			IAtomContainer mol = reader.getMol(indexMol);
			FeatureMap featureMap = new FeatureMap(fingerprinter.getFingerprint(mol));
			features[indexMol] = featureMap.getKeySet().size();
		}
		Long end = System.currentTimeMillis();
		
		Mean mean = new Mean();
		StandardDeviation stdv = new StandardDeviation();
		Max max = new Max();
		Median median = new Median();

		System.out.println("Time elapsed: " + (end - start) + " ms");
		System.out.println("mol/s = " + df.format(reader.getSize() / ((double) (end - start) / 1000)));
		System.out.println("no. features = " + df.format(mean.evaluate(features)) + "\t" + df.format(stdv.evaluate(features)));
		System.out.println("Max = " + df.format(max.evaluate(features)));
		System.out.println("Median = " + df.format(median.evaluate(features)));
	}
}