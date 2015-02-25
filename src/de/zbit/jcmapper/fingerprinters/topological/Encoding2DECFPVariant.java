package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.FingerPrinterException;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DECFP.BondOrderIdentifierTupel;
import de.zbit.jcmapper.fingerprinters.topological.features.DanglingBond;
import de.zbit.jcmapper.fingerprinters.topological.features.ECFPFeature;
import de.zbit.jcmapper.fingerprinters.topological.features.ECFPVariantFeature;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public class Encoding2DECFPVariant extends Encoding2D {
	private int currentIteration;
	private ArrayList<IFeature> completeFeatures;
	private IAtomContainer molecule;
	private Map<IAtom, Integer> hashedAtomLabels;
	private Map<IAtom, ECFPVariantFeature> featuresOfLastIteration;

	public Encoding2DECFPVariant() {
		super.setSearchDepth(4);
		super.setAtomLabelType(AtomLabelType.DAYLIGHT_INVARIANT_RING);
	}
	
	@Override
	public ArrayList<IFeature> getFingerprint(IAtomContainer molecule) {
		try {
			this.calculateFingerprint(molecule);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return this.completeFeatures;
	}
	
	private void calculateFingerprint(IAtomContainer ac) throws FingerPrinterException, MoltyperException{
		this.molecule = ac;
		this.initialize();
		this.computeInitialIteration();
		
		for (int i = 0; i < super.getSearchDepth(); i++) {
			this.currentIteration++;
			this.computeIteration();
		}
		
	}
	
	private void initialize() throws MoltyperException, FingerPrinterException{
		this.hashedAtomLabels = new HashMap<IAtom, Integer>();
		this.featuresOfLastIteration = new HashMap<IAtom, ECFPVariantFeature>();
		this.currentIteration = 0;
		this.completeFeatures = new ArrayList<IFeature>();
	}
	
	private void computeInitialIteration() throws MoltyperException, FingerPrinterException{
		for (IAtom atom : this.molecule.atoms()) {
            IAtomContainer substructure = new AtomContainer();
			substructure.addAtom(atom);
			ECFPVariantFeature ecfpFeature = new ECFPVariantFeature(this, molecule, atom, substructure,
													this.generateExtensionBondList(atom), this.currentIteration,this.getAtomLabel(atom).hashCode(),null);
			this.hashedAtomLabels.put(atom, ecfpFeature.hashCode());
			this.featuresOfLastIteration.put(atom, ecfpFeature);
			this.completeFeatures.add(ecfpFeature);
		}
	}
	
	private DanglingBond[] generateExtensionBondList(IAtom atom) throws FingerPrinterException {
		List<IBond> bonds = this.molecule.getConnectedBondsList(atom);
		DanglingBond[] connectivityBonds = new DanglingBond[bonds.size()];
		int i = 0;
		for (IBond bond : bonds) {
			connectivityBonds[i] = new DanglingBond(bond, bond.getConnectedAtom(atom));
			i++;
		}
		return connectivityBonds;
	}

	private void computeIteration() throws FingerPrinterException, MoltyperException {
		List<ECFPVariantFeature> newFeatures = new ArrayList<ECFPVariantFeature>();
		
		for (IAtom atom : this.hashedAtomLabels.keySet()) {
			newFeatures.add(this.computeIterationForAtom(atom));
		}
		
		this.removeDuplicateSubstructures(newFeatures);
		this.completeFeatures.addAll(newFeatures);
	}

	private ECFPVariantFeature computeIterationForAtom(IAtom atom) throws FingerPrinterException, MoltyperException {
		final ECFPVariantFeature oldFeature = this.featuresOfLastIteration.get(atom);
		final IAtomContainer newSubstructure = oldFeature.getNonDeepCloneOfSubstructure();
		final int numDanglingBonds = oldFeature.numberOfDanglingBonds();

		final List<BondOrderIdentifierTupel> connections = new ArrayList<BondOrderIdentifierTupel>(numDanglingBonds);
		final Map<IBond, DanglingBond> newConnectionCandidates = new HashMap<IBond, DanglingBond>();

		for (int i = 0; i < numDanglingBonds; i++) {
			final DanglingBond connection = oldFeature.getDanglingBond(i);
			final IAtom connectedAtom = connection.getBond().getAtom(connection.getConnectedAtomPosition());
			final int identifierOfConnectedAtom = this.hashedAtomLabels.get(connectedAtom);
			newSubstructure.addAtom(connectedAtom);
			newSubstructure.addBond(connection.getBond());
			final BondOrderIdentifierTupel boIDtupel=Encoding2DECFP.getNewBondOrderIdentifierTupel(this.getBondOrder(connection.getBond()), identifierOfConnectedAtom);
			connections.add(boIDtupel);

			final ArrayList<DanglingBond> newConnections = this.getConnectionsOfAtom(connection.getBond(), connectedAtom);
			for (final DanglingBond dbond : newConnections) {
				if (!newConnectionCandidates.containsKey(dbond.getBond())) {
					newConnectionCandidates.put(dbond.getBond(), dbond);
				}
			}
		}

		final Iterator<DanglingBond> iter = newConnectionCandidates.values().iterator();
		
		while (iter.hasNext()) {
			final DanglingBond bondToCheck = iter.next();
			if (newSubstructure.contains(bondToCheck.getConnectedAtom())) {
				if (!newSubstructure.contains(bondToCheck.getBond())) {
					newSubstructure.addBond(bondToCheck.getBond());
				}
				iter.remove();
			}
		}
		
		final DanglingBond[] newDanglingBonds = newConnectionCandidates.values().toArray(
				new DanglingBond[newConnectionCandidates.size()]);
		
		final ECFPVariantFeature newFeature = new ECFPVariantFeature(this, molecule, atom, newSubstructure, newDanglingBonds,
				this.currentIteration,oldFeature.hashCode(), connections);
		
		this.featuresOfLastIteration.put(atom, newFeature);
		return newFeature;
	}

	private int getBondOrder(IBond bond) throws MoltyperException {
		if (bond.getFlag(CDKConstants.ISAROMATIC)) {
			return 4;
		} else {
			return bond.getOrder().ordinal();
		}
	}

	private ArrayList<DanglingBond> getConnectionsOfAtom(IBond fromBond, IAtom atom) throws FingerPrinterException {
		final ArrayList<DanglingBond> connections = new ArrayList<DanglingBond>();
		for (final IBond bond : this.molecule.getConnectedBondsList(atom)) {
			if (bond != fromBond) {
				connections.add(new DanglingBond(bond, bond.getConnectedAtom(atom)));
			}
		}
		return connections;
	}

	@Override
	public String getNameOfFingerPrinter() {
		return "ECFP";
	}

	private boolean hasDuplicate(ECFPVariantFeature feature) {
		for (final IFeature f : this.completeFeatures) {
			if (feature.hasEqualSubstructure((ECFPVariantFeature) f)) {
				return true;
			}
		}
		return false;
	}

	private void removeDuplicateSubstructures(List<ECFPVariantFeature> newFeatures) {
		final Iterator<ECFPVariantFeature> iter = newFeatures.iterator();

		while (iter.hasNext()) {
			final ECFPVariantFeature featureToCheck = iter.next();
			if (this.hasDuplicate(featureToCheck)) {
				iter.remove();
				continue;
			}
			for (final ECFPVariantFeature feature : newFeatures) {
				if (feature != featureToCheck && featureToCheck.hasEqualSubstructure(feature)) {
					if (featureToCheck.hashCode() >= feature.hashCode()) {
						iter.remove();
						break;
					}
				}
			}
		}
	}

}
