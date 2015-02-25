package fingerprinters.topological;

import org.junit.BeforeClass;
import org.junit.Test;


import de.zbit.jcmapper.fingerprinters.topological.MACCS166;
import fingerprinters.SameMoleculeTester;


public class MACCS166KeyTest {
	
	static SameMoleculeTester tester;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tester = new SameMoleculeTester(new MACCS166());
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
