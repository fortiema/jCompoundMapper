package fingerprinters;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;

import junit.framework.Assert;


public class SameMoleculeTester {
	
	public RandomAccessMDLReader reader = null;
	public final ArrayList<ArrayList<IFeature>> features = new ArrayList<ArrayList<IFeature>>();
	public final ArrayList<FeatureMap> featuremaps = new ArrayList<FeatureMap>();
	public final EncodingFingerprint fingerprinter;
	
	public SameMoleculeTester(EncodingFingerprint fingerPrinter) {
		this.fingerprinter = fingerPrinter;

		try {
			reader = new RandomAccessMDLReader(new File("./resources/Oxaceprol_MM.sdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < reader.getSize(); i++) {
			List<IFeature> featuresOfMolecule = fingerprinter.getFingerprint(reader.getMol(i));
			Assert.assertNotNull(featuresOfMolecule);
			Assert.assertTrue(featuresOfMolecule.size()>0);
			features.add((ArrayList<IFeature>)featuresOfMolecule);
		}
	}
	
	public void checkLength(){
		Assert.assertEquals(features.get(0).size(), features.get(1).size());
	}
	
	public void checkFeatures(){
		ArrayList<IFeature> mol1 = features.get(0);
		ArrayList<IFeature> mol2 = features.get(1);
		
		for(int i=0; i<mol1.size(); i++){
			Collections.sort(mol1);
			Collections.sort(mol2);
			Assert.assertEquals(mol1.get(i).hashCode(), mol2.get(i).hashCode());
		}
	}
	
	public void checkHashedFeatures(){
		FeatureMap mol1 = new FeatureMap(features.get(0));
		FeatureMap mol2 = new FeatureMap(features.get(1));
		
		Assert.assertNotNull(mol1);
		Assert.assertNotNull(mol2);
		
		BitSet hFingerprint1 = mol1.getHashedFingerPrint(1024);
		BitSet hFingerprint2 = mol2.getHashedFingerPrint(1024);
		
		Assert.assertEquals(hFingerprint1,hFingerprint2);
	}
}
