package de.zbit.jcmapper.io.writer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.io.writer.feature.SortableFeature;
import de.zbit.jcmapper.tools.progressbar.ProgressBar;


public class ExporterSparseNominalWeka implements IExporter {

	private int labelThreshold = 5;

	public void setLabelThreshold(int labelThreshold) {
		// System.out.println("yes it works:"+labelThreshold);
		this.labelThreshold = labelThreshold;
	}
	
	/**
	 * generates a hash map with all features found in the data set
	 * 
	 * @param reader
	 * @param fingerprinter
	 * @return
	 */
	private TreeMap<IFeature, Integer> collectGlobalFeatures(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter) {
		// first round: collect all features
		
		TreeMap<IFeature, Integer> globalFeatureHashMap = new TreeMap<IFeature, Integer>();
		for (int i = 0; i < reader.getSize(); i++) {
			IAtomContainer mol = reader.getMol(i);
			FeatureMap featureMap = new FeatureMap(fingerprinter.getFingerprint(mol));
			Set<IFeature> keys = featureMap.getKeySet();
			Iterator<IFeature> featureIterator = keys.iterator();
			while (featureIterator.hasNext()) {
				IFeature currentFeature = featureIterator.next();
				globalFeatureHashMap.put(currentFeature, 0);
			}
		}

		// assign indices 1,...,n to the n features found
		Set<IFeature> featuresGlobal = globalFeatureHashMap.keySet();
		Iterator<IFeature> featuresIter = featuresGlobal.iterator();
		
		TreeMap<IFeature, Integer> globalFeatureHashMapFinal = new TreeMap<IFeature, Integer>();
		int index = 1;
		while (featuresIter.hasNext()) {
			globalFeatureHashMapFinal.put(featuresIter.next(), index);
			index++;
		}

		return globalFeatureHashMapFinal;
	}

	/*
	 * write a ARFF header
	 */
	private void writeHeader(TreeMap<IFeature, Integer> map, FileWriter fw, RandomAccessMDLReader reader, String label, boolean useAromaticFlag) throws IOException {
		Set<IFeature> keys = map.keySet();
		fw.append("@relation	MOLECULE\n");
		Iterator<IFeature> iter = keys.iterator();
		while (iter.hasNext()) {
			fw.append("@ATTRIBUTE\t+" + iter.next().featureToString(useAromaticFlag) + "\t{0,1}\n");
		}
		fw.append("@ATTRIBUTE "+ getLabels(label, reader));
		fw.append("@DATA\n");
	}

	@Override
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag) {

		int collisions = 0;

		Long start = System.currentTimeMillis();
		
		
		ProgressBar progressBar = new ProgressBar(reader.getSize());

		try {
			final FileWriter fw = new FileWriter(outputFile);
			TreeMap<IFeature, Integer> globalFeatureHashMap = collectGlobalFeatures(reader, fingerprinter);
			writeHeader(globalFeatureHashMap, fw, reader, label, useAromaticFlag);

			for (int i = 0; i < reader.getSize(); i++) {
				IAtomContainer mol = reader.getMol(i);
				FeatureMap featureMap = new FeatureMap(fingerprinter.getFingerprint(mol));
				String molLabel = (String) mol.getProperty(label);
				if (molLabel != null) {
					featureMap.setLabel(molLabel);
				} else {
					featureMap.setLabel("?");
				}

				ArrayList<SortableFeature> Features = new ArrayList<SortableFeature>();
				Set<IFeature> keys = featureMap.getKeySet();
				for (IFeature feature : keys) {
					if (feature instanceof IFeature) {
						Features.add(new SortableFeature(feature,useAromaticFlag));
					}
				}

				Collections.sort(Features);

				fw.append("{");
				for (SortableFeature feature : Features) {
					int index = globalFeatureHashMap.get(feature);
					fw.append(" " + index + " 1,");
				}				

				fw.append(featureMap.getLabel());
				fw.append("}");
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