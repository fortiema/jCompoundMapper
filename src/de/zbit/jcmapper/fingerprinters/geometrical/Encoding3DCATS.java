package de.zbit.jcmapper.fingerprinters.geometrical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;
import de.zbit.jcmapper.tools.moltyping.PharmacophorePointAssigner;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;
import de.zbit.jcmapper.tools.moltyping.pharmacophore.PotentialPharmacophorePoint;
import de.zbit.jcmapper.tools.moltyping.pharmacophore.PotentialPharmacophorePointPair;


public class Encoding3DCATS extends Encoding3D{

	private final PharmacophorePointAssigner pAssigner = new PharmacophorePointAssigner();

	public Encoding3DCATS() {
		super.setDistanceCutoff(9);
		super.setAtomLabelType(AtomLabelType.CUSTOM);
	}

	/**
	 * The SMARTS pattern are matched and assigned to the atoms. Then, the last
	 * lipophilic pattern (carbon atoms only adjacent to carbon atoms). This
	 * pattern is faster realized by a limited DFS starting at each carbon atom
	 * of the molecule
	 * 
	 * The negative COOH/SOOH/POOH groups are better realized by an graph
	 * 
	 * @param mol
	 * @return
	 */
	private int[] getCATS3DFingerprint(AtomContainer mol) {
		final HashMap<Integer, Vector<PotentialPharmacophorePoint>> PPPAssignment = pAssigner.getPharmacophorePoints(mol);
		final int[][] matrix = super.computeDistanceMatrix(mol);
		final HashSet<PotentialPharmacophorePointPair> Pairs = pAssigner.assignPairs(mol, PPPAssignment, matrix);
		return getFingerprint(Pairs);
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		final int[] features = this.getCATS3DFingerprint((AtomContainer) ac);
		final ArrayList<IFeature> fingerprint = new ArrayList<IFeature>();
		for (int i = 0; i < features.length; i++) {
			//if (features[i] > 0) {
				final NumericStringFeature feature = new NumericStringFeature("CATS3D-" + i, features[i]);
				fingerprint.add(feature);
		//	}
		}
		return fingerprint;
	}

	private int[] getFingerprint(HashSet<PotentialPharmacophorePointPair> Pairs) {
 		final int[] ret = new int[(int) ((Math.ceil(super.getDistanceCutoff()+1)*15))];

		for (final PotentialPharmacophorePointPair pppp : Pairs) {
			if (pppp.getDistance() > super.getDistanceCutoff()) {
				continue;
			}
			ret[pAssigner.getIndex4PPPP(pppp, (int)super.getDistanceCutoff())]++;
		}
		return ret;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "CATS3D";
	}
	
	@Override
	public void setAtomLabelType(AtomLabelType atomLabelType) {
		//ignore
	}
		
	@Override
	public boolean isHashable() {
		return false;
	}
}
