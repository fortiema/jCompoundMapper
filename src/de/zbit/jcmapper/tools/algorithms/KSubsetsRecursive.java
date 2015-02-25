package de.zbit.jcmapper.tools.algorithms;

import java.util.ArrayList;

public class KSubsetsRecursive {

	private static int k;
	private static ArrayList<ArrayList<Integer>> ksubsets;
	private static int n;
	private static int[] set;

	@SuppressWarnings("unchecked")
	static void generateKSubset(int i, int j, ArrayList<Integer> list) {
		// subset filled with k elements, store this
		if (j == k) {
			ksubsets.add(list);
			// System.out.println(list.toString());
			return;
		}
		// no elements available, return
		if (i == n) {
			return;
		}

		list = (ArrayList<Integer>) list.clone();
		generateKSubset(i + 1, j, list);

		list = (ArrayList<Integer>) list.clone();
		// add element to k-subset
		list.add(set[i]);
		generateKSubset(i + 1, j + 1, list);
	}

	public static ArrayList<ArrayList<Integer>> getKSubsets(int nset, int kset) {
		set = new int[nset];
		ksubsets = new ArrayList<ArrayList<Integer>>();
		n = nset;
		k = kset;
		for (int i = 0; i < nset; i++) {
			set[i] = i;
		}
		runRecursion();
		return ksubsets;
	}

	private static void runRecursion() {
		generateKSubset(0, 0, new ArrayList<Integer>());
	}
}
