package fingerprinters.topological;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DCATS;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;

import junit.framework.Assert;

public class CATS2DFingerprintTest {
	Encoding2DCATS fingerprinter = new Encoding2DCATS();

	@Test
	public void runTest() {
		RandomAccessMDLReader reader = null;
		try {
			reader = new RandomAccessMDLReader(new File("./resources/ACE_MM.sdf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < reader.getSize(); i++) {
			List<IFeature> fingerprint = fingerprinter.getFingerprint(reader.getMol(i));
			Assert.assertEquals(150, fingerprint.size());
		}
	}
}
