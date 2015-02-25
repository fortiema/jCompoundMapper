package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;


public abstract class Encoding2D extends EncodingFingerprint {

	public class PathFeature {
		private final List<IAtom> atomSequence;
		private final IAtomContainer ac;
		private final String canonicalStringRepresentation;

		public PathFeature(List<IAtom> atomsequence, IAtomContainer ac) {
			this.atomSequence = atomsequence;
			this.ac = ac;
			this.canonicalStringRepresentation = this.generateCanonicalString();
		}

		private String createBackwardStringRepresention() {
			final StringBuffer sb = new StringBuffer();
			for (int i = this.atomSequence.size() - 1; i > 0; i--) {
				final IAtom atom = this.atomSequence.get(i);
				final IBond bond = this.ac.getBond(this.atomSequence.get(i), this.atomSequence.get(i - 1));
				try {
					sb.append(Encoding2D.this.getAtomLabel(atom) + Encoding2D.this.getBondLabel(bond));
				} catch (final MoltyperException e) {
					e.printStackTrace();
				}
			}
			try {
				sb.append(Encoding2D.this.getAtomLabel(this.atomSequence.get(0)));
			} catch (final MoltyperException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}

		private String createForwardStringRepresention() {
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < this.atomSequence.size() - 1; i++) {
				final IAtom atom = this.atomSequence.get(i);
				final IBond bond = this.ac.getBond(this.atomSequence.get(i), this.atomSequence.get(i + 1));
				try {
					sb.append(Encoding2D.this.getAtomLabel(atom) + Encoding2D.this.getBondLabel(bond));
				} catch (final MoltyperException e) {
					e.printStackTrace();
				}
			}
			try {
				sb.append(Encoding2D.this.getAtomLabel(this.atomSequence.get(this.atomSequence.size() - 1)));
			} catch (final MoltyperException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}

		private String generateCanonicalString() {
			final String forwardStringRepresentation = this.createForwardStringRepresention();
			final String backwardStringRepresentation = this.createBackwardStringRepresention();
			final ArrayList<String> result = new ArrayList<String>();
			result.add(forwardStringRepresentation);
			result.add(backwardStringRepresentation);
			Collections.sort(result);
			return result.get(0);
		}

		@Override
		public String toString() {
			return this.canonicalStringRepresentation;
		}
	}

	protected int searchDepth = 8;

	public int getSearchDepth() {
		return this.searchDepth;
	}

	public void setSearchDepth(int searchDepth) {
		this.searchDepth = searchDepth;
	}

//	/**
//	 * uses Johnson's algorithm to compute the graph features by iteratively
//	 * applying Dijkstra's algorithm
//	 * 
//	 * @param ac
//	 * @return
//	 */
//	public int[][] computeShortestPathMatrix(IAtomContainer ac) {
//		int dim = ac.getAtomCount();
//		int[][] matrix = new int[dim][dim];
//
//		for (int i = 0; i < dim; i++) {
//			for (int j = i; j < dim; j++) {
//				if (i != j) {
//					List<IAtom> shortestPath = PathTools.getShortestPath(ac, ac.getAtom(i), ac.getAtom(j));
//					int shortestPathLength = shortestPath.size();
// 					matrix[i][j] = shortestPathLength;
//					matrix[j][i] = shortestPathLength;
//				}else{
//					matrix[i][j] = 0;
//				}
//			}
//		}
//
//		return matrix;
//	}
}
