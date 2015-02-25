package de.zbit.jcmapper.io.writer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.topological.features.PositionFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.tools.progressbar.ProgressBar;



public abstract class ExporterHashLinear extends ExporterLinear {
	protected int hashSpace;

	public ExporterHashLinear() {
		setHashSpace(1024);
	}

	public ExporterHashLinear(int hashspace) {
		setHashSpace(hashSpace);
	}

	public int getHashSpace() {
		return hashSpace;
	}

	/**
	 * sets the dimension of the hash space
	 * 
	 * @param hashSpace
	 */
	public void setHashSpace(int hashSpace) {
		this.hashSpace = hashSpace;
	}

	protected abstract void writeHeader(FileWriter fw,EncodingFingerprint fingerprinter,String label,RandomAccessMDLReader reader);

	protected abstract int writeFingerprint(ArrayList<IFeature> fingerprint, FileWriter fw, String label);

	@Override
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag) {
		
		try {
			
			final FileWriter fw = new FileWriter(outputFile);
			double collisions = 0;
			double featureCount = 0.0;
			Long start = System.currentTimeMillis();
			writeHeader(fw,fingerprinter,label,reader);
			
			ProgressBar progressBar = new ProgressBar(reader.getSize());
			
 			for (int i = 0; i < reader.getSize(); i++) {
				IAtomContainer mol = reader.getMol(i);
				FeatureMap featureMap = new FeatureMap(fingerprinter.getFingerprint(mol));
				String molLabel = (String) mol.getProperty(label);
				if (molLabel != null) {
					featureMap.setLabel(molLabel);
				} else {
					featureMap.setLabel("?");
				}

				Set<IFeature> keys = featureMap.getKeySet();
				featureCount = featureCount + keys.size();
				ArrayList<IFeature> featureBits = new ArrayList<IFeature>();
				// rehash all features and pack them into a sortable list
				
				
				
				for (IFeature feature : keys) {
					if (feature instanceof IFeature) {
						if(feature instanceof PositionFeature){
							featureBits.add(feature);
						}else{
							int localHash = ExporterHelper.rehash(feature.hashCode(), this.hashSpace);
							IFeature hashedFeature = new HashedBitFeature(localHash);
							featureBits.add(hashedFeature);
						}
					}
				}
				
				progressBar.DisplayBar();
 				Collections.sort(featureBits);
				collisions += writeFingerprint(featureBits, fw, featureMap.getLabel());
			}

			fw.close();
			Long end = System.currentTimeMillis();
			System.out.println("Time elapsed: " + (end - start) + " ms");
			ExporterHelper.printInfo(collisions, featureCount, reader.getSize());

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private class HashedBitFeature implements IFeature {
		int hash;

		public HashedBitFeature(int hash) {
			this.hash = hash;
		}

		@Override
		public String featureToString(boolean useAromaticFlag) {
			return ("" + hash);
		}

		@Override
		public double getValue() {
			return 1;
		}
		
		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public int compareTo(IFeature arg0) {
			if (arg0.hashCode() > hashCode())
				return -1;
			if (arg0.hashCode() < hashCode())
				return 1;
			return 0;
		}

		@Override
		public Iterable<IAtom> representedAtoms() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Iterable<IBond> representedBonds() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
