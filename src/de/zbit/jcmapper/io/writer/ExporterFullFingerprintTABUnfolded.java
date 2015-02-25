/*
 * Author: Joerg Kurt Wegner (JKW), me@joergkurtwegner.eu
 * Copyright JKW, 2012. All rights reserved.
 * 2011-12-14
 */
package de.zbit.jcmapper.io.writer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DECFP;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.io.writer.feature.SortableFeature;
import de.zbit.jcmapper.tools.progressbar.ProgressBar;


public class ExporterFullFingerprintTABUnfolded implements IExporter {

	public ExporterFullFingerprintTABUnfolded() {
	}

	@Override
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag) {

		try {
			final FileWriter fw = new FileWriter(outputFile);
			double collisions = 0;
			double featureCount = 0.0;
			Long start = System.currentTimeMillis();
			
			//if(fingerprinter.getNameOfFingerPrinter().equals("ECFP")){
	        //	//switch on substructure hashes, aka do not use default iteration and parent hash lists
	        //	((Encoding2DECFP)fingerprinter).setSubstructureHash(true);
	        // }
			
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

				Set<IFeature> featureKeys = featureMap.getKeySet();
				featureCount = featureCount + featureKeys.size();
				HashMap<Integer, SortableFeature> features = new HashMap<Integer, SortableFeature>();
			
				for (IFeature feature : featureKeys) {
					int hashCode = feature.hashCode();
					if (features.containsKey(hashCode)) {
						collisions++;
					} else {
						features.put(hashCode, new SortableFeature(feature, useAromaticFlag));
					}
				}

				for (Integer hashCode : features.keySet()){
					fw.append(featureMap.getLabel() + "\t");
					fw.append(hashCode.toString()+ "\t");
					SortableFeature feature=features.get(hashCode);
					fw.append(feature.getValue()+ "\t");
					String featureString=feature.getString(useAromaticFlag);
					fw.append(featureString+ ", ");
					fw.append("\n");
				}
				progressBar.DisplayBar();
			}

			fw.close();
			Long end = System.currentTimeMillis();
			System.out.println("Time elapsed: " + (end - start) + " ms");
			ExporterHelper.printInfo(collisions, featureCount, reader.getSize());

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}