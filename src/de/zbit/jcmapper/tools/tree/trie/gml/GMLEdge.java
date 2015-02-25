package de.zbit.jcmapper.tools.tree.trie.gml;

public class GMLEdge {
	private String label = "";
	private int source = 0;
	private int target = 0;

	/**
	 * returns an edge in GML string encoding
	 */
	public String getGMLEdgeString() {
		final String rep = "\tedge [\n" + "\t\tsource " + this.getSource() + "\n" + "\t\ttarget " + this.getTarget() + "\n" + "\t\tlabel \"" + this.getLabel() + "\"\n" + "\t]\n";
		return rep;
	}

	public String getLabel() {
		return this.label;
	}

	public int getSource() {
		return this.source;
	}

	public int getTarget() {
		return this.target;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	/**
	 * edge [ source 1 target 2 label "Edge from node 1 to node 2" ]
	 */
}
