package de.zbit.jcmapper.fingerprinters.features;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FeatureMap {
	private final HashMap<IFeature, Double> featureMap = new HashMap<IFeature, Double>();
	private String label = "?";

	public FeatureMap(List<IFeature> features) {
		for (final IFeature feature : features) {
			if (!this.featureMap.containsKey(feature)) {
				this.featureMap.put(feature, feature.getValue());
			} else {
				double value = this.featureMap.get(feature);
				value = value + feature.getValue();
				this.featureMap.put(feature, value);
			}
		}
	}

	private int computeIntersection(FeatureMap other) {
		int commonFeatures = 0;

		// compare the smaller feature set against the larger one
		if (other.getSize() < this.getSize()) {
			final Set<IFeature> keys = other.featureMap.keySet();
			for (final IFeature key : keys) {
				if (this.featureMap.containsKey(key)) {
					commonFeatures++;
				}
			}
		} else {
			final Set<IFeature> keys = this.featureMap.keySet();
			for (final IFeature key : keys) {
				if (other.featureMap.containsKey(key)) {
					commonFeatures++;
				}
			}
		}

		return commonFeatures;
	}

	public double computeMinMaxDistance(FeatureMap other) {
		double sumMin = 0;
		double sumMax = 0;

		final double sizeThis = this.getSize();
		final double sizeOther = other.getSize();
		// both compounds are empty
		if (sizeThis == 0 && sizeOther == 0)
			return 1.0;

		HashSet<IFeature> keysCommon = new HashSet<IFeature>();
		keysCommon.addAll(this.getKeySet());
		Set<IFeature> keysOther = other.getKeySet();

		// get union set of the features
		for (IFeature keyOther : keysOther) {
			if (!keysCommon.contains(keyOther))
				keysCommon.add(keyOther);
		}

		for (final IFeature key : keysCommon) {
			double myValue = 0;
			if (this.featureMap.containsKey(key)) {
				myValue = this.featureMap.get(key);
			}
			double otherValue = 0;
			if (other.featureMap.containsKey(key)) {
				otherValue = other.featureMap.get(key);
			}

			sumMin = sumMin + Math.min(myValue, otherValue);
			sumMax = sumMax + Math.max(myValue, otherValue);
		}

		if (sumMin == 0 || sumMax == 0)
			return 0;

		return (sumMin / sumMax);
	}

	public double computeTanimotoCoefficient(FeatureMap other) {
		final double sizeThis = this.getSize();
		final double sizeOther = other.getSize();

		// both compounds are empty
		if (sizeThis == 0 && sizeOther == 0)
			return 1.0;

		final double intersection = this.computeIntersection(other);
		final double union = sizeOther + sizeThis - intersection;

		// else
		return intersection / union;
	}

	public Set<IFeature> getKeySet() {
		return this.featureMap.keySet();
	}

	public String getLabel() {
		return this.label;
	}

	public int getSize() {
		return this.featureMap.size();
	}

	public double getValue(IFeature key) {
		return this.featureMap.get(key);
	}

	public void print(boolean useAromaticFlag) {
		DecimalFormat df = new DecimalFormat();
		System.out.print("label=" + this.label + "\t");
		final Set<IFeature> features = this.featureMap.keySet();
		ArrayList<IFeature> list = new ArrayList<IFeature>(features);
		Collections.sort(list);
		for (final IFeature feature : list) {
			System.out.print("\t" + feature.featureToString(useAromaticFlag) + " " + df.format(this.featureMap.get(feature)));
		}
		System.out.println("");
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * returns a hashed binary fingerprint of size hashSpace
	 * 
	 * @param hashSpace
	 * @return
	 */
	public BitSet getHashedFingerPrint(int hashSpace) {
		BitSet hashedFingerPrint = new BitSet(hashSpace);
		Set<IFeature> features = this.getKeySet();

		for (IFeature feature : features) {
			Random random = new Random(feature.hashCode());
			int randomNumber = random.nextInt(hashSpace);
			hashedFingerPrint.set(randomNumber);
		}
		return hashedFingerPrint;
	}

}
