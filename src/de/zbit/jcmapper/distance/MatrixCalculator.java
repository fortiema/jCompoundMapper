package de.zbit.jcmapper.distance;

import java.util.List;

import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.tools.progressbar.ProgressBar;



public class MatrixCalculator {
	private IDistanceMeasure similaritymeasure = new DistanceTanimoto();

	public double[][] computeMatrix(List<FeatureMap> samples) {

		final int n = samples.size();
		ProgressBar progressBar = new ProgressBar(n);

		final double[][] matrix = new double[n][n];
		for (int i = 0; i < n; i++) {
			final FeatureMap featureMapI = samples.get(i);
			for (int j = i; j < n; j++) {
				final FeatureMap featureMapJ = samples.get(j);
				final double sim = this.similaritymeasure.getSimilarity(featureMapI, featureMapJ);
				matrix[i][j] = sim;
				matrix[j][i] = sim;
			}
			progressBar.DisplayBar();
		}
		return matrix;
	}

	public void setSimilaritymeasure(IDistanceMeasure similaritymeasure) {
		this.similaritymeasure = similaritymeasure;
	}

}
