package de.zbit.jcmapper.io.reader;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;

public class RandomAccessMDLReader implements Closeable{

	private ArrayList<Long> molindex;
	private BufferedRandomAccessFile raf = null;
	private int size = 0;
	private AtomContainer threadmol = null;
	private boolean removeHydrogens = true;

	/**
	 * this thread reads a molecule
	 */
	public class ParseThread extends Thread {

		private final Long index;

		public ParseThread(Long index) {
			this.index = index;
		}

		@Override
		public void run() {
			try {
				RandomAccessMDLReader.this.threadmol = RandomAccessMDLReader.this.getMolRaf(this.index);
			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final CDKException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * opens a buffered reader on mdl sd file
	 * 
	 * @param sdf
	 * @throws IOException
	 */
	public RandomAccessMDLReader(File sdf) throws IOException {

		try {
			this.raf = new BufferedRandomAccessFile(sdf, "r");
		} catch (final FileNotFoundException e1) {
			System.out.println("Could not find file: " + sdf.getCanonicalPath());
		}
		try {
			this.setRanges();
		} catch (final IOException e) {
			System.out.println("[RandomAccessMDLReader] Could not parse MDL SD file " + sdf);
			System.exit(1);
		}
	}

	/**
	 * returns the ith molecule in the random access file as plain mol file
	 * (e.g. to display it with Marvin)
	 * 
	 * @param index
	 * @return
	 */
	public String getMDLMolString(int index) {
		try {
			return this.getPlainMol(this.molindex.get(index));
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final CDKException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * returns the mdl sd tag for the ith molecule
	 * 
	 * @param index
	 * @return
	 */
	public String getLabel(int index, String label) {
		try {
			String molString = this.getPlainMol(this.molindex.get(index));
			BufferedReader reader = new BufferedReader(new StringReader(molString));
			String line;
			while ((line = reader.readLine()) != null) {
				// tag was found, proceed to next line
				if (line.contains(">") && line.contains("<" + label + ">"))
					return reader.readLine().trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * returns all possible labels for this data set
	 * 
	 * @param label
	 * @return
	 */
	public Set<String> getAllLabelClasses(String label) {
		HashSet<String> tags = new HashSet<String>();
		for (int i = 0; i < this.size; i++) {
			String currentLabel = getLabel(i, label);
			if (!tags.contains(currentLabel)) {
				tags.add(currentLabel);
			}
		}
		return tags;
	}

	/**
	 * returns the ith molecule in the random access file
	 * 
	 * @param index
	 * @returns null if the molecule could not be parsed
	 */
	public AtomContainer getMol(int index) {

		this.threadmol = null;
		ParseThread readthread = new ParseThread(this.molindex.get(index));
		readthread.start();
		try {
			readthread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

        AtomContainer mol = new AtomContainer();
		if (this.threadmol == null) {
			readthread = null;
			return mol;
		} else {
			try {
				mol = (AtomContainer) this.threadmol.clone();
			} catch (final CloneNotSupportedException e) {
				e.printStackTrace();
			}
			readthread = null;
			mol.setProperty("ID", (int) System.currentTimeMillis());

			if (this.removeHydrogens) {
				mol = (AtomContainer)MoleculePreprocessor.prepareMoleculeRemoveHydrogens(mol);
			} else {
				mol = (AtomContainer)MoleculePreprocessor.prepareMoleculeConserveHydrogens(mol);
			}
			return mol;
		}
	}

	/**
	 * returns the ith Molecule (index of first structure is 0!)
	 * 
	 * @param mr
	 * @return
	 * @throws IOException
	 * @throws CDKException
	 */
	private AtomContainer getMolRaf(Long mr) throws IOException, CDKException {
		this.raf.seek(mr.longValue());
		final StringBuffer mdl = new StringBuffer();

		String line = null;
		while ((line = this.raf.readLineBuffered()) != null) {
			mdl.append(line + "\n");
			if (line.startsWith("$$$$")) {
				break;
			}
		}

        AtomContainer molecule = null;
		String mdlString = mdl.toString();
		molecule = this.getRawMolecule(mdlString);

		return molecule;
	}

	/**
	 * returns the ith Molecule (index of first structure is 0!)
	 * 
	 * @param mr
	 * @return
	 * @throws IOException
	 * @throws CDKException
	 */
	private String getPlainMol(Long mr) throws IOException, CDKException {
		this.raf.seek(mr.longValue());
		final StringBuffer mdl = new StringBuffer();

		String line = null;
		while ((line = this.raf.readLineBuffered()) != null) {
			mdl.append(line + "\n");
			if (line.startsWith("$$$$")) {
				break;
			}
		}

		return mdl.toString();
	}

	/**
	 * get a type molecule with stripped hydrogens
	 *
	 * @param mdlString
	 * @return
	 * @throws CDKException
	 */
	private AtomContainer getRawMolecule(String mdlString) throws CDKException {

        AtomContainer mol = null;

		try {
			final MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdlString));
			final ChemFile fileContents = reader.read(new ChemFile());
			final org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
			final org.openscience.cdk.interfaces.IChemModel model = sequence.getChemModel(0);
			final org.openscience.cdk.interfaces.IAtomContainerSet som = model.getMoleculeSet();
			final org.openscience.cdk.interfaces.IAtomContainer imol = som.getAtomContainer(0);
			mol = (AtomContainer) imol;

		} catch (final NullPointerException e) {
			/*
			 * Fix for NullPointer due to occurence of D or T (Deuterium or
			 * Tritium) with massNumber null as happens now and then in Starlite
			 * (Chembl) Regex fix to replace D or T with a H (Hydrogen) symbol
			 * as replacement.
			 */

			final Pattern p = Pattern.compile("[D|T](\\s+\\d){6}");
			final Matcher matcher = p.matcher(mdlString);
			final StringBuilder sb = new StringBuilder(mdlString);
			while (matcher.find()) {
				sb.replace(matcher.start(), matcher.start() + 1, "H");
			}
			mdlString = sb.toString();
			final MDLV2000Reader reader = new MDLV2000Reader(new StringReader(mdlString));
			final ChemFile fileContents = reader.read(new ChemFile());
			final org.openscience.cdk.interfaces.IChemSequence sequence = fileContents.getChemSequence(0);
			final org.openscience.cdk.interfaces.IChemModel model = sequence.getChemModel(0);
			final org.openscience.cdk.interfaces.IAtomContainerSet som = model.getMoleculeSet();
			final org.openscience.cdk.interfaces.IAtomContainer imol = som.getAtomContainer(0);
			mol = (AtomContainer) imol;
		}

		return mol;
	}

	/**
	 * returns the number of structures contained in the MDL SD file
	 * 
	 * @return
	 */
	public int getSize() {
		return this.size;
	}

	public boolean isRemoveHydrogens() {
		return removeHydrogens;
	}

	public void setRemoveHydrogens(boolean removeHydrogens) {
		this.removeHydrogens = removeHydrogens;
	}

	/**
	 * 
	 * stores the offset for the molecules
	 * 
	 * @return
	 * @throws IOException
	 */
	private void setRanges() throws IOException {
		String line;
		Long mr;

		this.molindex = new ArrayList<Long>();
		new StringBuffer();

		int c = 0;
		// add the first entry
		this.molindex.add(new Long(0));
		while ((line = this.raf.readLineBuffered()) != null) {
			if (line.startsWith("$$$$")) {
				mr = this.raf.getFilePointer();
				this.molindex.add(mr);
				mr = new Long(0);
				c++;
				if (c % 2000 == 0) {
					System.out.print(".");
				}
			}
		}
		this.size = c;
		System.out.print("\n");
	}

	@Override
	public void close() throws IOException {
		this.raf.close();
	}
}
