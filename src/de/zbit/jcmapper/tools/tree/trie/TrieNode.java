package de.zbit.jcmapper.tools.tree.trie;

import java.io.Serializable;
import java.util.ArrayList;

import de.zbit.jcmapper.tools.tree.trie.pattern.InterfacePattern;
import de.zbit.jcmapper.tools.tree.trie.pattern.PatternString;


public class TrieNode implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private ArrayList<TrieNode> children = new ArrayList<TrieNode>();
	private int count;
	private boolean feature;
	private String label = null;
	private int numlabel;
	private Float weight = null;

	public TrieNode() {

	}

	public TrieNode(InterfacePattern pattern) {
		if (pattern instanceof PatternString) {
			this.label = ((PatternString) pattern).getLabel();
		}
		final Float w = pattern.getWeight();
		if (w != null) {
			this.weight = w;
		}
		this.numlabel = pattern.getNumLabel();
	}

	public void addChildNode(TrieNode n) {
		this.children.add(n);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (final CloneNotSupportedException e) {
			// This should never happen
			throw new InternalError(e.toString());
		}
	}

	public ArrayList<TrieNode> getChildren() {
		return this.children;
	}

	public int getCount() {
		return this.count;
	}

	public String getLabel() {
		return this.label;
	}

	public TrieNode getLastChildNode() {
		return this.children.get(this.children.size() - 1);
	}

	public int getNumlabel() {
		return this.numlabel;
	}

	public Float getWeight() {
		return this.weight;
	}

	public boolean isFeature() {
		return this.feature;
	}

	public void setChildren(ArrayList<TrieNode> children) {
		this.children = children;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setFeature(boolean isfeature) {
		this.feature = isfeature;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setNumlabel(int numlabel) {
		this.numlabel = numlabel;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

}
