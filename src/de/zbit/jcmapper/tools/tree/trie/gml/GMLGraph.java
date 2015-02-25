package de.zbit.jcmapper.tools.tree.trie.gml;

import java.util.ArrayList;

/*	graph [
 comment "This is a sample graph"
 directed 1
 id 42
 label "Hello, I am a graph"
 node [
 id 1
 label "Node 1"
 thisIsASampleAttribute 42
 ]
 node [
 id 2
 label "node 2"
 thisIsASampleAttribute 43
 ]
 node [
 id 3
 label "node 3"
 thisIsASampleAttribute 44
 ]
 edge [
 source 1
 target 2
 label "Edge from node 1 to node 2"
 ]
 edge [
 source 2
 target 3
 label "Edge from node 2 to node 3"
 ]
 edge [
 source 3
 target 1
 label "Edge from node 3 to node 1"
 ]
 ]*/

public class GMLGraph {
	private String comment;
	private int directed = 1;
	private final ArrayList<GMLEdge> edges = new ArrayList<GMLEdge>();
	private int id = 0;

	private String label;
	private final ArrayList<GMLNode> nodes = new ArrayList<GMLNode>();

	public void addEdge(GMLEdge edge) {
		this.edges.add(edge);
	}

	public void addNode(GMLNode node) {
		this.nodes.add(node);
	}

	public String getComment() {
		return this.comment;
	}

	public int getDirected() {
		return this.directed;
	}

	public int getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDirected(int directed) {
		this.directed = directed;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("graph [\n");
		sb.append("\tcomment \"" + this.getComment() + "\"\n");
		sb.append("\tdirected " + this.getDirected() + "\n");
		sb.append("\tid " + this.getId() + "\n");
		sb.append("\tlabel \"" + this.getLabel() + "\"\n");

		for (int i = 0; i < this.nodes.size(); i++) {
			sb.append(this.nodes.get(i).getGMLNodeString());
		}

		for (int i = 0; i < this.edges.size(); i++) {
			sb.append(this.edges.get(i).getGMLEdgeString());
		}

		sb.append("]\n");

		return sb.toString();
	}

}
