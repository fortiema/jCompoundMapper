package de.zbit.jcmapper.tools.tree.trie.pattern;

import java.util.ArrayList;

public class PatternContainer {
	private int count = 1;
	private double numericValue;
	private ArrayList<InterfacePattern> patterns = new ArrayList<InterfacePattern>();

	public void addSimplePattern(InterfacePattern p) {
		this.patterns.add(p);
	}

	public double getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(double numericValue) {
		this.numericValue = numericValue;
	}

	public String getCompleteStringPattern() {
		String pattern = "";
		for (int i = 0; i < this.patterns.size(); i++) {
			pattern = pattern + this.patterns.get(i).getNumLabel() + ">";
		}
		return pattern;
	}

	public int getCount() {
		return this.count;
	}

	public ArrayList<InterfacePattern> getPatterns() {
		return this.patterns;
	}

	@Override
	public int hashCode() {
		return this.patterns.hashCode();
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setPatterns(ArrayList<InterfacePattern> patterns) {
		this.patterns = patterns;
	}

}
