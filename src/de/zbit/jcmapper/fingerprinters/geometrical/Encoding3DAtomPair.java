package de.zbit.jcmapper.fingerprinters.geometrical;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.CombinatorialPatternHelper;
import de.zbit.jcmapper.fingerprinters.features.IFeature;


public class Encoding3DAtomPair extends Encoding3D {

	private final CombinatorialPatternHelper combPatternHelper = new CombinatorialPatternHelper();
	
	public Encoding3DAtomPair() {
		super.setDistanceCutoff(10);
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		List<IFeature> features = new ArrayList<IFeature>();
		final int[][] matrix = super.computeDistanceMatrix(ac);
		int distanceCutOff = (int) Math.round(super.getDistanceCutoff());
		features = combPatternHelper.getFingerprint2Point(ac, matrix, distanceCutOff, super.getTyper());
		return features;
	}
	
	@Override
	public String getNameOfFingerPrinter() {
		return "2-Point Atom Pairs 3D";
	}

}
