package de.zbit.jcmapper.tools.tree.trie.pattern;

public interface InterfacePattern {

	// numeric label for the pattern
	public int getNumLabel();

	// get a weight for this pattern
	public Float getWeight();

	// set a weight for this pattern
	public void setWeight(Float weight);

}
