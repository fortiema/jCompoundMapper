package de.zbit.jcmapper.fingerprinters.geometrical;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.CombinatorialPatternHelper;
import de.zbit.jcmapper.fingerprinters.features.IFeature;


public class Encoding3DAtomTriple extends Encoding3D {

	private final CombinatorialPatternHelper combPatternHelper = new CombinatorialPatternHelper();

	public Encoding3DAtomTriple() {
		super.setDistanceCutoff(6);
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		List<IFeature> features = new ArrayList<IFeature>();
		final int[][] matrix = super.computeDistanceMatrix(ac);
		int distanceCutOff = (int) Math.round(super.getDistanceCutoff());
		features = combPatternHelper.getFingerprint3Point(ac, matrix, distanceCutOff, super.getTyper());
		return features;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "3-Point Atom Pairs 3D";
	}

}
