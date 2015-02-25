package de.zbit.jcmapper.tools.moltyping.pharmacophore;

public class PotentialPharmacophorePoint implements Comparable<PotentialPharmacophorePoint> {
	private final int hash;
	private final String pharmacophoreType;
	private final String SMARTS;
	private final String Description;

	public PotentialPharmacophorePoint(String pharmacophoreType, String SMARTS, String Description) {
		this.pharmacophoreType = pharmacophoreType;
		this.hash = pharmacophoreType.hashCode();
		this.SMARTS = SMARTS;
		this.Description = Description;
	}

	@Override
	public int compareTo(PotentialPharmacophorePoint arg0) {
		if(this.hash > arg0.hash){
			return 1;
		}else{
			return -1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hash == ((PotentialPharmacophorePoint) obj).hashCode();
	}

	@Override
	public int hashCode() {
		return this.hash;
	}
	
	public String getDescription() {
		return this.Description;
	}

	public String getPharmacophoreType() {
		return this.pharmacophoreType;
	}

	public String getSMARTSPattern() {
		return this.SMARTS;
	}

	@Override
	public String toString() {
		return this.pharmacophoreType + " - " + this.SMARTS;
	}

}
