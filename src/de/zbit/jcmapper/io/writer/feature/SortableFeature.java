package de.zbit.jcmapper.io.writer.feature;

import de.zbit.jcmapper.fingerprinters.features.IFeature;

public class SortableFeature implements Comparable<SortableFeature> {
	final IFeature pattern;
	final int hash;
	final boolean useAromaticityFlag;

	public SortableFeature(IFeature feature, boolean useAromaticityFlag) {
		this.pattern = feature;
		this.hash = feature.featureToString(useAromaticityFlag).hashCode();
		this.useAromaticityFlag=useAromaticityFlag;
	}

	public int getHash() {
		return hash;
	}

	public double getValue() {
		return pattern.getValue();
	}

	public IFeature getFeature() {
		return pattern;
	}
	
	public String getString() {
		return this.pattern.featureToString(useAromaticityFlag);
	}

	public String getString(boolean useAromaticityFlag) {
		return this.pattern.featureToString(useAromaticityFlag);
	}

	@Override
	public int compareTo(SortableFeature that) {
		return this.hash - ((SortableFeature) that).getHash();
	}
}