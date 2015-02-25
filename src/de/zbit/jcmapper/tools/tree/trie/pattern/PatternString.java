package de.zbit.jcmapper.tools.tree.trie.pattern;

public class PatternString implements InterfacePattern {
	private int label;
	private String pattern;
	private Float weight = null;

	public int compareTo(PatternString o) {
		if (this.label < o.getNumLabel()) {
			return -1;
		} else {
			return 1;
		}
	}

	public String getLabel() {
		return this.pattern;
	}

	@Override
	public int getNumLabel() {
		return this.label;
	}

	public String getPattern() {
		return this.pattern;
	}

	@Override
	public Float getWeight() {
		return this.weight;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
		this.label = this.pattern.hashCode();
	}

	@Override
	public void setWeight(Float weight) {
		this.weight = weight;
	}
}
