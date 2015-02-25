package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.graph.matrix.TopologicalMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public class Encoding2DMolprint extends Encoding2D {

	public Encoding2DMolprint() {
		super.setSearchDepth(3);
		super.setAtomLabelType(AtomLabelType.ELEMENT_SYMBOL);
	}

	private ArrayList<IFeature> calculateFingerprint(IAtomContainer molecule) throws Exception {
		final int[][] topologicalDistanceMatrix = TopologicalMatrix.getMatrix(molecule);
		final ArrayList<IFeature> features = new ArrayList<IFeature>();
		if (topologicalDistanceMatrix.length == 1) {
			return generateFingerprintForSingleAtomMolecule(molecule.getAtom(0));
		}

		for (int atomID = 0; atomID < topologicalDistanceMatrix.length; atomID++) {

			final List<List<String>> spheres = generateSphereLists();
			// generate the features
			for (int atom = 0; atom < topologicalDistanceMatrix[atomID].length; atom++) {
				if (topologicalDistanceMatrix[atomID][atom] <= this.getSearchDepth()) {
					spheres.get(topologicalDistanceMatrix[atomID][atom]).add(this.getAtomLabel(molecule.getAtom(atom)));
				}
			}
			// sorting makes the spheres canonical
			for (final List<String> sphere : spheres) {
				Collections.sort(sphere);
			}
			// 0[rootlabel]1[label1 label2 label3]2[label1 ... labeln] is a
			// FingerprintFeature
			final StringBuffer sb = new StringBuffer();

			for (int i = 0; i < spheres.size(); i++) {
				final List<String> sphere = spheres.get(i);
				if (sphere.size() == 0) {
					continue;
				}
				sb.append(i + "[");
				for (int j = 0; j < sphere.size() - 1; j++) {
					sb.append(sphere.get(j) + " ");
				}
				sb.append(sphere.get(sphere.size() - 1) + "]");
				if (i > 0) {
					features.add(new NumericStringFeature(sb.toString(), 1.0));
				}
			}
		}
		return features;
	}

	private ArrayList<IFeature> generateFingerprintForSingleAtomMolecule(IAtom atom) throws MoltyperException {
		final ArrayList<IFeature> features = new ArrayList<IFeature>();
		features.add(new NumericStringFeature("0[" + this.getAtomLabel(atom) + "]", 1.0));
		return features;
	}

	private List<List<String>> generateSphereLists() {
		final List<List<String>> spheres = new ArrayList<List<String>>(this.getSearchDepth() + 1);

		for (int d = 0; d <= this.getSearchDepth(); d++) {
			spheres.add(d, new ArrayList<String>(5));
		}
		return spheres;
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer molecule) {
		try {
			return this.calculateFingerprint(molecule);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<IFeature>();
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "RAD2D";
	}
}
