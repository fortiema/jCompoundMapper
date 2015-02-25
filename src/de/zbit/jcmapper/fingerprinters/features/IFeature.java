package de.zbit.jcmapper.fingerprinters.features;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public interface IFeature extends Comparable<IFeature>{

	public abstract String featureToString(boolean useAromaticFlag);

	public abstract double getValue();
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object feature);
	
	public abstract Iterable<IAtom> representedAtoms();
	
	public abstract Iterable<IBond> representedBonds();
	
}