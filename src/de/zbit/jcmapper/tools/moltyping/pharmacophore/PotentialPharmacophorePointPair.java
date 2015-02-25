package de.zbit.jcmapper.tools.moltyping.pharmacophore;

//public class PotentialPharmacophorePointPair implements Comparable<PotentialPharmacophorePointPair> {
public class PotentialPharmacophorePointPair{
	private int topologicalDistance;
	private int atomIndex1, atomIndex2;
	private PotentialPharmacophorePoint PPPFirst, PPPSecond;

	public PotentialPharmacophorePointPair(PotentialPharmacophorePoint ppp1, int atomIndex1, PotentialPharmacophorePoint ppp2, int atomIndex2) {
		// Define order of the pharmacophore type
		if (ppp1.getPharmacophoreType().compareTo(ppp2.getPharmacophoreType()) > 0) {
			this.PPPFirst = ppp2;
			this.PPPSecond = ppp1;
		} else {
			this.PPPFirst = ppp1;
			this.PPPSecond = ppp2;
		}
		// Define ordering of the atom indices
		if (atomIndex1 > atomIndex2) {
			this.atomIndex1 = atomIndex2;
			this.atomIndex2 = atomIndex1;
		} else {
			this.atomIndex1 = atomIndex1;
			this.atomIndex2 = atomIndex2;
		}
	}
 

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof PotentialPharmacophorePointPair) {
			final PotentialPharmacophorePointPair temp = (PotentialPharmacophorePointPair) arg0;
			if (this.atomIndex1 == temp.atomIndex1 && this.atomIndex2 == temp.atomIndex2) {
				if (this.PPPFirst.equals(temp.PPPFirst) && this.PPPSecond.equals(temp.PPPSecond)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public PotentialPharmacophorePoint getFirstPPP() {
		return this.PPPFirst;
	}

	public PotentialPharmacophorePoint getSecondPPP() {
		return this.PPPSecond;
	}

	public int getDistance() {
		return this.topologicalDistance;
	}

	public void setDistance(int distance) {
		this.topologicalDistance = distance;
	}

	@Override
	public String toString() {
		return this.PPPFirst.getPharmacophoreType() + this.PPPSecond.getPharmacophoreType() + "-" + this.atomIndex1 + "-" + this.atomIndex2;
	}
}
