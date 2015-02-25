package de.zbit.jcmapper.fingerprinters.topological.features;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.features.IFeature;

public class PositionFeature implements IFeature {
	
	private int position;
	private double value;
	
	public PositionFeature(int position){
		this.position = position;
		this.value = 1;
	}

	@Override
	public boolean equals(Object obj) {
		final int hashOther = ((IFeature) obj).hashCode();
		return (this.hashCode() == hashOther);
	}
	
		
	@Override
	public String featureToString(boolean useAromaticFlag) {
		return "MACCS-" + this.position;
	}
	
	@Override
	public int compareTo(IFeature arg0) {
		return ((Integer) this.hashCode()).compareTo(arg0.hashCode());
	}

	@Override
	public double getValue() {
		return this.value;
	}
	
	@Override
	public int hashCode(){
		return this.position;
	}

	@Override
	public Iterable<IAtom> representedAtoms() {
		return null;
	}

	@Override
	public Iterable<IBond> representedBonds() {
		return null;
	}
}
