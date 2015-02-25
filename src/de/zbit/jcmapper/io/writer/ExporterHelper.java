package de.zbit.jcmapper.io.writer;

import java.text.DecimalFormat;
import java.util.Random;

import org.openscience.cdk.interfaces.IAtomContainer;

public class ExporterHelper {
	/**
	 * returns the name of the chemical compound
	 */
	public static String getMolName(IAtomContainer ac) {
		String molName = (String) ac.getProperty("cdk:Title");
		if (molName == null) {
			molName = "MOL";
		}
		return molName;
	}

	public static void printInfo(double collisions, double featureCount, double dataSetSize) {
		DecimalFormat df = new DecimalFormat();
		double collisionRate = collisions / dataSetSize;
		double avgFeatureCount = featureCount / dataSetSize;
		System.out.println("Avg. features per mol = " + df.format(avgFeatureCount));
		System.out.println("Total collisions = " + df.format(collisions));
		System.out.println("Avg. collisions per mol = " + df.format(collisionRate));
		System.out.println("Avg. collision rate per mol = " + df.format(collisionRate / avgFeatureCount));
	}

	/**
	 * assign new hash
	 */
	public static int rehash(int seed, int hashSpace) {
		Random generator = new Random(seed);
		int newHash = generator.nextInt(hashSpace)+1;
		return newHash;
	}
}
