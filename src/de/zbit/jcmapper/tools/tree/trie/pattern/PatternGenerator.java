package de.zbit.jcmapper.tools.tree.trie.pattern;

import java.util.ArrayList;
import java.util.Collections;

public class PatternGenerator {

	// private static SymbolDistribution sdistribution = new
	// SymbolDistribution();

	/**
	 * returns the index of the next bond symbol
	 * 
	 * @param str
	 * @return
	 */
	private static int getNextBondSymbol(String str) {

		final ArrayList<Integer> indices = new ArrayList<Integer>();

		int start1 = str.indexOf("-");
		if (start1 < 0) {
			start1 = Integer.MAX_VALUE;
		}
		int start2 = str.indexOf("=");
		int start3 = str.indexOf("#");
		int start4 = str.indexOf(":");

		if (start1 < 0) {
			start1 = Integer.MAX_VALUE;
		}

		if (start2 < 0) {
			start2 = Integer.MAX_VALUE;
		}

		if (start3 < 0) {
			start3 = Integer.MAX_VALUE;
		}

		if (start4 < 0) {
			start4 = Integer.MAX_VALUE;
		}

		indices.add(start1);
		indices.add(start2);
		indices.add(start3);
		indices.add(start4);

		Collections.sort(indices);

		return indices.get(0);
	}

	/**
	 * creates a simple pattern by decomposing a string to its characters
	 * 
	 * @param pattern
	 * @return
	 */
	public static PatternContainer getPattern4String(String pattern, int blocksize) {

		final PatternContainer c = new PatternContainer();

		for (int i = 0; i < pattern.length(); i = i + blocksize) {
			final PatternString p = new PatternString();

			String t = "";
			for (int b = 0; b < blocksize; b++) {
				t = t + pattern.charAt(i + b);
			}

			p.setPattern(t);
			c.addSimplePattern(p);
		}
		return c;
	}

	/**
	 * creates a simple pattern by decomposing a string to its characters, this
	 * function weights the position in the string
	 * 
	 * @param pattern
	 * @return
	 */
	public static PatternContainer getPatternsBondSeparated(String pattern) {

		final PatternContainer c = new PatternContainer();

		boolean finished = false;
		String temp = new String(pattern);

		while (!finished) {
			final int pos = getNextBondSymbol(temp);
			if (pos != Integer.MAX_VALUE) {

				// atom symbol
				final PatternString p = new PatternString();
				p.setPattern(temp.substring(0, pos));

				// bond symbol
				final PatternString pbond = new PatternString();
				pbond.setPattern(temp.substring(pos, pos + 1));

				temp = temp.substring(pos + 1, temp.length());

				c.addSimplePattern(p);
				c.addSimplePattern(pbond);

			} else {
				finished = true;

				final PatternString p = new PatternString();
				p.setPattern(temp);
				c.addSimplePattern(p);
			}
		}

		return c;
	}

	/**
	 * creates a simple pattern by decomposing a string to its characters, this
	 * function weights the position in the string
	 * 
	 * @param pattern
	 * @return
	 */
	public static PatternContainer getPatternsBondSeparatedWeighted(String pattern) {

		final PatternContainer c = new PatternContainer();

		boolean finished = false;
		String temp = new String(pattern);
		Float depth = 1.0f;

		while (!finished) {
			final int pos = getNextBondSymbol(temp);
			if (pos != Integer.MAX_VALUE) {

				// atom symbol
				final PatternString p = new PatternString();
				p.setPattern(temp.substring(0, pos));
				p.setWeight(depth);

				// bond symbol
				final PatternString pbond = new PatternString();
				pbond.setPattern(temp.substring(pos, pos + 1));
				pbond.setWeight(depth);

				temp = temp.substring(pos + 1, temp.length());

				c.addSimplePattern(p);
				c.addSimplePattern(pbond);

				depth = depth * 0.85f;

			} else {
				finished = true;

				final PatternString p = new PatternString();
				p.setPattern(temp);
				p.setWeight(depth);
				c.addSimplePattern(p);
			}
		}

		return c;
	}

	/**
	 * creates a simple pattern by decomposing a string to its characters, this
	 * function weights the symbols by their probability
	 * 
	 * @param pattern
	 * @return
	 */
	public static PatternContainer getPatternsBondSeparatedWeightedByProbability(String pattern) {

		final PatternContainer c = new PatternContainer();

		boolean finished = false;
		String temp = new String(pattern);
		temp = temp.replace("-", ")-(");
		temp = temp.replace(":", "):(");
		temp = temp.replace("=", ")=(");
		temp = temp.replace("#", ")#(");
		temp = "(" + temp;
		temp = temp + ")";
		// float probability = 0.0f;

		while (!finished) {
			final int pos = getNextBondSymbol(temp);
			if (pos < Integer.MAX_VALUE) {

				// atom symbol
				final PatternString p = new PatternString();
				final int pos1 = temp.indexOf("(");
				final int pos2 = temp.indexOf(")");
				String esymbol = temp.substring(pos1 + 1, pos2);
				esymbol = esymbol.replace("$", ")");
				// final float weight = (float)
				// sdistribution.getLog10Distribution4Symbol(esymbol);
				p.setPattern(esymbol);
				// probability = probability + weight;
				// p.setWeight(probability);

				// bond symbol
				final PatternString pbond = new PatternString();
				pbond.setPattern(temp.substring(pos, pos + 1));
				// pbond.setWeight(probability);

				// shorten the pattern
				temp = temp.substring(pos + 1, temp.length());

				c.addSimplePattern(p);
				c.addSimplePattern(pbond);

			} else {
				// append the last symbol
				// with the final probability
				finished = true;
				temp = temp.replace("(", "");
				temp = temp.replace(")", "");

				final PatternString p = new PatternString();
				// final float weight = (float)
				// sdistribution.getLog10Distribution4Symbol(temp);
				// probability = probability + weight;
				p.setPattern(temp);
				// p.setWeight(probability);
				c.addSimplePattern(p);
			}
		}

		return c;
	}

	/**
	 * creates a simple pattern by decomposing a string to its characters, this
	 * function weights the position in the string
	 * 
	 * @param pattern
	 * @return
	 */
	public static PatternContainer getWeightedPattern4String(String pattern, int blocksize) {

		final PatternContainer c = new PatternContainer();

		for (int i = 0; i < pattern.length(); i = i + blocksize) {
			final PatternString p = new PatternString();

			String t = "";
			for (int b = 0; b < blocksize; b++) {
				t = t + pattern.charAt(i + b);
			}
			p.setWeight(((float) pattern.length() - (float) i) / (pattern.length()));
			p.setPattern(t);
			c.addSimplePattern(p);
		}
		return c;
	}
}
