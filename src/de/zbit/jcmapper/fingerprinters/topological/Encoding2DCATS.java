package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;
import de.zbit.jcmapper.tools.moltyping.PharmacophorePointAssigner;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;
import de.zbit.jcmapper.tools.moltyping.pharmacophore.PotentialPharmacophorePoint;
import de.zbit.jcmapper.tools.moltyping.pharmacophore.PotentialPharmacophorePointPair;


public class Encoding2DCATS extends Encoding2D {

	private final PharmacophorePointAssigner pAssigner = new PharmacophorePointAssigner();

	public Encoding2DCATS() {
		super.setSearchDepth(9);
		super.setAtomLabelType(AtomLabelType.CUSTOM);
	}

	private int[] getCATS2DFingerprint(AtomContainer mol) {
		final HashMap<Integer, Vector<PotentialPharmacophorePoint>> PPPAssignment = pAssigner
				.getPharmacophorePoints(mol);
		int[][] admat = AdjacencyMatrix.getMatrix(mol);
		int[][] shortestPathMatrix = PathTools.computeFloydAPSP(admat);
		final HashSet<PotentialPharmacophorePointPair> Pairs = pAssigner.assignPairs(mol, PPPAssignment,
				shortestPathMatrix);

		return this.getFingerprint(Pairs);
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		final int[] features = getCATS2DFingerprint((AtomContainer) ac);
		final ArrayList<IFeature> fingerprint = new ArrayList<IFeature>();
		for (int i = 0; i < features.length; i++) {
			//if (features[i] > 0) {
				final NumericStringFeature feature = new NumericStringFeature("CATS2D-" + i, features[i]);
				fingerprint.add(feature);
			//}
		}
		return fingerprint;
	}

	/**
	 * generates the vector, the dimension is adjusted according to the search
	 * depth
	 * 
	 * @param Pairs
	 * @return
	 */
	private int[] getFingerprint(HashSet<PotentialPharmacophorePointPair> Pairs) {
		final int[] ret = new int[(int) (Math.ceil(super.getSearchDepth()) + 1) * 15];
		for (final PotentialPharmacophorePointPair pppp : Pairs) {
			if (pppp.getDistance() > super.getSearchDepth()) {
				continue;
			}
			ret[pAssigner.getIndex4PPPP(pppp, this.searchDepth)]++;
		}
		return ret;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "CATS2D";
	}

	@Override
	public void setAtomLabelType(AtomLabelType atomLabelType) {
		// ignore
	}

	@Override
	public boolean isHashable() {
		return false;
	}
}
