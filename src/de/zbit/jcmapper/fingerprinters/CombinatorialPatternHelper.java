package de.zbit.jcmapper.fingerprinters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.features.NumericStringFeature;
import de.zbit.jcmapper.tools.moltyping.ExtendedAtomAndBondTyper;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;
import de.zbit.jcmapper.tools.moltyping.PharmacophorePointAssigner;
import de.zbit.jcmapper.tools.moltyping.pharmacophore.PotentialPharmacophorePoint;


public class CombinatorialPatternHelper {

	final PharmacophorePointAssigner passigner = new PharmacophorePointAssigner();

	/**
	 * returns all possible 2 point PPP patterns with max distanceCutOff. The
	 * pairwise distances are encoded in the matrix
	 * 
	 * @param ac
	 * @param matrix
	 * @param distanceCutOff
	 * @return
	 */
	public List<IFeature> getFingerprint2PointPPP(IAtomContainer ac, int[][] matrix, int distanceCutOff) {

		final PharmacophorePointAssigner passigner = new PharmacophorePointAssigner();
		final HashMap<Integer, Vector<PotentialPharmacophorePoint>> pharmacophorePoints = passigner.getPharmacophorePoints(ac);
		final ArrayList<IFeature> features = new ArrayList<IFeature>();

		for (int i = 0; i < matrix.length; i++) {
			final Vector<PotentialPharmacophorePoint> pointsAtomI = pharmacophorePoints.get(i);
			if (pointsAtomI == null) {
				continue;
			}
			for (int j = i; j < matrix.length; j++) {
				if (i == j) {
					continue;
				}
				if (matrix[i][j] > distanceCutOff) {
					continue;
				}
				final Vector<PotentialPharmacophorePoint> pointsAtomJ = pharmacophorePoints.get(j);
				if (pointsAtomJ == null) {
					continue;
				}

				for (final PotentialPharmacophorePoint pointAtomI : pointsAtomI) {
					final String phaTypeI = pointAtomI.getPharmacophoreType();

					for (final PotentialPharmacophorePoint pointAtomJ : pointsAtomJ) {
						final StringBuffer sb = new StringBuffer();
						final String phaTypeJ = pointAtomJ.getPharmacophoreType();

						if (phaTypeI.compareTo(phaTypeJ) > 0) {
							sb.append(phaTypeI);
							sb.append("-");
							sb.append(matrix[i][j]);
							sb.append("-");
							sb.append(phaTypeJ);
						} else {
							sb.append(phaTypeJ);
							sb.append("-");
							sb.append(matrix[j][i]);
							sb.append("-");
							sb.append(phaTypeI);
						}

						final NumericStringFeature feature = new NumericStringFeature(sb.toString(), 1.0);
						features.add(feature);
					}
				}
			}
		}
		return features;
	}

	/**
	 * returns all possible 3 point PPP patterns with max distanceCutOff. The
	 * pairwise distances are encoded in the matrix
	 * 
	 * @param ac
	 * @param matrix
	 * @param distanceCutOff
	 * @return
	 */
	public List<IFeature> getFingerprint3PointPPP(IAtomContainer ac, int[][] matrix, int distanceCutOff) {
		final PharmacophorePointAssigner passigner = new PharmacophorePointAssigner();
		final HashMap<Integer, Vector<PotentialPharmacophorePoint>> pharmacophorePoints = passigner.getPharmacophorePoints(ac);
		final ArrayList<IFeature> features = new ArrayList<IFeature>();

		for (int i = 0; i < matrix.length; i++) {

			final Vector<PotentialPharmacophorePoint> pointsAtomI = pharmacophorePoints.get(i);
			if (pointsAtomI == null) {
				continue;
			}

			for (int j = 0; j < matrix.length; j++) {
				if (i == j) {
					continue;
				}
				if (matrix[i][j] > distanceCutOff) {
					continue;
				}
				final Vector<PotentialPharmacophorePoint> pointsAtomJ = pharmacophorePoints.get(j);
				if (pointsAtomJ == null) {
					continue;
				}

				for (int k = 0; k < matrix.length; k++) {

					if (k == j || k == i) {
						continue;
					}
					if (matrix[j][k] > distanceCutOff) {
						continue;
					}
					if (matrix[k][i] > distanceCutOff) {
						continue;
					}

					final Vector<PotentialPharmacophorePoint> pointsAtomK = pharmacophorePoints.get(k);
					if (pointsAtomK == null) {
						continue;
					}

					for (final PotentialPharmacophorePoint pointAtomI : pointsAtomI) {
						for (final PotentialPharmacophorePoint pointAtomJ : pointsAtomJ) {
							for (final PotentialPharmacophorePoint pointAtomK : pointsAtomK) {
								final StringBuffer sb = new StringBuffer();

								sb.append(pointAtomI.getPharmacophoreType());
								sb.append("-");
								sb.append(matrix[i][j]);
								sb.append("-");
								sb.append(pointAtomJ.getPharmacophoreType());
								sb.append("-");
								sb.append(matrix[j][k]);
								sb.append("-");
								sb.append(pointAtomK.getPharmacophoreType());
								sb.append("-");
								sb.append(matrix[k][i]);

								final NumericStringFeature feature = new NumericStringFeature(sb.toString(), 1.0);
								features.add(feature);
							}
						}
					}
				}
			}
		}
		return features;
	}

	/**
	 * generates all possible patters between two atoms
	 * 
	 * @param ac
	 * @param matrix
	 * @param distanceCutoff
	 * @param typer
	 * @return
	 */
	public List<IFeature> getFingerprint2Point(IAtomContainer ac, int[][] matrix, int distanceCutoff,
			ExtendedAtomAndBondTyper typer) {
		final ArrayList<IFeature> features = new ArrayList<IFeature>();

		for (int i = 0; i < matrix.length; i++) {
			// ith atom
			String atomTypeA = null, atomTypeB = null;
			try {
				final IAtom atomA = ac.getAtom(i);
				atomTypeA = typer.getAtomLabel(atomA);
			} catch (final MoltyperException e) {
				e.printStackTrace();
			}

			// jth atom
			for (int j = i; j < matrix.length; j++) {
				if (i == j) {
					continue;
				}
				if (matrix[i][j] > distanceCutoff) {
					continue;
				}

				try {
					final IAtom atomB = ac.getAtom(j);
					atomTypeB = typer.getAtomLabel(atomB);
				} catch (final MoltyperException e) {
					e.printStackTrace();
				}

				final StringBuffer sb = new StringBuffer();
				if (atomTypeA.compareTo(atomTypeB) > 0) {
					sb.append(atomTypeA);
					sb.append("-");
					sb.append(matrix[i][j]);
					sb.append("-");
					sb.append(atomTypeB);
				} else {
					sb.append(atomTypeB);
					sb.append("-");
					sb.append(matrix[j][i]);
					sb.append("-");
					sb.append(atomTypeA);
				}
				final NumericStringFeature feature = new NumericStringFeature(sb.toString(), 1);
				features.add(feature);
			}
		}
		return features;
	}

	/**
	 * generates all possible patterns between three atoms
	 * 
	 * @param ac
	 * @param matrix
	 * @param distanceCutoff
	 * @param typer
	 * @return
	 */
	public List<IFeature> getFingerprint3Point(IAtomContainer ac, int[][] matrix, int distanceCutoff,
			ExtendedAtomAndBondTyper typer) {
		final ArrayList<IFeature> features = new ArrayList<IFeature>();

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (i == j) {
					continue;
				}
				if (matrix[i][j] > distanceCutoff) {
					continue;
				}

				for (int k = 0; k < matrix.length; k++) {
					if ((i == k) || (j == k)) {
						continue;
					}
					if (matrix[j][k] > distanceCutoff) {
						continue;
					}
					if (matrix[k][i] > distanceCutoff) {
						continue;
					}

					final StringBuffer sb = new StringBuffer();
					final IAtom atomA = ac.getAtom(i);
					final IAtom atomB = ac.getAtom(j);
					final IAtom atomC = ac.getAtom(k);

					try {
						sb.append(typer.getAtomLabel(atomA));
						sb.append("-");
						sb.append(matrix[i][j]);
						sb.append("-");
						sb.append(typer.getAtomLabel(atomB));
						sb.append("-");
						sb.append(matrix[j][k]);
						sb.append("-");
						sb.append(typer.getAtomLabel(atomC));
						sb.append("-");
						sb.append(matrix[k][i]);
					} catch (final MoltyperException e) {
						e.printStackTrace();
					}

					final NumericStringFeature feature = new NumericStringFeature(sb.toString(), 1.0);
					features.add(feature);
				}
			}
		}
		return features;
	}
}
