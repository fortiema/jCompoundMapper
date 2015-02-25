package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;


public class Encoding2DAllShortestPath extends Encoding2D {

	public Encoding2DAllShortestPath() {
		super.setSearchDepth(8);
	}

	/**
	 * returns the set of all shortest path between two nodes
	 * 
	 * @param ac
	 * @param start
	 * @param end
	 * @param distance
	 * @return
	 */
	private List<PathFeature> getAllShortestPath(IAtomContainer ac, Atom start, Atom end, int distance) {
		final ArrayList<List<IAtom>> pathlist = (ArrayList<List<IAtom>>) PathTools.getPathsOfLength(ac, start, distance);
		final ArrayList<PathFeature> features = new ArrayList<PathFeature>();
		for (int i = 0; i < pathlist.size(); i++) {
			int listSize = pathlist.get(i).size();
			IAtom endAtom = pathlist.get(i).get(listSize - 1);
			// if this path has the shortest distance possible and it ends with
			// the target atom, add the feature
			if (((distance + 1) == pathlist.get(i).size()) && endAtom.equals(end)) {
				final PathFeature feature = new PathFeature(pathlist.get(i), ac);
				features.add(feature);
			}
		}
		return features;
	}

	@Override
	public List<IFeature> getFingerprint(IAtomContainer ac) {
		final int[][] admat = AdjacencyMatrix.getMatrix(ac);
		final int[][] shortest_path = PathTools.computeFloydAPSP(admat);
		final List<PathFeature> allShortestPaths = new ArrayList<PathFeature>();

		for (int i = 0; i < ac.getAtomCount(); i++) {
			final Atom start = (Atom) ac.getAtom(i);
			// we may skip the other half of the matrix because we generate a
			// canonical features afterwards
			for (int j = i; j < ac.getAtomCount(); j++) {
				if (i == j) {
					continue;
				}
				final Atom end = (Atom) ac.getAtom(j);
				if (shortest_path[i][j] <= super.getSearchDepth()) {
					final List<PathFeature> allShortestPathsIndex = getAllShortestPath(ac, start, end,
							shortest_path[i][j]);
					allShortestPaths.addAll(allShortestPathsIndex);
				}
			}
		}

		final ArrayList<IFeature> fingerprint = new ArrayList<IFeature>();
		for (int i = 0; i < allShortestPaths.size(); i++) {
			final String string = allShortestPaths.get(i).toString();
			final NumericStringFeature feature = new NumericStringFeature(string, 1.0);
			fingerprint.add(feature);
		}

		return fingerprint;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "All-Shortest-Path";
	}
}
