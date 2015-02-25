package de.zbit.jcmapper.distance;

import de.zbit.jcmapper.fingerprinters.features.FeatureMap;

public class DistanceTanimoto implements IDistanceMeasure {

	@Override
	public String getIdentifier() {
		return "Tanimoto Coefficient";
	}

	@Override
	public double getSimilarity(FeatureMap a, FeatureMap b) {
		return a.computeTanimotoCoefficient(b);
	}

}
