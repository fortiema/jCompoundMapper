package de.zbit.jcmapper.tools.moltyping.enumerations;

public class EnumerationsAtomTypes {
	public static enum AtomLabelType {
 		CDK_ATOM_TYPES,
		/** element symbol + #atom neighbours */
		ELEMENT_NEIGHBOR,
		/** element symbol + ring flag +#atom neighbours */
		ELEMENT_NEIGHBOR_RING,
		/** Only element symbols */
		ELEMENT_SYMBOL,
		CUSTOM,
		DAYLIGHT_INVARIANT,
		DAYLIGHT_INVARIANT_RING,
	}
}
