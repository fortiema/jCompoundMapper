package de.zbit.jcmapper.fingerprinters.topological.features;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.FingerPrinterException;


public class DanglingBond {
	private IBond bond;
	private int connectedAtomPosition;
	private IAtom connectedAtom;

	public DanglingBond(IBond bond, IAtom connectedAtom) throws FingerPrinterException {
		if (bond.contains(connectedAtom)) {
			this.connectedAtom = connectedAtom;
			this.bond = bond;
			this.connectedAtomPosition = this.getConnectedAtomPositionInBond(connectedAtom);
		} else {
			throw new FingerPrinterException("Atom not contained in Bond. Cannot instantiate "
					+ this.getClass().getName());
		}
	}

	private int getConnectedAtomPositionInBond(IAtom atom) {
		if (this.bond.getAtom(0) == atom) {
			return 0;
		} else {
			return 1;
		}
	}

	public int getConnectedAtomPosition() {
		return connectedAtomPosition;
	}

	public IAtom getConnectedAtom() {
		return connectedAtom;
	}

	public IBond getBond() {
		return bond;
	}
}
