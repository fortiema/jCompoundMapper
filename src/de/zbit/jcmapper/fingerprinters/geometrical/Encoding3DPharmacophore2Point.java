package de.zbit.jcmapper.fingerprinters.geometrical;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.CombinatorialPatternHelper;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public class Encoding3DPharmacophore2Point extends Encoding3D {
	
	final CombinatorialPatternHelper patternHelper = new CombinatorialPatternHelper();

	public Encoding3DPharmacophore2Point() {
		super.setDistanceCutoff(10);
		super.setAtomLabelType(AtomLabelType.CUSTOM);
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		final int[][] matrix = this.computeDistanceMatrix(ac);
		int distanceCutOff = (int) Math.round(super.getDistanceCutoff());
		List<IFeature> features = patternHelper.getFingerprint2PointPPP(ac, matrix, distanceCutOff);
		return features;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "2-Point Pharmacophore Pairs 3D";
	}

	@Override
	public void setAtomLabelType(AtomLabelType atomLabelType) {
		// custom type
	}
}
