package de.zbit.jcmapper.fingerprinters;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.tools.moltyping.ExtendedAtomAndBondTyper;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public abstract class EncodingFingerprint {

	private final ExtendedAtomAndBondTyper atomTyper = new ExtendedAtomAndBondTyper(AtomLabelType.ELEMENT_NEIGHBOR);

	public String getAtomLabel(IAtom atom) throws MoltyperException {
		return this.atomTyper.getAtomLabel(atom);
	}

	public AtomLabelType getAtomLabelType() {
		return this.atomTyper.getAtomLabelType();
	}

	public String getBondLabel(IBond bond) throws MoltyperException {
		return ExtendedAtomAndBondTyper.getBondSymbol(bond);
	}

	public abstract List<IFeature> getFingerprint(IAtomContainer ac);

	public abstract String getNameOfFingerPrinter();

	public boolean isHashable() {
		return false;
	}

	public void setAtomLabelType(AtomLabelType atomLabelType) {
		this.atomTyper.setAtomLabelType(atomLabelType);
	}
	
	public ExtendedAtomAndBondTyper getTyper(){
		return atomTyper;	
	}
}
