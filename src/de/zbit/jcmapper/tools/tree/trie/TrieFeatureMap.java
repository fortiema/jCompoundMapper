package de.zbit.jcmapper.tools.tree.trie;

import java.util.Set;

import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.tools.tree.trie.pattern.PatternContainer;
import de.zbit.jcmapper.tools.tree.trie.pattern.PatternString;


public class TrieFeatureMap extends Trie {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3933760346780215217L;

	public TrieFeatureMap(FeatureMap fmap) {
		Set<IFeature> featuresKeys = fmap.getKeySet();
		
		 for(IFeature feature: featuresKeys){
			 double count = fmap.getValue(feature);
			 PatternContainer pc = getPattern4String(feature.featureToString(false));
			 pc.setCount((int) count);
			 pc.setNumericValue(count);
			 this.insertPattern(pc);
 		 }
	}

	private PatternContainer getPattern4String(String pattern) {

		final PatternContainer c = new PatternContainer();

		for (int i = 0; i < pattern.length(); i = i + 1) {
			final PatternString p = new PatternString();

			String t = "";
			t = t + pattern.charAt(i);

			p.setPattern(t);
			c.addSimplePattern(p);
		}
		return c;
	}
}
