package de.zbit.jcmapper.tools.moltyping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

import de.zbit.jcmapper.tools.moltyping.pharmacophore.PPPDefinitionFileReader;
import de.zbit.jcmapper.tools.moltyping.pharmacophore.PotentialPharmacophorePoint;
import de.zbit.jcmapper.tools.moltyping.pharmacophore.PotentialPharmacophorePointPair;


public class PharmacophorePointAssigner {
	private final ArrayList<PotentialPharmacophorePoint> ppps;

	public PharmacophorePointAssigner() {
		final PPPDefinitionFileReader PPPreader = new PPPDefinitionFileReader();
		this.ppps = PPPreader.readPharmacophoreDefinitions();
	}

	private HashMap<Integer, Vector<PotentialPharmacophorePoint>> assignPharmacophorePoints(IAtomContainer mol) {
		// Store the PotentialPharmacophorePoint assignment in a HashMap
		// The key is the atom index and the value a vector of PPPs
		// Note an atom can represent multiple PPPs (e.g. Negative and Acceptor)

		final HashMap<Integer, Vector<PotentialPharmacophorePoint>> PPPAssignment = new HashMap<Integer, Vector<PotentialPharmacophorePoint>>();

		// Now math the individual patterns
		for (final PotentialPharmacophorePoint ppp : this.ppps) {
			final List<List<Integer>> hits = this.getAtoms4SMARTS(ppp.getSMARTSPattern(), mol);
			for (int i = 0, n = hits.size(); i < n; i++) {
				for (int j = 0, n1 = hits.get(i).size(); j < n1; j++) {
					if (!PPPAssignment.containsKey(hits.get(i).get(j))) {
						final Vector<PotentialPharmacophorePoint> temp = new Vector<PotentialPharmacophorePoint>();
						temp.add(ppp);
						PPPAssignment.put(hits.get(i).get(j), temp);
					} else {
						PPPAssignment.get(hits.get(i).get(j)).add(ppp);
					}
				}
			}
		}
		//We do not want to see all those non-matching patterns
		//if (PPPAssignment.size() == 0) {
		//	System.out.println("[PPP-Typer] Warning: zero pharmacophore points");
		//}

		return PPPAssignment;
	}

	/**
	 * This method performs the SMARTS matching as implemented in the CDK
	 * 
	 * @param pattern
	 *            - Valid SMARTS pattern
	 * @param mol
	 *            - IAtomContainer
	 * @return matches - List<List<Integer>> List of Lists, containing the atom
	 *         indices of the matches
	 */
	private List<List<Integer>> getAtoms4SMARTS(String pattern, IAtomContainer mol) {
		try {
			final SMARTSQueryTool sqt = new SMARTSQueryTool("C", DefaultChemObjectBuilder.getInstance());
			sqt.setSmarts(pattern);
			sqt.matches(mol);
			return sqt.getUniqueMatchingAtoms();
		} catch (final CDKException e) {
			// e.printStackTrace();
			//We do not want to see all those non-matching patterns
			//System.out.println("Warning: SMARTS search skipped because of timeout. Pattern: " + pattern);
			ArrayList<List<Integer>> list = new ArrayList<List<Integer>>();
			return list;
		}
	}

	/**
	 * returns a vector of potential pharmacophore points for a molecule
	 * 
	 * @param mol
	 * @return
	 */
	public HashMap<Integer, Vector<PotentialPharmacophorePoint>> getPharmacophorePoints(IAtomContainer mol) {
		final HashMap<Integer, Vector<PotentialPharmacophorePoint>> PPPAssignment = this.assignPharmacophorePoints(mol);
		this.lipophilicCarbonAnnotation(PPPAssignment, mol);
		this.negativeCOOHPOOHSOOHDetection(PPPAssignment, mol);
		
		//TODO
		//System.out.println(PPPAssignment.toString());
		return PPPAssignment;
	}

	private boolean isCOOHPOOHSOOHGroup(IAtom a, IAtomContainer mol) {
		int falseNeighbor = 0;
		boolean doubleBondSeen = false;
		boolean singleBondSeen = false;
		boolean hydrogenSeen = false;

		for (final IAtom n : mol.getConnectedAtomsList(a)) {
			if (!n.getAtomicNumber().equals(8)) {
				if (falseNeighbor == 1) {
					return false;
				} else {
					falseNeighbor++;
				}
			} else {
				// Oxygen neighbor check bond type
				final IBond b = mol.getBond(a, n);
				if (b.getOrder().equals(IBond.Order.SINGLE)) {
					if (!singleBondSeen) {
						singleBondSeen = true;
						if (n.getImplicitHydrogenCount() > 0)
							hydrogenSeen = true;
					} else {
						return false;
					}
				} else if (b.getOrder().equals(IBond.Order.DOUBLE)) {
					if (!doubleBondSeen) {
						doubleBondSeen = true;
					} else {
						return false;
					}
				}
			}
		}
		if (doubleBondSeen && singleBondSeen && hydrogenSeen) {
			return true;
		} else {
			return false;
		}
	}

	private void lipophilicCarbonAnnotation(HashMap<Integer, Vector<PotentialPharmacophorePoint>> PPPAssignment,
			IAtomContainer mol) {
		// First create the PPP object
		final PotentialPharmacophorePoint ppp = new PotentialPharmacophorePoint("L", "No SMARTS available",
				"Carbon atoms only adjacent to carbon atoms");

		for (final IAtom a : mol.atoms()) {
			if (a.getAtomicNumber() == 6) {
				// Check if all neighbors are carbon atoms
				if (this.neighborsCarbonAtom(mol, a)) {
					// Append entry to the HashMap
					if (!PPPAssignment.containsKey(mol.getAtomNumber(a))) {
						final Vector<PotentialPharmacophorePoint> temp = new Vector<PotentialPharmacophorePoint>();
						temp.add(ppp);
						PPPAssignment.put(mol.getAtomNumber(a), temp);
					} else {
						PPPAssignment.get(mol.getAtomNumber(a)).add(ppp);
					}
				}
			}
		}
	}

	private void negativeCOOHPOOHSOOHDetection(HashMap<Integer, Vector<PotentialPharmacophorePoint>> PPPAssignment,
			IAtomContainer mol) {
		// Search for COOH, POOH, and SOOH groups
		final PotentialPharmacophorePoint ppp = new PotentialPharmacophorePoint("N", "no pattern",
				"carbon, sulfur, or phosphorus atom of COOH, SOOH, or POOH");
		for (int i = 0; i < mol.getAtomCount(); i++) {
			final IAtom a = mol.getAtom(i);
			if (a.getAtomicNumber().equals(6) || a.getAtomicNumber().equals(15) || a.getAtomicNumber().equals(16)) {
				// Possible starting point of group
				// Check neighbor
				if (this.isCOOHPOOHSOOHGroup(a, mol)) {
					if (!PPPAssignment.containsKey(mol.getAtomNumber(a))) {
						final Vector<PotentialPharmacophorePoint> temp = new Vector<PotentialPharmacophorePoint>();
						temp.add(ppp);
						PPPAssignment.put(mol.getAtomNumber(a), temp);
					} else {
						PPPAssignment.get(mol.getAtomNumber(a)).add(ppp);
					}
				}
			}
		}
	}

	/**
	 * This method performs the limited DFS search at an carbon atom a. The
	 * method returns true if all adjacent atoms are carbons.
	 * 
	 * @param mol
	 *            - IAtomContainer
	 * @param a
	 *            - IAtom Root atom for the neighbor check
	 * @return boolean - True if all neighbors of a are carbon atoms, otherwise
	 *         false
	 */
	private boolean neighborsCarbonAtom(IAtomContainer mol, IAtom a) {
		for (final IAtom n : mol.getConnectedAtomsList(a)) {
			// Ignore hydrogen atoms
			if (n.getAtomicNumber() == 1) {
				continue;
			}
			if (n.getAtomicNumber() != 6) {
				return false;
			}
		}
		return true;
	}

	/**
	 * returns the bit position in the CATS autocorrelation vector
	 * @param p
	 * @param searchDepth
	 * @return
	 */
	public int getIndex4PPPP(PotentialPharmacophorePointPair p, int searchDepth) {
		final String pharmacophore1 = p.getFirstPPP().getPharmacophoreType();
		final String pharmacophore2 = p.getSecondPPP().getPharmacophoreType();
		final String pp = pharmacophore1 + pharmacophore2;
		int index = 0;
		final int offset = searchDepth+1;
		
		if (pp.equals("AA")) {
			index = offset*0;
		} else if (pp.equals("AD")) {
			index = offset*1;
		} else if (pp.equals("AL")) {
			index = offset*2;
		} else if (pp.equals("AN")) {
			index = offset*3;
		} else if (pp.equals("AP")) {
			index = offset*4;
		} else if (pp.equals("DD")) {
			index = offset*5;
		} else if (pp.equals("DL")) {
			index = offset*6;
		} else if (pp.equals("DN")) {
			index = offset*7;
		} else if (pp.equals("DP")) {
			index = offset*8;
		} else if (pp.equals("LL")) {
			index = offset*9;
		} else if (pp.equals("LN")) {
			index = offset*10;
		} else if (pp.equals("LP")) {
			index = offset*11;
		} else if (pp.equals("NN")) {
			index = offset*12;
		} else if (pp.equals("NP")) {
			index = offset*13;
		} else if (pp.equals("PP")) {
			index = offset*14;
		}
		index += (p.getDistance());
		return index;
	}

	
	public HashSet<PotentialPharmacophorePointPair> assignPairs(AtomContainer mol,
			HashMap<Integer, Vector<PotentialPharmacophorePoint>> PPPAssignment, int[][] matrix) {
		final HashSet<PotentialPharmacophorePointPair> Pairs = new HashSet<PotentialPharmacophorePointPair>();

		// Now create the ordered pairwise PPP objects
		for (int i = 0; i < mol.getAtomCount(); i++) {
			if (!PPPAssignment.containsKey(i)) {
				// No PPP for this atom
				continue;
			}
			// Iterate over all assignments of the first atom
			for (final PotentialPharmacophorePoint ppp1 : PPPAssignment.get(i)) {
				for (int j = i; j < mol.getAtomCount(); j++) {
					if (!PPPAssignment.containsKey(j)) {
						// No PPP for this atom
						continue;
					}
					// Iterate over all assignments of the second atom
					for (final PotentialPharmacophorePoint ppp2 : PPPAssignment.get(j)) {
						final PotentialPharmacophorePointPair pppp = new PotentialPharmacophorePointPair(ppp1, i, ppp2,	j);
						pppp.setDistance(matrix[i][j]);
						if (!Pairs.contains(pppp)) {
							Pairs.add(pppp);
						}
					}
				}
			}
		}
		return Pairs;
	}
}
