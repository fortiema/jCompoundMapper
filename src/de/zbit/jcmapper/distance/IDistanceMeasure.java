package de.zbit.jcmapper.distance;

import de.zbit.jcmapper.fingerprinters.features.FeatureMap;

public interface IDistanceMeasure {
	public String getIdentifier();

	public double getSimilarity(FeatureMap a, FeatureMap b);
}
