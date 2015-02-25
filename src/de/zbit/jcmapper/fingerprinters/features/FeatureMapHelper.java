package de.zbit.jcmapper.fingerprinters.features;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Median;

public class FeatureMapHelper {

	public static void printFeatureMapStatistics(List<FeatureMap> featureMaps) {
		int numberOfSamples = featureMaps.size();
		double[] featurePerSample = new double[numberOfSamples];

		HashMap<IFeature, Double> spectrumFeatures = new HashMap<IFeature, Double>();

		for (int i = 0; i < numberOfSamples; i++) {
			Set<IFeature> featureKeys = featureMaps.get(i).getKeySet();
			featurePerSample[i] = featureKeys.size();
			for (IFeature key : featureKeys) {
				if (spectrumFeatures.containsKey(key)) {
					final Double newValue = spectrumFeatures.get(key) + featureMaps.get(i).getValue(key);
					spectrumFeatures.put(key, newValue);
				} else {
					spectrumFeatures.put(key, featureMaps.get(i).getValue(key));
				}
			}
		}

		int dimSpectrum = spectrumFeatures.keySet().size();
		double[] counts = new double[dimSpectrum];

		Set<IFeature> keysSpectrum = spectrumFeatures.keySet();
		int i = 0;
		for (IFeature key : keysSpectrum) {
			counts[i] = spectrumFeatures.get(key);
			i++;
		}

		java.util.Locale.setDefault(java.util.Locale.ENGLISH);
		final DecimalFormat df = new DecimalFormat("###.####");
		Mean mean = new Mean();
		StandardDeviation stdv = new StandardDeviation();
		Max max = new Max();
		Median median = new Median();
		System.out.println("number of unique features = " + dimSpectrum);
		System.out.println("avg. features per sample = " + df.format(mean.evaluate(featurePerSample)) + " +/- " + df.format(stdv.evaluate(featurePerSample)));
		System.out.println("median number of features = " + median.evaluate(featurePerSample));
		System.out.println("max. number of features = " + df.format(max.evaluate(featurePerSample)));
	}
}
