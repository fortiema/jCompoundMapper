package de.zbit.jcmapper.tools.moltyping;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public class ExtendedAtomAndBondTyper {
	/**
	 * returns the CDK atom type
	 * 
	 * @param atom
	 * @return
	 */
	public static String getAtomType(IAtom atom) {
		String type = atom.getAtomTypeName();
		if (type == null) {
			type = "*";
		}
		return type;
	}

	/**
	 * returns the bond symbol for a given bond
	 * 
	 * @param bond
	 * @return
	 */
	public static String getBondSymbol(org.openscience.cdk.interfaces.IBond bond) {
		String bondSymbol = "";
		if (bond.getFlag(CDKConstants.ISAROMATIC)) {
			bondSymbol = ":";
		} else if (bond.getOrder() == IBond.Order.SINGLE) {
			bondSymbol = "-";
		} else if (bond.getOrder() == IBond.Order.DOUBLE) {
			bondSymbol = "=";
		} else if (bond.getOrder() == IBond.Order.TRIPLE) {
			bondSymbol = "#";
		}
		return bondSymbol;
	}

	/**
	 * returns the bond symbol for a given bond
	 * 
	 * @param bond
	 * @return
	 */
	public static String getBondSymbolNoAromatic(org.openscience.cdk.interfaces.IBond bond) {
		String bondSymbol = "";
		if (bond.getOrder() == IBond.Order.SINGLE) {
			bondSymbol = "-";
		} else if (bond.getOrder() == IBond.Order.DOUBLE) {
			bondSymbol = "=";
		} else if (bond.getOrder() == IBond.Order.TRIPLE) {
			bondSymbol = "#";
		}
		return bondSymbol;
	}

	public static String getDaylightInvariant(IAtom atom) {
		final int numberOfHydrogens = getHydrogenCount(atom);
		final int numberOfNeighbors = getFormalneighbourCount(atom);
		final int valency = getValency(atom);
		final StringBuffer label = new StringBuffer();

		label.append(atom.getAtomicNumber());
		label.append("." + (numberOfNeighbors - numberOfHydrogens));
		label.append("." + (valency - numberOfHydrogens));
		label.append("." + (atom.getMassNumber()));
		label.append("." + (atom.getFormalCharge()));
		label.append("." + (numberOfHydrogens));

		return label.toString();
	}

	/**
	 * Daylight Invariant + Ring as Atom Label:
	 * Symbol.#Heavyneighbours.valence-hydrogens.mass.charge.#hydrogens.InRing
	 * e.g. 12.2.3.mass.0.1.1
	 */
	public static String getDaylightInvariantRing(IAtom atom) {
		final int numberOfHydrogens = getHydrogenCount(atom);
		final int numberOfNeighbors = getFormalneighbourCount(atom);
		final int valency = getValency(atom);
		final StringBuffer label = new StringBuffer();

		label.append(atom.getAtomicNumber());
		label.append("." + (numberOfNeighbors - numberOfHydrogens));
		label.append("." + (valency - numberOfHydrogens));
		label.append("." + (atom.getMassNumber()));
		label.append("." + (atom.getFormalCharge()));
		label.append("." + (numberOfHydrogens));

		if (atom.getFlag(CDKConstants.ISINRING)) {
			label.append(".1");
		} else {
			label.append(".0");
		}

		return label.toString();
	}

	/**
	 * returns the element plus neighbor count type plus ring information
	 * 
	 * @param atom
	 * @return
	 */
	public static String getElementNeighborRingType(IAtom atom) {
		
		String type = getAtomSymbol(atom);
		Integer formalNeighbourCount = getFormalneighbourCount(atom);
		Integer hydrogenCount = getHydrogenCount(atom);

		try {
			if (atom.getFlag(CDKConstants.ISAROMATIC)) {
				type = type + ".a." + (formalNeighbourCount - hydrogenCount);
			} else if (atom.getFlag(CDKConstants.ISINRING)) {
				type = type + ".r." + (formalNeighbourCount - hydrogenCount);
			} else {
				type = type + "." + (formalNeighbourCount - hydrogenCount);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return type;
	}

	
	public static String getAtomTypeName(IAtom atom){
		String type = atom.getAtomTypeName();
		if (type == null) {
			type = "*";
		}
		return type;
	}
	
	public static String getAtomSymbol(IAtom atom){
		String type = atom.getSymbol();
		if (type == null) {
			type = "*";
		}
		return type;
	}
	
	public static Integer getFormalneighbourCount(IAtom atom){
		Integer formalNeighboutCount = atom.getFormalNeighbourCount();
		if (formalNeighboutCount == null)
			formalNeighboutCount = 0;
		return formalNeighboutCount;
	}
	
	public static Integer getValency(IAtom atom){
		Integer valency = atom.getValency();
		if (valency == null)
			valency = 0;
		return valency;
	}

	public static Integer getHydrogenCount(IAtom atom){
		Integer hydrogenCount = atom.getImplicitHydrogenCount();
		if (hydrogenCount == null)
			hydrogenCount = 0;
		return hydrogenCount;
	}
	
	
	/**
	 * returns the element plus neighbor count type
	 * 
	 * @param atom
	 * @return
	 */
	public static String getElementNeighborType(IAtom atom) {

		String type = getAtomSymbol(atom);
		Integer formalNeighbourCount = getFormalneighbourCount(atom);
		Integer hydrogenCount = getHydrogenCount(atom);

		type = type + "." + (formalNeighbourCount - hydrogenCount);
		return type;
	}

	/**
	 * returns a cdk atom type flagged with r and aromaticity information
	 * 
	 * @param atom
	 * @return
	 */
	public static String getRingAtomType(IAtom atom) {

		String type = getAtomTypeName(atom);

		if (atom.getFlag(CDKConstants.ISAROMATIC)) {
			return (type + ".a");
		}

		if (atom.getFlag(CDKConstants.ISINRING)) {
			return (type + ".r");
		}

		return type;
	}

	public static boolean needsAromaticityDetection(AtomLabelType atomLabelType) throws MoltyperException {
		if (atomLabelType == AtomLabelType.CDK_ATOM_TYPES) {
			return true;
		} else if (atomLabelType == AtomLabelType.ELEMENT_NEIGHBOR) {
			return false;
		} else if (atomLabelType == AtomLabelType.ELEMENT_NEIGHBOR_RING) {
			return true;
		} else if (atomLabelType == AtomLabelType.ELEMENT_SYMBOL) {
			return false;
		}else if (atomLabelType == AtomLabelType.DAYLIGHT_INVARIANT) {
			return false;
		}else if (atomLabelType == AtomLabelType.DAYLIGHT_INVARIANT_RING) {
		    return true;
		} else {
			throw new MoltyperException("Unrecognized AtomType:" + atomLabelType.name());
		}
	}

	private AtomLabelType atomLabelType;

	public ExtendedAtomAndBondTyper(AtomLabelType type) {
		this.atomLabelType = type;
	}

	/**
	 * Acessor function that returns atom type depending on AtomLabelType
	 * chosen. This function should be accessed by the fingerprinters.
	 * 
	 * @param atom
	 *            Atom to be Atomtyped
	 * @return
	 * @throws MoltyperException
	 */
	public String getAtomLabel(IAtom atom) throws MoltyperException {
		if (this.atomLabelType == AtomLabelType.CDK_ATOM_TYPES) {
			return getAtomType(atom);
		} else if (this.atomLabelType == AtomLabelType.ELEMENT_NEIGHBOR) {
			return getElementNeighborType(atom);
		} else if (this.atomLabelType == AtomLabelType.ELEMENT_NEIGHBOR_RING) {
			return getElementNeighborRingType(atom);
		} else if (this.atomLabelType == AtomLabelType.ELEMENT_SYMBOL) {
			return getAtomSymbol(atom);
		}else if (atomLabelType == AtomLabelType.DAYLIGHT_INVARIANT) {
			return getDaylightInvariant(atom);
		}else if (atomLabelType == AtomLabelType.DAYLIGHT_INVARIANT_RING) {
			return getDaylightInvariantRing(atom);
		}else {
			throw new MoltyperException("Unrecognized AtomType:" + this.atomLabelType.name());
		}
	}

	public AtomLabelType getAtomLabelType() {
		return this.atomLabelType;
	}

	public void setAtomLabelType(AtomLabelType type) {
		this.atomLabelType = type;
	}
}
