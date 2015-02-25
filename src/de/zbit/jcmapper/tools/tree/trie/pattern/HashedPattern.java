package de.zbit.jcmapper.tools.tree.trie.pattern;

public class HashedPattern implements InterfacePattern {
	private int label;
	private Float weight = null;

	public int compareTo(HashedPattern o) {
		if (this.label < o.getNumLabel()) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public int getNumLabel() {
		return this.label;
	}

	@Override
	public Float getWeight() {
		return this.weight;
	}

	public void setPattern(String pattern) {
		final int hash = pattern.hashCode();
		this.label = new java.util.Random(hash).nextInt(HashedPatternConstants.MAX_DEPTH);
	}

	@Override
	public void setWeight(Float weight) {
		this.weight = weight;
	}

}
