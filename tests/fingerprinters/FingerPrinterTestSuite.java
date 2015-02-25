package fingerprinters;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import fingerprinters.geometrical.AtomPair3DTest;
import fingerprinters.geometrical.AtomTriple3DTest;
import fingerprinters.geometrical.CATS3DTest;
import fingerprinters.geometrical.Molprint3DTest;
import fingerprinters.geometrical.Pharmacophore2Point3DTest;
import fingerprinters.geometrical.Pharmacophore3Point3DTest;
import fingerprinters.topological.AllShortestPathTest;
import fingerprinters.topological.AtomPair2DTest;
import fingerprinters.topological.AtomTriple2DTest;
import fingerprinters.topological.CATS2DKeyTest;
import fingerprinters.topological.DepthFirstSearchTest;
import fingerprinters.topological.ECFPTest;
import fingerprinters.topological.ECFPVariantTest;
import fingerprinters.topological.LocalAtomEnvironmentsStarTest;
import fingerprinters.topological.Molprint2DTest;
import fingerprinters.topological.Pharmacophore2Point2DTest;
import fingerprinters.topological.Pharmacophore3Point2DTest;
import fingerprinters.topological.RadialDepthFirstSearchTest;
import fingerprinters.topological.SHEDKeyTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	DepthFirstSearchTest.class,
	SHEDKeyTest.class,
	AtomPair3DTest.class,
	AtomTriple3DTest.class,
	CATS3DTest.class,
	Molprint3DTest.class,
	Pharmacophore2Point3DTest.class,
	Pharmacophore3Point3DTest.class,
	AllShortestPathTest.class,
	AtomPair2DTest.class,
	AtomTriple2DTest.class,
	CATS2DKeyTest.class,
	ECFPVariantTest.class,
	ECFPTest.class,
	LocalAtomEnvironmentsStarTest.class,
	Molprint2DTest.class,
	Pharmacophore2Point2DTest.class,
	Pharmacophore3Point2DTest.class,
	RadialDepthFirstSearchTest.class
})
public class FingerPrinterTestSuite {

}
