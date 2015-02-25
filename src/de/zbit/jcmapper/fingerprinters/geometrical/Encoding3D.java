package de.zbit.jcmapper.fingerprinters.geometrical;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtomContainer;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;


public abstract class Encoding3D extends EncodingFingerprint {

 	private double stretchingFactor = 1.0;
 	private double distanceCutoff = 10;

	/**
	 * computes the distance matrix of all atoms corrected by stretching factor
	 * and mathematically rounded
	 * 
	 * @param ac
	 * @return
	 */
	public int[][] computeDistanceMatrix(IAtomContainer ac) {
		final int dimension = ac.getAtomCount();
		final int[][] distanceMatrix = new int[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = i; j < dimension; j++) {
				final Point3d start = ac.getAtom(i).getPoint3d();
				final Point3d end = ac.getAtom(j).getPoint3d();
				
				double distance;
				if(start == null || end == null){
					final Point2d start2d = ac.getAtom(i).getPoint2d();
					final Point2d end2d = ac.getAtom(j).getPoint2d();
					distance = Math.round(start2d.distance(end2d) * this.getStretchingFactor());					
				}else{
					distance = Math.round(start.distance(end) * this.getStretchingFactor());					
				}
				distanceMatrix[i][j] = (int) distance;
				distanceMatrix[j][i] = (int) distance;
			}
		}
		return distanceMatrix;
	}

	public double getDistanceCutoff() {
		return this.distanceCutoff;
	}

	public double getStretchingFactor() {
		return this.stretchingFactor;
	}

	public void setDistanceCutoff(double distanceCutoff) {
		this.distanceCutoff = distanceCutoff;
	}

	public void setStretchingFactor(double StretchingFactor) {
		this.stretchingFactor = StretchingFactor;
	}
}
