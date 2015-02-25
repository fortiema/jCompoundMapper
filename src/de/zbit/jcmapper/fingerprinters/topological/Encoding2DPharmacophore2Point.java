package de.zbit.jcmapper.fingerprinters.topological;

import java.util.List;

import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.CombinatorialPatternHelper;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public class Encoding2DPharmacophore2Point extends Encoding2D {

	final CombinatorialPatternHelper patternHelper = new CombinatorialPatternHelper();

	public Encoding2DPharmacophore2Point() {
		super.setAtomLabelType(AtomLabelType.CUSTOM);
	}
	
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		final int[][] admat = AdjacencyMatrix.getMatrix(ac);
		final int[][] shortestPathMatrix = PathTools.computeFloydAPSP(admat);
		int distanceCutOff = (int) Math.round(super.getSearchDepth());
		List<IFeature> features = patternHelper.getFingerprint2PointPPP(ac, shortestPathMatrix, distanceCutOff);
		return features;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "2-Point Pharmacophore Pairs 2D";
	}

	@Override
	public void setAtomLabelType(AtomLabelType atomLabelType) {
		//ignore
	}
}
