package fingerprinters.topological;

import org.junit.BeforeClass;
import org.junit.Test;

import de.zbit.jcmapper.fingerprinters.topological.Encoding2DSHEDKey;

import fingerprinters.SameMoleculeTester;


public class SHEDKeyTest {
	static SameMoleculeTester tester;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tester = new SameMoleculeTester(new Encoding2DSHEDKey());
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
