package de.zbit.jcmapper.tools.tree.trie;

/**
 * 
 * a simple container to store the informatio n of a trie traversal
 * 
 * @author hinselma
 * 
 */
public class TrieResultContainer {
	private double dsimilarity = 0.0;
	private int isimilarity = 0;

	private int maxAB = 0;

	private int minAB = 0;
	private int somevalue1 = 0;
	private int somevalue2 = 0;

	private int somevalue3 = 0;
	private double somevalue4 = 0;

	public double getDsimilarity() {
		return this.dsimilarity;
	}

	public int getMaxAB() {
		return this.maxAB;
	}

	public int getMinAB() {
		return this.minAB;
	}

	public int getSimilarity() {
		return this.isimilarity;
	}

	public int getSomevalue1() {
		return this.somevalue1;
	}

	public int getSomevalue2() {
		return this.somevalue2;
	}

	public int getSomevalue3() {
		return this.somevalue3;
	}

	public double getSomevalue4() {
		return this.somevalue4;
	}

	public void setDsimilarity(double dsimilarity) {
		this.dsimilarity = dsimilarity;
	}

	public void setMaxAB(int maxAB) {
		this.maxAB = maxAB;
	}

	public void setMinAB(int minAB) {
		this.minAB = minAB;
	}

	public void setSimilarity(int similarity) {
		this.isimilarity = similarity;
	}

	public void setSomevalue1(int somevalue1) {
		this.somevalue1 = somevalue1;
	}

	public void setSomevalue2(int somevalue2) {
		this.somevalue2 = somevalue2;
	}

	public void setSomevalue3(int somevalue3) {
		this.somevalue3 = somevalue3;
	}

	public void setSomevalue4(double somevalue4) {
		this.somevalue4 = somevalue4;
	}

}
