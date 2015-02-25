package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public class Encoding2DAllPaths extends Encoding2D {

	public Encoding2DAllPaths() {
		super.setAtomLabelType(AtomLabelType.ELEMENT_NEIGHBOR);
		super.setSearchDepth(8);
	}

	/**
	 * returns the set of all shortest path between two nodes
	 * 
	 * @param ac
	 * @param start
	 * @return
	 */
	private List<PathFeature> getAllPathFeatures(IAtomContainer ac, Atom start, int depth) {
		final List<List<IAtom>> paths = PathTools.getPathsOfLengthUpto(ac, start, super.getSearchDepth());
		final ArrayList<PathFeature> features = new ArrayList<PathFeature>();
		for (int i = 0; i < paths.size(); i++) {
			final PathFeature feature = new PathFeature(paths.get(i), ac);
			features.add(feature);
		}
		return features;
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		final ArrayList<PathFeature> rawfingerprint = new ArrayList<PathFeature>();

		for (int i = 0; i < ac.getAtomCount(); i++) {
			final List<PathFeature> localpaths = getAllPathFeatures(ac, (Atom) ac.getAtom(i), super.getSearchDepth());
			rawfingerprint.addAll(localpaths);
		}

		final ArrayList<IFeature> fingerprint = new ArrayList<IFeature>();
		for (int i = 0; i < rawfingerprint.size(); i++) {
			final String string = rawfingerprint.get(i).toString();
			final NumericStringFeature feature = new NumericStringFeature(string, 1.0);
			fingerprint.add(feature);
		}
		return fingerprint;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "AllPaths";
	}
}
