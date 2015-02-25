package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;


public class Encoding2DLocalAtomEnvironment extends Encoding2D {

	private boolean useBonds = true;

	public Encoding2DLocalAtomEnvironment() {
		super.setSearchDepth(6);
	}

	@Override
	public ArrayList<IFeature> getFingerprint(IAtomContainer ac) {
		final ArrayList<IFeature> result = new ArrayList<IFeature>();
		for (int i = 0; i < ac.getAtomCount(); i++) {
			final List<List<IAtom>> lae = PathTools.getPathsOfLengthUpto(ac, ac.getAtom(i), super.getSearchDepth());
			HashMap<Integer, List<List<IAtom>>> laeMap = getFeaturesBySearchDepth(lae);
			Set<Integer> keySet = laeMap.keySet();

			for (Integer key : keySet) {
				List<String> localFragment = new ArrayList<String>();
				try {
					localFragment = this.getPaths(laeMap.get(key), ac);
				} catch (MoltyperException e) {
					e.printStackTrace();
				}
				final String shellD = localFragment.toString().replaceAll(" ", "");
				final NumericStringFeature feature = new NumericStringFeature(shellD, 1.0);
				result.add(feature);
			}
		}
		return result;
	}

	private HashMap<Integer, List<List<IAtom>>> getFeaturesBySearchDepth(List<List<IAtom>> lae) {
		HashMap<Integer, List<List<IAtom>>> result = new HashMap<Integer, List<List<IAtom>>>();

		for (List<IAtom> laei : lae) {
			int searchDepth = laei.size();
			if (result.containsKey(searchDepth)) {
				result.get(searchDepth).add(laei);
			} else {
				List<List<IAtom>> newFragment = new ArrayList<List<IAtom>>();
				newFragment.add(laei);
				result.put(searchDepth, newFragment);
			}
		}
		return result;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "RadialStarFingerprint";
	}

	/**
	 * returns a sorted lists of paths describing the environment
	 * 
	 * @param localAtomEnvironment
	 * @return
	 * @throws MoltyperException
	 */
	private ArrayList<String> getPaths(List<List<IAtom>> localAtomEnvironment, IAtomContainer ac)
			throws MoltyperException {
		final ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < localAtomEnvironment.size(); i++) {
			final List<IAtom> iThPath = localAtomEnvironment.get(i);
			final StringBuffer sb = new StringBuffer();
			for (int j = 0; j < iThPath.size(); j++) {
				sb.append(super.getAtomLabel(iThPath.get(j)));
				if (this.useBonds) {
					if ((j < iThPath.size() - 1) && (iThPath.size() > 1)) {
						final int bondindex = ac.getBondNumber(iThPath.get(j), iThPath.get(j + 1));
						sb.append(super.getBondLabel(ac.getBond(bondindex)));
					}
				}
			}
			result.add(sb.toString());
		}
		Collections.sort(result);
		return result;
	}

	public boolean isUseBonds() {
		return this.useBonds;
	}

	public void setUseBonds(boolean useBonds) {
		this.useBonds = useBonds;
	}
}
