package de.zbit.jcmapper.fingerprinters;

import de.zbit.jcmapper.fingerprinters.FingerPrinterException.ErrorCode;
import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3DAtomPair;
import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3DAtomTriple;
import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3DCATS;
import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3DMolprint;
import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3DPharmacophore2Point;
import de.zbit.jcmapper.fingerprinters.geometrical.Encoding3DPharmacophore3Point;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DAllPaths;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DAllShortestPath;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DAtomPair;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DAtomTriple;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DCATS;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DECFP;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DECFPVariant;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DLocalAtomEnvironment;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DMolprint;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DPharmacophore2Point;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DPharmacophore3Point;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DSHEDKey;
import de.zbit.jcmapper.fingerprinters.topological.MACCS166;

public class FingerPrinterFactory {

	public static EncodingFingerprint getFingerprinter(FingerprintType type) throws FingerPrinterException{
		if (type == FingerprintType.MACCS){
			return new MACCS166();
		} else if (type == FingerprintType.RAD2D) {
			return new Encoding2DMolprint();
		} else if (type == FingerprintType.RAD3D) {
			return new Encoding3DMolprint();
		} else if (type == FingerprintType.ASP) {
			return new Encoding2DAllShortestPath();
		} else if (type == FingerprintType.AP2D) {
			return new Encoding2DAtomPair();
		} else if (type == FingerprintType.AT2D) {
			return new Encoding2DAtomTriple();
		} else if (type == FingerprintType.CATS2D) {
			return new Encoding2DCATS();
		} else if (type == FingerprintType.DFS) {
			return new Encoding2DAllPaths();
		} else if (type == FingerprintType.ECFP) {
			return new Encoding2DECFP();
		} else if (type == FingerprintType.ECFPVariant) {
			return new Encoding2DECFPVariant();
		} else if (type == FingerprintType.LSTAR) {
			return new Encoding2DLocalAtomEnvironment();
		} else if (type == FingerprintType.PHAP2POINT2D) {
			return new Encoding2DPharmacophore2Point();
		} else if (type == FingerprintType.PHAP3POINT2D) {
			return new Encoding2DPharmacophore3Point();
		} else if (type == FingerprintType.PHAP2POINT3D) {
			return new Encoding3DPharmacophore2Point();
		} else if (type == FingerprintType.PHAP3POINT3D) {
			return new Encoding3DPharmacophore3Point();
		} else if (type == FingerprintType.AP3D) {
			return new Encoding3DAtomPair();
		} else if (type == FingerprintType.AT3D) {
			return new Encoding3DAtomTriple();
		} else if (type == FingerprintType.SHED) {
			return new Encoding2DSHEDKey();
		} else if (type == FingerprintType.CATS3D) {
			return new Encoding3DCATS();
		} else if (type == FingerprintType.ECFPVariant) {
			return new Encoding2DECFPVariant();
		} else if (type == FingerprintType.ECFC) {
			return new Encoding2DECFP();
		}
		throw new FingerPrinterException(ErrorCode.UNKNOWN_FINGERPRINTER_TYPE,FingerPrinterFactory.class.toString(),type.toString());
	}
	
	public static enum FingerprintType {
		DFS,

		ASP,

		AP2D,

		AT2D,

		AP3D,

		AT3D,

		CATS2D,

		CATS3D,

		PHAP2POINT2D,

		PHAP3POINT2D,

		PHAP2POINT3D,

		PHAP3POINT3D,

		ECFP,
		
		ECFC,
		
		ECFPVariant,

		LSTAR,
		
		SHED,

		RAD2D,

		RAD3D,
		
		MACCS;
	}
}
