package de.zbit.jcmapper.io.writer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.io.writer.feature.SortableFeature;
import de.zbit.jcmapper.tools.progressbar.ProgressBar;



public class ExporterSparseString implements IExporter {
 
	@Override
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag) {

		try {
			final FileWriter fw = new FileWriter(outputFile);
			int collisions = 0;
			java.util.Locale.setDefault(java.util.Locale.ENGLISH);
			DecimalFormat df = new DecimalFormat();

			Long start = System.currentTimeMillis();
			
			ProgressBar progressBar = new ProgressBar(reader.getSize());
			for (int i = 0; i < reader.getSize(); i++) {
				IAtomContainer mol = reader.getMol(i);
				FeatureMap featureMap = new FeatureMap(fingerprinter.getFingerprint(mol));
				String molLabel = (String) mol.getProperty(label);
				if (molLabel != null) {
					featureMap.setLabel(molLabel);
				} else {
					featureMap.setLabel(ExporterHelper.getMolName(mol) + "_INDEX=" + i);
				}

				//IFeature[] keys = (IFeature[]) featureMap.getKeySet().toArray();
				Set<IFeature> keys =   featureMap.getKeySet();
				ArrayList<SortableFeature> Features = new ArrayList<SortableFeature>();

				for (IFeature feature : keys) {
					if (feature instanceof IFeature) {
						Features.add(new SortableFeature(feature,useAromaticFlag));
					}
				}

				Collections.sort(Features);
				fw.append(featureMap.getLabel());
				int lastUsedIndex = 0;
				for (SortableFeature feature : Features) {
					if (feature.getHash() == lastUsedIndex) {
						collisions++;
						continue;
					}
					fw.append("\t" + feature.getString(useAromaticFlag) + ":" + df.format(feature.getValue()));
					lastUsedIndex = feature.getHash();
				}
				fw.append("\n");
				progressBar.DisplayBar();
			}
			Long end = System.currentTimeMillis();
			System.out.println("Time elapsed: " + (end - start) + " ms");
			fw.close();
			System.out.println("Collisions:" + collisions);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}