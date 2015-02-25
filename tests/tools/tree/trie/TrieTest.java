package tools.tree.trie;

import org.junit.Test;

import de.zbit.jcmapper.tools.tree.trie.Trie;
import de.zbit.jcmapper.tools.tree.trie.pattern.HashedPattern;
import de.zbit.jcmapper.tools.tree.trie.pattern.Pattern;
import de.zbit.jcmapper.tools.tree.trie.pattern.PatternContainer;


public class TrieTest {

	@Test
	public void runTest() {
		final TrieTest t = new TrieTest();
		t.testLossLessTrie();
		t.testHashedTrie();
	}

	private static void printTrieStats(Trie t) {
		System.out.println("Unique feature count = " + t.getNumberOfFeatures());
		System.out.println("Total feature count = " + t.getTotalFeatureCount());
		System.out.println("Total number of nodes = " + t.getTotalNodeCount());
	}

	private void testHashedTrie() {
		// create a demo pattern
		final HashedPattern p1 = new HashedPattern();
		p1.setPattern("N2-34");
		final HashedPattern p2 = new HashedPattern();
		p2.setPattern("O=-7");
		final HashedPattern p3 = new HashedPattern();
		p3.setPattern("NX-9");

		// create a container for the patterns
		final PatternContainer c = new PatternContainer();
		c.addSimplePattern(p1);
		c.addSimplePattern(p2);
		c.addSimplePattern(p3);
		c.setCount(2);

		final PatternContainer c2 = new PatternContainer();
		c2.addSimplePattern(p1);
		c2.addSimplePattern(p3);
		c2.addSimplePattern(p2);
		c2.setCount(1);

		// create trie A
		final Trie A = new Trie();
		A.insertPattern(c);
		A.insertPattern(c2);
		A.init();
		printTrieStats(A);

		// create trie B
		final Trie B = new Trie();
		B.insertPattern(c);
		B.insertPattern(c);
		B.init();
		printTrieStats(B);

		System.out.println(A.getGMLString());
		System.out.println(B.getGMLString());

		System.out.println("\nMin(A,B) = " + A.computeSimilarityMin(B));
		System.out.println("Spectrum(A,B) = " + A.computeSimilaritySpectrum(B));
		System.out.println("Tanimoto(A,B) = " + A.computeSimilarityTanimoto(B));
	}

	private void testLossLessTrie() {
		// create a demo pattern
		final Pattern p1 = new Pattern();
		p1.setPattern("N2-34");
		final Pattern p2 = new Pattern();
		p2.setPattern("O=-7");
		final Pattern p3 = new Pattern();
		p3.setPattern("NX-9");

		// create a container for the patterns
		final PatternContainer c = new PatternContainer();
		c.addSimplePattern(p1);
		c.addSimplePattern(p2);
		c.addSimplePattern(p3);
		c.setCount(2);

		// create trie A
		final Trie A = new Trie();
		A.insertPattern(c);
		A.init();
		printTrieStats(A);

		// create trie B
		final Trie B = new Trie();
		B.insertPattern(c);
		B.insertPattern(c);
		B.init();
		printTrieStats(B);

		System.out.println(A.getGMLString());
		System.out.println(B.getGMLString());

		System.out.println("\nMin(A,B) = " + A.computeSimilarityMin(B));
		System.out.println("Spectrum(A,B) = " + A.computeSimilaritySpectrum(B));
		System.out.println("Tanimoto(A,B) = " + A.computeSimilarityTanimoto(B));
	}

}
