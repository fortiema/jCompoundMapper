package de.zbit.jcmapper.distance;


public class DistanceFactory {

	public static IDistanceMeasure getDistance(DistanceType type) {
		if (type == DistanceType.TANIMOTO) {
			return new DistanceTanimoto();
		} else if (type == DistanceType.MINMAX) {
			return new DistanceMinMax();
		}
		// default
		return new DistanceTanimoto();

	}

	public static enum DistanceType {
		TANIMOTO, MINMAX;
	}
}
