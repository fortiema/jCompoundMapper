package de.zbit.jcmapper.io.writer;

public class ExporterFactory {
	public static IExporter getExporter(ExporterType type) {
		if (type == ExporterType.LIBSVM_MATRIX) {
			return new ExporterLIBSVMMatrix();
		} else if (type == ExporterType.LIBSVM_SPARSE) {
			return new ExporterLIBSVMSparse();
		} else if (type == ExporterType.LIBSVM_SPARSE_FREQUENCY) {			
			return new ExporterLIBSVMSparseFrequency();
		} else if (type == ExporterType.FULL_CSV) {
			return new ExporterFullFingerprintCSV();
		} else if (type == ExporterType.FULL_TAB_UNFOLDED) {
			return new ExporterFullFingerprintTABUnfolded();
		} else if (type == ExporterType.WEKA_HASHED) {
			return new ExporterHashWeka();
		} else if (type == ExporterType.STRING_PATTERNS) {
			return new ExporterSparseString();
		} else if (type == ExporterType.BENCHMARKS) {
			return new ExporterBenchmark();
		} else if (type == ExporterType.WEKA_NOMINAL) {
			return new ExporterSparseNominalWeka();
		} else if (type == ExporterType.SQLITE) {
			return new ExporterSQLite();
		} else if (type == ExporterType.SDF_PROPERTY) {
			return new ExporterSDFProperty();
		} else if (type == ExporterType.NUMERIC_SQLITE) {
			return new ExporterNumericSQLite();
		}
		return new ExporterLIBSVMSparse();
	}

	public static enum ExporterType {
		LIBSVM_SPARSE, LIBSVM_SPARSE_FREQUENCY, LIBSVM_MATRIX, FULL_CSV, FULL_TAB_UNFOLDED, STRING_PATTERNS, WEKA_HASHED, WEKA_NOMINAL, BENCHMARKS, SQLITE, NUMERIC_SQLITE, SDF_PROPERTY;
	}

	public static ExporterType getExporterType(int index) {
		if (index == ExporterType.LIBSVM_MATRIX.ordinal()) {
			return ExporterType.LIBSVM_MATRIX;
		} else if (index == ExporterType.LIBSVM_SPARSE.ordinal()) {
			return ExporterType.LIBSVM_SPARSE;
		} else if (index == ExporterType.LIBSVM_SPARSE_FREQUENCY.ordinal()) {
			return ExporterType.LIBSVM_SPARSE_FREQUENCY;			
		} else if (index == ExporterType.BENCHMARKS.ordinal()) {
			return ExporterType.BENCHMARKS;
		} else if (index == ExporterType.FULL_CSV.ordinal()) {
			return ExporterType.FULL_CSV;
		} else if (index == ExporterType.FULL_TAB_UNFOLDED.ordinal()) {
			return ExporterType.FULL_TAB_UNFOLDED;
		} else if (index == ExporterType.STRING_PATTERNS.ordinal()) {
			return ExporterType.STRING_PATTERNS;
		}else if (index == ExporterType.WEKA_HASHED.ordinal()) {
			return ExporterType.WEKA_HASHED;
		}else if (index == ExporterType.WEKA_NOMINAL.ordinal()) {
			return ExporterType.WEKA_NOMINAL;
		}else if (index == ExporterType.SQLITE.ordinal()) {
			return ExporterType.SQLITE;
		}else if (index == ExporterType.NUMERIC_SQLITE.ordinal()) {
			return ExporterType.NUMERIC_SQLITE;
		}else if (index == ExporterType.SDF_PROPERTY.ordinal()) {
			return ExporterType.SDF_PROPERTY;
		}
		return ExporterType.LIBSVM_SPARSE;
	}
}
