package de.zbit.jcmapper.tools.tree.trie.gml;

public class GMLNode {

	private int attribute = 0;
	private String color = ConstantsYEditColor.WHITE;
	private int id = 0;
	private String label = "Node";

	public GMLNode(int id) {
		this.id = id;
	}

	public int getAttribute() {
		return this.attribute;
	}

	public String getColor() {
		return this.color;
	}

	/**
	 * returns a gml node string representation for this node
	 */
	public String getGMLNodeString() {
		String rep = "\tnode [\n";
		rep = rep + "\t\tid " + this.getId() + "\n";
		rep = rep + "\t\tlabel \"" + this.getLabel() + "\"\n";
		rep = rep + "\t\tattribute " + this.getAttribute() + "\n";

		// graphics tag
		rep = rep + "\t\tgraphics [\n";
		rep = rep + "\t\tfill \"" + this.getColor() + "\"\n";
		rep = rep + "\t\ttype\t\""+"ellipse\"\n";
		rep = rep + "\t\tdropShadowColor\t\"#B3A691\"\n";
		rep = rep + "\t\tdropShadowOffsetX	5\n";
		rep = rep + "\t\t]\n";
		rep = rep + "\t]\n";
		return rep;
	}

	public int getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
