package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;


public class DepthFirstSearch extends Encoding2D {

	/**
	 * @author russr
	 * 
	 */

	public class DepthFirstSearchFeature {

		final private int featureSize;
		final private String featureString;
		final private IAtom firstAtom;
		final private IAtom lastAtom;
		final private int atomsHash;

		public int getAtomsHash() {
			return atomsHash;
		}

		public DepthFirstSearchFeature(IAtom firstAtom, IAtom lastAtom, int featureSize, String featureString,
				int atomsHash) {
			if (firstAtom.hashCode() == lastAtom.hashCode() && featureSize > 1) {
				System.out.println("Wrong feature:" + featureString);
			}
			this.featureSize = featureSize;
			this.firstAtom = firstAtom;
			this.lastAtom = lastAtom;
			this.featureString = featureString;
			this.atomsHash = atomsHash;
		}

		public String featureToString() {

			return featureString;
		}

		public int hashToInteger() {
			return firstAtom.hashCode();
		}

		public boolean compareTo(DepthFirstSearchFeature other) {
			if (this.firstAtom.hashCode() == other.getFirstAtom().hashCode()
					&& (this.lastAtom.hashCode() == other.getLastAtom().hashCode())) {
				if (this.atomsHash == other.getAtomsHash()) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		public String getFeatureString() {
			return featureString;
		}

		public IAtom getFirstAtom() {
			return firstAtom;
		}

		public IAtom getLastAtom() {
			return lastAtom;
		}

	}

	private void checkAndStore(ArrayList<Object> newPath, Map<String, ArrayList<DepthFirstSearchFeature>> paths,
			ArrayList<IFeature> features, IAtomContainer ac) throws MoltyperException {
		final StringBuilder newPathStringForward = new StringBuilder();
		final StringBuilder newPathStringReverse = new StringBuilder();

		final int pathLength = newPath.size() - 1;
		IAtom firstAtom = null;
		IAtom lastAtom = null;
		int i;
		boolean forward = true;
		String storePath = "";

		// the atomsHash is used to determine whether a similar
		// looking path from one atom to another is really the same
		int atomsHash = 0;
		for (i = 0; i <= pathLength; i++) {
			if (newPath.get(i) instanceof IAtom) {
				atomsHash += ((IAtom) newPath.get(i)).hashCode();
			}
		}

		// this is a preview scan to avoid building the whole string twice
		for (i = 0; i <= pathLength; i++) {

			if (newPath.get(i) instanceof IAtom) {
				if (firstAtom == null) {
					firstAtom = (IAtom) newPath.get(i);
				}
				newPathStringForward.append(getAtomLabel((IAtom) newPath.get(i)));

			} else if (newPath.get(i) instanceof String) {
				newPathStringForward.append((String) newPath.get(i));
			}

			if (newPath.get(pathLength - i) instanceof IAtom) {
				if (lastAtom == null) {
					lastAtom = (IAtom) newPath.get(pathLength - i);
				}
				newPathStringReverse.append(getAtomLabel((IAtom) newPath.get(pathLength - i)));
			} else if (newPath.get(pathLength - i) instanceof String) {
				newPathStringReverse.append((String) newPath.get(pathLength - i));
			}

			/*
			 * We build up the forward and the reverse path until there happens
			 * to be a difference
			 */
			int direction = newPathStringForward.toString().compareTo(newPathStringReverse.toString());
			if (direction != 0) {
				if (direction < 0) {
					forward = true;
					break;
				} else if (direction > 0) {
					forward = false;
					IAtom tempAtom = firstAtom;
					firstAtom = lastAtom;
					lastAtom = tempAtom;
					break;
				}
			}
		}
		i++;
		if (forward == true) {
			for (; i <= pathLength; i++) {
				if (newPath.get(i) instanceof IAtom) {
					newPathStringForward.append(getAtomLabel((IAtom) newPath.get(i)));
				} else if (newPath.get(i) instanceof String) {
					newPathStringForward.append((String) newPath.get(i));
				}
			}
			storePath = newPathStringForward.toString();
		} else {
			for (; i <= pathLength; i++) {
				if (newPath.get(pathLength - i) instanceof IAtom) {
					newPathStringReverse.append(getAtomLabel((IAtom) newPath.get(pathLength - i)));

				} else if (newPath.get(pathLength - i) instanceof String) {
					newPathStringReverse.append((String) newPath.get((pathLength - i)));
				}
			}
			storePath = newPathStringReverse.toString();
		}
		// debug
		if (!paths.containsKey(storePath)) {
			DepthFirstSearchFeature newFeature = new DepthFirstSearchFeature(firstAtom, lastAtom, pathLength,
					storePath, atomsHash);
			ArrayList<DepthFirstSearchFeature> newArray = new ArrayList<DepthFirstSearchFeature>();
			newArray.add(newFeature);
			paths.put(storePath, newArray);
			features.add(new NumericStringFeature(newFeature.featureToString(), 1.0));
		} else {
			/*
			 * set FeatureIsIncluded to "true" as an option to suppress double
			 * features (these feature really appear more than once in the
			 * molecule)
			 */
			boolean featureIsIncluded = false;
			// featureIsIncluded=true;
			DepthFirstSearchFeature newFeature = new DepthFirstSearchFeature(firstAtom, lastAtom, pathLength,
					storePath, atomsHash);
			for (DepthFirstSearchFeature feature : paths.get(storePath)) {
				if ((feature).compareTo(newFeature)) {
					featureIsIncluded = true;
				}
			}
			if (!featureIsIncluded) {
				features.add(new NumericStringFeature(newFeature.featureToString(), 1.0));
				paths.get(storePath).add(newFeature);
			}

		}
	}

	/**
	 * Performs a recursive depth first search
	 * 
	 * @param ac
	 *            The AtomContainer to be searched
	 * @param root
	 *            The Atom to start the search at
	 * @param currentPath
	 *            The Path that has been generated so far
	 * @param currentDepth
	 *            The current depth in this recursive search
	 * @param searchDepth
	 *            Description of the Parameter
	 * @throws MoltyperException
	 */
	private void depthFirstSearch(IAtomContainer ac, IAtom root, Map<String, ArrayList<DepthFirstSearchFeature>> paths,
			List<Object> currentPath, int currentDepth, int searchDepth, ArrayList<IFeature> strings)
			throws MoltyperException {
		final List<IBond> bonds = ac.getConnectedBondsList(root);
		org.openscience.cdk.interfaces.IAtom nextAtom = null;
		ArrayList<Object> newPath = null;
		String bondSymbol = null;
		currentDepth++;
		for (int f = 0; f < bonds.size(); f++) {

			final IBond bond = (IBond) bonds.get(f);
			nextAtom = bond.getConnectedAtom(root);

			if (!currentPath.contains(nextAtom)) {
				newPath = new ArrayList<Object>(currentPath);
				bondSymbol = getBondLabel(bond);
				newPath.add(bondSymbol);
				newPath.add(nextAtom);
				try {
					this.checkAndStore(newPath, paths, strings, ac);
				} catch (MoltyperException e) {
					e.printStackTrace();
				}

				if (currentDepth < searchDepth) {
					this.depthFirstSearch(ac, nextAtom, paths, newPath, currentDepth, searchDepth, strings);
				}
			}
		}
	}

	/**
	 * Gets all pathes of length 1 up to the length given by the 'searchDepth"
	 * parameter. The pathes are aquired by a number of depth first searches,
	 * one for each atom.
	 * 
	 * @param ac
	 *            The AtomContainer which is to be searched.
	 * @param searchDepth
	 *            Description of the Parameter
	 */
	protected ArrayList<IFeature> findPathes(IAtomContainer ac, int searchDepth) {
		final Map<String, ArrayList<DepthFirstSearchFeature>> paths = new HashMap<String, ArrayList<DepthFirstSearchFeature>>();
		final ArrayList<IFeature> features = new ArrayList<IFeature>();
		final ArrayList<Object> currentPath = new ArrayList<Object>();
		for (int f = 0; f < ac.getAtomCount(); f++) {
			currentPath.clear();
			currentPath.add(ac.getAtom(f));
			try {
				this.checkAndStore(currentPath, paths, features, ac);
			} catch (MoltyperException e) {
				e.printStackTrace();
			}
			try {
				this.depthFirstSearch(ac, ac.getAtom(f), paths, currentPath, 0, searchDepth, features);
			} catch (MoltyperException e) {
				e.printStackTrace();
			}
		}
		return features;
	}

	/**
	 * Generates a fingerprint of the default size for the given AtomContainer.
	 * 
	 * @param ac
	 *            The AtomContainer for which a Fingerprint is generated
	 */

	@Override
	public ArrayList<IFeature> getFingerprint(IAtomContainer ac) {
		final ArrayList<IFeature> paths = this.findPathes(ac, getSearchDepth());
		return paths;
	}

	/**
	 * returns a hashed fingerprint
	 * 
	 * @param ac
	 * @param searchDepth
	 * @param hashsize
	 * @return
	 */
	public BitSet getHashFingerprint(IAtomContainer ac, int searchDepth, int hashsize) {
		final ArrayList<IFeature> paths = this.findPathes(ac, searchDepth);
		final BitSet bs = new BitSet(hashsize);

		for (int i = 0; i < paths.size(); i++) {
			final int hash = paths.get(i).hashCode();
			bs.set(new java.util.Random(hash).nextInt(hashsize - 1));
		}
		return bs;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "DFS";
	}

}
