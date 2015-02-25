package de.zbit.jcmapper.tools.tree.trie.pattern;

public class Pattern implements InterfacePattern {
	private int label;
	private Float weight = null;

	public int compareTo(Pattern o) {
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
		this.label = pattern.hashCode();
		new java.util.Random(500);
	}

	@Override
	public void setWeight(Float weight) {
		this.weight = weight;
	}
}
