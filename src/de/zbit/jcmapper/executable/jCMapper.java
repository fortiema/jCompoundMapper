package de.zbit.jcmapper.executable;


import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.zbit.jcmapper.distance.DistanceFactory;
import de.zbit.jcmapper.distance.DistanceFactory.DistanceType;
import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.FingerPrinterFactory;
import de.zbit.jcmapper.fingerprinters.FingerPrinterFactory.FingerprintType;
import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3D;
import de.zbit.jcmapper.fingerprinters.topological.DepthFirstSearch;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2D;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.io.writer.ExporterFactory;
import de.zbit.jcmapper.io.writer.ExporterHashLinear;
import de.zbit.jcmapper.io.writer.ExporterHashWeka;
import de.zbit.jcmapper.io.writer.ExporterLIBSVMMatrix;
import de.zbit.jcmapper.io.writer.IExporter;
import de.zbit.jcmapper.io.writer.ExporterFactory.ExporterType;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;

public class jCMapper {

	private String sdFileInputData = "";
	private String sdTagForLabel = "?";
	private EncodingFingerprint fingerprintEncoding = new DepthFirstSearch();
	private FingerprintType fingerprintType = FingerprintType.DFS;
	private String outFile;
	private Integer labelThreshold=5; 
	private ExporterType exporterType = ExporterType.LIBSVM_SPARSE;
	private DistanceType distanceType = DistanceType.TANIMOTO;
	private int hashSpaceSize = (int) Math.pow(2, 18);
	private boolean useAromaticityFlag=false;

	/**
	 * @param args
	 */
	public static void main(String[] args){
		java.util.Locale.setDefault(java.util.Locale.ENGLISH);
		final jCMapper molprinter = new jCMapper();
		Options options = molprinter.buildCommandLine();
		
		try{
			molprinter.parseCommandLine(args, options);
		}catch(ParseException pe){
			if(pe.getMessage().substring(0,24).equals("Please check your input:"))
				pe.printStackTrace();
			else
				System.out.println(pe.getMessage());
			System.exit(1);
		}
		
		molprinter.printInfos();
		
		try{
			molprinter.exportFingerprintFile();
		}catch(IOException ioe){
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * read compounds and export
	 */
	private void exportFingerprintFile() throws IOException {
		RandomAccessMDLReader reader = null;
		try {
			reader = new RandomAccessMDLReader(new File(sdFileInputData));
		} catch (Exception e) {
			System.out.println("SD file " + sdFileInputData + " not found!");
			System.exit(1);
		}

		IExporter exporter = ExporterFactory.getExporter(exporterType);

		if (exporter instanceof ExporterLIBSVMMatrix) {
			((ExporterLIBSVMMatrix) exporter).setDistanceMeasure(DistanceFactory.getDistance(distanceType));
		}
		if (exporter instanceof ExporterHashLinear) {
			((ExporterHashLinear) exporter).setHashSpace(this.hashSpaceSize);
		}
		if (exporter instanceof ExporterHashWeka) {
			((ExporterHashWeka) exporter).setLabelThreshold(this.labelThreshold);
		}
		
		 

		if (outFile == null) {
			String outFileName = (new File(sdFileInputData)).getName().replaceAll(".sdf", "") + "." + fingerprintType
					+ "." + exporterType;
			File fOut = new File(outFileName);
			if (!(ExporterType.BENCHMARKS == exporterType))
				System.out.println("Output file = " + fOut.getAbsolutePath());

			exporter.export(reader, fingerprintEncoding, sdTagForLabel, fOut,useAromaticityFlag);
		} else {
			try {
				File fOut = new File(outFile);
				if (!(ExporterType.BENCHMARKS == exporterType))
					System.out.println("Output file = " + fOut.getAbsolutePath());

				exporter.export(reader, fingerprintEncoding, sdTagForLabel, fOut,useAromaticityFlag);
			} catch (Exception e) {
				System.out.println("File " + outFile + " could not be created.");
				System.exit(1);
			}
		}
	}

	/**
	 * print setup
	 */
	private void printInfos() {
		System.out.println("Selected label: " + sdTagForLabel);
		System.out.println("Output format: " + exporterType);
		System.out.println("Fingerprinting algorithm: " + fingerprintEncoding.getNameOfFingerPrinter());
		if (fingerprintEncoding instanceof Encoding2D) {
			System.out.println("Search depth: " + ((Encoding2D) fingerprintEncoding).getSearchDepth());
		}
		if (fingerprintEncoding instanceof Encoding3D) {
			System.out.println("Distance cutoff: " + ((Encoding3D) fingerprintEncoding).getDistanceCutoff());
			System.out.println("Stretching factor: " + ((Encoding3D) fingerprintEncoding).getStretchingFactor());
		}
		System.out.println("Labeling Algorithm: " + fingerprintEncoding.getAtomLabelType());
		System.out.println("Export option: " + exporterType);
		System.out.println("Use aromaticity flag: " + useAromaticityFlag);
		if (exporterType == ExporterType.LIBSVM_MATRIX)
			System.out.println("Similarity measure: " + distanceType);
		
		System.out.println("Hash space size = " + this.hashSpaceSize);
	}

	@SuppressWarnings("static-access")
	private Options buildCommandLine() {
		final Options options = new Options();
		final Option optSDF = (OptionBuilder.isRequired(true).withDescription("MDL SD file").hasArg(true).create("f"));
		final Option optLabel = (OptionBuilder.isRequired(false).withDescription("Label (MDL SD Property)")
				.hasArg(true).create("l"));
		final Option optType = (OptionBuilder
				.isRequired(false)
				.withDescription(
						"Atom Type: " + listEnumerationOption(AtomLabelType.values())).hasArg(true).create("a"));
		final Option optDistanceMeasure = (OptionBuilder
				.isRequired(false)
				.withDescription(
						"Distance measure (matrix format): " + listEnumerationOption(DistanceType.values())).hasArg(true).create("m"));
		final Option optFingerprintAlgorithm = (OptionBuilder
				.isRequired(false)
				.withDescription(
						"Fingerprinting algorithm: " + listEnumerationOption(FingerprintType.values())).hasArg(true)
				.create("c"));
		final Option optDistanceCutOff = (OptionBuilder.isRequired(false)
				.withDescription("Distance cutoff / search depth").hasArg(true).create("d"));
		final Option optStretchingFactor = (OptionBuilder.isRequired(false)
				.withDescription("Stretching factor (3D fingerprints)").hasArg(true).create("s"));
		final Option optHelprinter = (OptionBuilder.isRequired(false).withDescription("Print help").hasArg(false)
				.create("h"));
		final Option optOutFile = (OptionBuilder.isRequired(false).hasArg(true).withDescription("Output file")
				.create("o"));

		final Option optLabelThreshold = (OptionBuilder.isRequired(false).hasArg(true).withDescription("Label threshold")
				.create("lt"));
		final Option optHashSpace = (OptionBuilder.isRequired(false).hasArg(true).withDescription(
				"Hash space size (default=2^18)").create("hs"));
		final Option optFormat = (OptionBuilder.isRequired(false).hasArg(true).withDescription(
				"Output format: " + listEnumerationOption(ExporterType.values())).create("ff"));
		final Option optAromFlag = (OptionBuilder.isRequired(false).withDescription("Use aromaticity flag (only relevant for STRING_PATTERNS,SQLITE,WEKA_NOMINAL)").hasArg(false)
				.create("k"));

		options.addOption(optSDF);
		options.addOption(optLabel);
		options.addOption(optType);
		options.addOption(optFingerprintAlgorithm);
		options.addOption(optDistanceCutOff);
		options.addOption(optOutFile);
		options.addOption(optLabelThreshold);
		options.addOption(optFormat);
		options.addOption(optDistanceMeasure);
		options.addOption(optStretchingFactor);
		options.addOption(optHashSpace);
		options.addOption(optAromFlag);
		return options.addOption(optHelprinter);
	}
	
	private String listEnumerationOption(Enum[] options){
		StringBuffer optionString = new StringBuffer();
		for(int i=0; i<options.length; i++){
			optionString.append(options[i]);
			if(i<options.length-1)
				optionString.append(", ");
		}
		return optionString.toString();
	}

	private void parseCommandLine(String[] args, Options options) throws ParseException {
		CommandLine lvCmd = null;
		final HelpFormatter lvFormater = new HelpFormatter();
		final CommandLineParser lvParser = new BasicParser();
		/**
		 * parse command line
		 */
		try {
			lvCmd = lvParser.parse(options, args);
			if (lvCmd.hasOption('h')) {
				lvFormater.printHelp("java -jar jCMapper.jar", options);				
				System.exit(1);
			}
		} catch (final ParseException pvException) {
			lvFormater.printHelp("jCMapper", options);
			System.out.println("Parse error: " + pvException.getMessage());
			System.exit(1);
		}

		try {
			if (lvCmd.hasOption("c")) {
				try {
					String fingerprintName = lvCmd.getOptionValue("c");
					fingerprintType = FingerPrinterFactory.FingerprintType.valueOf(fingerprintName);
					fingerprintEncoding = FingerPrinterFactory.getFingerprinter(fingerprintType);
				} catch (Exception e) {
					throw new ParseException("Error parsing name of encoding type. Please check your input.");
				}
			}
			if (lvCmd.hasOption("k")) {
				useAromaticityFlag=true;
			}
			if (lvCmd.hasOption("f")) {
				sdFileInputData = new String(lvCmd.getOptionValue("f"));
				sdFileInputData = (new File(sdFileInputData)).getAbsolutePath();
				System.out.println("Processing MDL SD file: " + sdFileInputData);
			}
			if (lvCmd.hasOption("o")) {
				outFile = new String(lvCmd.getOptionValue("o"));
				outFile = new File(outFile).getCanonicalPath();
				System.out.println("Output filename: " + outFile);
			}
			if (lvCmd.hasOption("m")) {
				String distance;
				try {
					distance = lvCmd.getOptionValue("m");
					distanceType = DistanceFactory.DistanceType.valueOf(distance);
				} catch (Exception e) {
					throw new ParseException("Error parsing name of similarity measure. Please check your input.");
				}
			}
			if (lvCmd.hasOption("ff")) {
				try {
					String formatOutFile = lvCmd.getOptionValue("ff");
					exporterType = ExporterType.valueOf(formatOutFile);
				} catch (Exception e) {
					throw new ParseException("Error parsing name of format type. Please check your input.");
				}
			}
			if (lvCmd.hasOption("lt")) {
				try {					
					labelThreshold = new Integer(lvCmd.getOptionValue("lt"));					
				} catch (Exception e) {
					throw new ParseException("Error parsing integer value for format type. Please check your input.");
				}
			}
			if (lvCmd.hasOption("l")) {
				sdTagForLabel = new String(lvCmd.getOptionValue("l"));
			}
			if (lvCmd.hasOption("a")) {
				try {
					final String atomType = lvCmd.getOptionValue("a");
					final AtomLabelType atomLabelType = AtomLabelType.valueOf(atomType);
					fingerprintEncoding.setAtomLabelType(atomLabelType);
				} catch (Exception e) {
					throw new ParseException("Error parsing name of atom type. Please check your input.");
				}
			}
			if (lvCmd.hasOption("hs")) {
				try {
					this.hashSpaceSize = new Integer(lvCmd.getOptionValue("hs"));
				} catch (Exception e) {
					throw new ParseException("Error parsing integer value for size of the hash space. Please check your input.");
				}
			}
			if (lvCmd.hasOption("d")) {
				try {
					final double distanceCutOff = new Double(lvCmd.getOptionValue("d"));
					if (fingerprintEncoding instanceof Encoding2D) {
						((Encoding2D) fingerprintEncoding).setSearchDepth((int) distanceCutOff);
					}
					if (fingerprintEncoding instanceof Encoding3D) {
						((Encoding3D) fingerprintEncoding).setDistanceCutoff((int) distanceCutOff);
					}
				} catch (Exception e) {
					throw new ParseException("Error parsing double value for distance cutoff. Please check your input.");
				}
			}
			if (lvCmd.hasOption("s")) {
				try {
					double stretchingFactor = new Double(lvCmd.getOptionValue("s"));
					if (fingerprintEncoding instanceof Encoding3D) {
						((Encoding3D) fingerprintEncoding).setStretchingFactor(stretchingFactor);
					}
				} catch (Exception e) {
					System.out.println("Error parsing double value for stretching factor. Please check your input.");
					System.exit(1);
				}
			}
		} catch (final Exception e) {
			ParseException pe = new ParseException("Please check your input: "+e.getMessage());
			pe.setStackTrace(e.getStackTrace());
			throw pe;
		}
	}
}
