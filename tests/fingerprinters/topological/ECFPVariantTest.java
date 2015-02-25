package fingerprinters.topological;

import org.junit.BeforeClass;
import org.junit.Test;

import de.zbit.jcmapper.fingerprinters.topological.Encoding2DECFPVariant;

import fingerprinters.SameMoleculeTester;


public class ECFPVariantTest {
	static SameMoleculeTester tester;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tester = new SameMoleculeTester(new Encoding2DECFPVariant());
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
}
