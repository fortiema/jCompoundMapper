package de.zbit.jcmapper.fingerprinters.topological;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import de.zbit.jcmapper.fingerprinters.FingerPrinterException;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.topological.features.ECFPFeature;
import de.zbit.jcmapper.tools.moltyping.MoltyperException;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;


public class Encoding2DECFP extends Encoding2D {
	private int iteration;
	private ArrayList<IFeature> completeFeatures;
	private IAtomContainer molecule;
	

	private Map<IAtom,ECFPFeature> featuresOfLastIteration;
	
	public Encoding2DECFP(){
		this.setAtomLabelType(AtomLabelType.DAYLIGHT_INVARIANT_RING);
	}

	@Override
	public ArrayList<IFeature> getFingerprint(IAtomContainer molecule){
		try{
			calculateFingerprint(molecule);
		}catch(Exception e){
			e.printStackTrace();
		}
		return this.completeFeatures;
	}
	
	private void calculateFingerprint(IAtomContainer ac) throws FingerPrinterException, MoltyperException,CDKException{
		this.iteration=0;
		this.completeFeatures=new ArrayList<IFeature>();
		this.molecule=ac;
		this.featuresOfLastIteration = new HashMap<IAtom,ECFPFeature>();
		
		computeInitialIdentifiers();
		
		for(int i=0;i<this.getSearchDepth();i++){
			iteration++;
			computeIteration();
		}
		
		// wegner: this is still ugly, I would prefer the molecule is persistent within this object, which it is only 
		//      for the lifetime of a calculateFingerprint function call, very strange design, and not in a good way.
		//      Anyway, workaround is to pass a molecule object along to the ECFPFeature as workaround.
		this.featuresOfLastIteration=null;
		this.molecule=null;
	}
	
	private void computeInitialIdentifiers() throws FingerPrinterException, MoltyperException{
		for(IAtom atom: molecule.atoms()){
            IAtomContainer substructure = new AtomContainer();
			substructure.addAtom(atom);
			for(IBond bond: molecule.getConnectedBondsList(atom)){
				substructure.addBond(bond);
			}
			//System.out.println("initial "+this.getAtomLabel(atom)+",id="+this.getAtomLabel(atom).hashCode());
			ECFPFeature ecfpFeature = new ECFPFeature(this, molecule, atom, substructure,this.iteration,this.getAtomLabel(atom).hashCode(), null);
			this.featuresOfLastIteration.put(atom, ecfpFeature);
			completeFeatures.add(ecfpFeature);
		}
	}
	
	private void computeIteration() throws FingerPrinterException, MoltyperException{
		Map<IAtom,ECFPFeature> featuresOfIteration = new HashMap<IAtom, ECFPFeature>();
		List<ECFPFeature> features = new LinkedList<ECFPFeature>();
		
		for(IAtom atom: featuresOfLastIteration.keySet()){
			ECFPFeature feature = computeIterationForAtom(atom);
			features.add(feature);
			featuresOfIteration.put(atom,feature);
		}
		
		removeDuplicateSubstructures(features);
		completeFeatures.addAll(features);
		this.featuresOfLastIteration = featuresOfIteration;
	}
	
	private ECFPFeature computeIterationForAtom(IAtom atom) throws FingerPrinterException, MoltyperException{
		ECFPFeature oldFeature = featuresOfLastIteration.get(atom);
        IAtomContainer newSubstructure = oldFeature.getNonDeepCloneOfSubstructure();
		List<BondOrderIdentifierTupel> connectivity = new ArrayList<BondOrderIdentifierTupel>();

		for(IAtom connectedAtom: molecule.getConnectedAtomsList(atom)){
			int identifierOfConnectedAtom = featuresOfLastIteration.get(connectedAtom).hashCode();
			//System.out.println("iterate "+connectedAtom.getAtomTypeName()+",id="+identifierOfConnectedAtom+",id="+featuresOfLastIteration.get(connectedAtom).featureToString(true));
			connectivity.add(new BondOrderIdentifierTupel(this.getBondOrder(molecule.getBond(atom,connectedAtom)),identifierOfConnectedAtom));
            IAtomContainer structure = this.featuresOfLastIteration.get(connectedAtom).representedSubstructure();
			for(IAtom a: structure.atoms()){
				if(!newSubstructure.contains(a))
					newSubstructure.addAtom(a);
			}
			for(IBond b: structure.bonds()){
				if(!newSubstructure.contains(b))
					newSubstructure.addBond(b);
			}
		}
		
		ECFPFeature newFeature = new ECFPFeature(this, molecule, atom, newSubstructure, this.iteration,oldFeature.hashCode(), connectivity);
		return newFeature;
	}
	
	public boolean isSubstructureHash() {
		return ECFPFeature.isSubstructureHash();
	}

	public void setSubstructureHash(boolean substructureHash) {
		ECFPFeature.setSubstructureHash(substructureHash);
	}
	
	private void removeDuplicateSubstructures(Collection<ECFPFeature> newFeatures){
		Iterator<ECFPFeature> iter = newFeatures.iterator();
		
		while(iter.hasNext()){
			ECFPFeature featureToCheck = iter.next();
			if(hasDuplicate(featureToCheck)){
				iter.remove();
				continue;
			}
			for(ECFPFeature feature: newFeatures){
				if(feature!=featureToCheck && featureToCheck.representsSameSubstructures(feature)){
					if(featureToCheck.hashCode()>=feature.hashCode()){
						iter.remove();
						break;
					}
				}
			}
		}
	}
	
	private boolean hasDuplicate(ECFPFeature feature){
		for(IFeature f: completeFeatures){
			if(feature.representsSameSubstructures((ECFPFeature)f)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getNameOfFingerPrinter() {
		return "ECFP";
	}
	
	public static BondOrderIdentifierTupel getNewBondOrderIdentifierTupel(int bondOrder, int atomIdentifier){
		return new BondOrderIdentifierTupel(bondOrder, atomIdentifier);
	}
	
	public static class BondOrderIdentifierTupel implements Comparable<BondOrderIdentifierTupel>{
		public int bondOrder;
		public int atomIdentifier;
		
		public BondOrderIdentifierTupel(int bondOrder, int atomIdentifier){
			this.bondOrder=bondOrder;
			this.atomIdentifier=atomIdentifier;
		}
		
		@Override
		public int compareTo(BondOrderIdentifierTupel o) {
			if(this.bondOrder<o.bondOrder)
				return -1;
			else if(this.bondOrder>o.bondOrder)
				return 1;
			else{
				if(this.atomIdentifier<o.atomIdentifier)
					return -1;
				else if(this.atomIdentifier>o.atomIdentifier)
					return 1;
				else
					return 0;
			}
		}
	}
	
	private int getBondOrder(IBond bond){
		if(bond.getFlag(CDKConstants.ISAROMATIC))
			return 4;
		else
			return bond.getOrder().ordinal();
		}
}
