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
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.tools.progressbar.ProgressBar;


public class ExporterFullFingerprintCSV implements IExporter {
	private int hashSpace;

	public ExporterFullFingerprintCSV() {
		this.hashSpace = (int) Math.pow(2, 12);
	}

	public ExporterFullFingerprintCSV(int hashSpace) {
		if (hashSpace > 18) {
			hashSpace = 18;
		}
		if (hashSpace < 8) {
			hashSpace = 8;
		}
		this.hashSpace = hashSpace;
	}


	public int getHashSpace() {
		return hashSpace;
	}

	public void setHashSpace(int hashSpace) {
		this.hashSpace = hashSpace;
	}

	@Override
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag) {

		try {
			final FileWriter fw = new FileWriter(outputFile);
			double collisions = 0;
			double featureCount = 0.0;
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

				Set<IFeature> featureKeys = featureMap.getKeySet();
				featureCount = featureCount + featureKeys.size();
				HashMap<Integer, IFeature> features = new HashMap<Integer, IFeature>();

				for (IFeature feature : featureKeys) {
					int hashCode = ExporterHelper.rehash(feature.hashCode(), this.hashSpace);
					if (features.containsKey(hashCode)) {
						collisions++;
					} else {
						features.put(hashCode, feature);
					}
				}

				fw.append(featureMap.getLabel() + ", ");
				for (int h = 0; h < hashSpace; h++) {
					if (h == hashSpace - 1) {
						if (features.containsKey(h)) {
							fw.append("1");
						} else {
							fw.append("0");
						}
						continue;
					}

					if (features.containsKey(h)) {
						fw.append("1, ");
					} else {
						fw.append("0, ");
					}
				}
				fw.append("\n");
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