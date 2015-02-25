package de.zbit.jcmapper.io.reader;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.util.Map;

public final class MoleculePreprocessor {

    /**
     * standard preparation protocol: remove hydrogens, types, ring detection. Uses specified errorHandler
     *
     * @param mol
     * @throws Exception
     */
    public static IAtomContainer prepareMoleculeRemoveHydrogens(IAtomContainer mol, IChemObjectReaderErrorHandler errorHandler)
    {
        boolean errorFlag=false;
        IAtomContainer molOrig=mol;
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            Aromaticity.cdkLegacy().apply(mol);
            mol = addExplicitHydrogens(mol);

        } catch (final Exception e) {
            errorFlag=true;

            if (errorHandler != null)
                errorHandler.handleError("An error occurred while typing structure. " + e.getMessage(), e);
            else{
                System.out.println("An error occurred while typing structure, using unprocessed molecule. "+e.getMessage());
                e.printStackTrace();
            }
        }
        mol = removeHydrogens(mol);
        if(errorFlag){
            mol=molOrig;
        }
        return mol;
    }


	/**
	 * standard preparation protocol: remove hydrogens, types, ring detection
	 * 
	 * @param mol
	 * @throws Exception
	 */
	public static IAtomContainer prepareMoleculeRemoveHydrogens(IAtomContainer mol) {
        return prepareMoleculeRemoveHydrogens(mol, null);
	}

	
	/**
	 * types the molecule, leaves the hydrogens attached. Uses specified error handler
	 * @param mol
	 * @return
	 */
	public static IAtomContainer prepareMoleculeConserveHydrogens(IAtomContainer mol, IChemObjectReaderErrorHandler errorHandler) {
		try {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            Aromaticity.cdkLegacy().apply(mol);
		} catch (final Exception e) {
            if (errorHandler != null){
                errorHandler.handleError("An error occurred while typing structure:"+e.getMessage(), e);
            } else {
			    System.out.println("An error occurred while typing structure:"+e.getMessage());
                e.printStackTrace();
            }
		}
		return mol;
	}

    /**
     * types the molecule, leaves the hydrogens attached
     * @param mol
     * @return
     */
    public static IAtomContainer prepareMoleculeConserveHydrogens(IAtomContainer mol)
    {
        return prepareMoleculeConserveHydrogens(mol, null);
    }


	/**
	 * remove hydrogens
	 * 
	 * @param mol
	 * @throws Exception
	 */
	private static IAtomContainer removeHydrogens(IAtomContainer mol) {
		final Map<Object, Object> map = mol.getProperties();
		mol = AtomContainerManipulator.suppressHydrogens(mol);  // does not remove bridging hydrogens by default
		mol.setProperties(map);
		return mol;
	}
	
	/**
	 * add hydrogens
	 * 
	 * @param mol
	 * @throws CDKException
	 */
	private static IAtomContainer addExplicitHydrogens(IAtomContainer mol) throws CDKException{

		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
		for (IAtom atom : mol.atoms()) {
		     IAtomType type = matcher.findMatchingAtomType(mol, atom);
		     try{
		    	 AtomTypeManipulator.configure(atom, type);
		     }
		     catch(IllegalArgumentException e){
		    	 throw new CDKException(e.toString()+" for atom "+atom.getAtomicNumber()+" "+atom.getSymbol());
		     }
		   }
		CDKHydrogenAdder hydrogenAdder = CDKHydrogenAdder.getInstance(mol.getBuilder());
		hydrogenAdder.addImplicitHydrogens(mol);
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);

		return mol;

	}

}
