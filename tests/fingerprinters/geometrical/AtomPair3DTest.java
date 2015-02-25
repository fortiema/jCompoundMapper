package fingerprinters.geometrical;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3DAtomPair;
import de.zbit.jcmapper.tools.moltyping.enumerations.EnumerationsAtomTypes.AtomLabelType;

import fingerprinters.SameMoleculeTester;

public class AtomPair3DTest {
	static SameMoleculeTester tester;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tester = new SameMoleculeTester(new Encoding3DAtomPair());
	}

	@Test
	public void checkLength() {
		tester.checkLength();
	}
	
	@Test
	public void checkFeatures() {
		tester.checkFeatures();
	}
	
	@Test
	public void checkHashedFeatures() {
		tester.checkHashedFeatures();
	}
	
	@Test
	public void checkParameter(){
		Encoding3DAtomPair fingerprint = new Encoding3DAtomPair();
		fingerprint.setAtomLabelType(AtomLabelType.CDK_ATOM_TYPES);
		Assert.assertEquals(fingerprint.getAtomLabelType(), AtomLabelType.CDK_ATOM_TYPES); 
		fingerprint.setDistanceCutoff(5.5);
		Assert.assertEquals(fingerprint.getDistanceCutoff(), 5.5, 0.0);
		fingerprint.setStretchingFactor(5.5);
		Assert.assertEquals(fingerprint.getDistanceCutoff(), 5.5, 0.0);
	}
}
