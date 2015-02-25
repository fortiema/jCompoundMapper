/*
 * Author: Joerg Kurt Wegner (JKW), me@joergkurtwegner.eu
 * Copyright JKW, 2012. All rights reserved.
 * 2011-12-14
 */
package de.zbit.jcmapper.io.writer;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import de.zbit.jcmapper.fingerprinters.EncodingFingerprint;
import de.zbit.jcmapper.fingerprinters.features.FeatureMap;
import de.zbit.jcmapper.fingerprinters.features.IFeature;
import de.zbit.jcmapper.fingerprinters.topological.Encoding2DECFP;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.io.writer.feature.SortableFeature;
import de.zbit.jcmapper.tools.progressbar.ProgressBar;

public class ExporterSQLite implements IExporter {

	@Override
	public void export(RandomAccessMDLReader reader, EncodingFingerprint fingerprinter, String label, File outputFile, boolean useAromaticFlag) {
		// WARNING: This is extremely slow, so beware or try doing some parts in memory
		//          For now this scales to any size, since it operates fully on SQL queries, but
		//          this slows down things.
		// WARNING2: This does only work for a single execution, not for an export into an
		//           already existing SQLite DB
		final boolean createPivotedTable= false;
		
		final SQLiteConnection db = new SQLiteConnection(outputFile);
		//avoid special characters in table name
		String fingerprinterName=fingerprinter.getNameOfFingerPrinter();
		fingerprinterName=fingerprinterName.replace(' ', '_');
		fingerprinterName=fingerprinterName.replace('-', '_');
		
		//if(fingerprinter.getNameOfFingerPrinter().equals("ECFP")){
	    //  	//switch on substructure hashes, aka do not use default iteration and parent hash lists
	    //  	((Encoding2DECFP)fingerprinter).setSubstructureHash(true);
	    //   }

		final String tableDictionary="dictionary"+fingerprinterName;
		final String tableFingerprint="fingerprint"+fingerprinterName;
		final String tableCompounds="compounds"+fingerprinterName;
		final String tableFingerprintPivoted="fingerprintpivot"+fingerprinterName;
		try {
			db.open(true);
			//dictionary
			//db.exec("DROP TABLE IF EXISTS "+tableDictionary);
			db.exec("CREATE TABLE IF NOT EXISTS "+tableDictionary+"(encoding TEXT, fp INTEGER PRIMARY KEY);");
			db.exec("CREATE UNIQUE INDEX IF NOT EXISTS Index_"+tableDictionary+"_fp ON "+tableDictionary+"(fp);");
			//fingerprint
			//db.exec("DROP TABLE IF EXISTS "+tableFingerprint);
			db.exec("CREATE TABLE IF NOT EXISTS "+tableFingerprint+"(compoundnbr INTEGER, fp INTEGER, value REAL);");
			//compounds
			//db.exec("DROP TABLE IF EXISTS "+tableCompounds);
			db.exec("CREATE TABLE IF NOT EXISTS "+tableCompounds+"(compoundid TEXT PRIMARY KEY, compoundnbr INTEGER);");
			//fingerprint pivoted
			if(createPivotedTable){
				//just an initial table, the rest will be created dynamically
				//db.exec("DROP TABLE IF EXISTS "+tableFingerprintPivoted);
				db.exec("CREATE TABLE IF NOT EXISTS "+tableFingerprintPivoted+"(compoundnbr INTEGER PRIMARY KEY);");
			}
		}
		catch (SQLiteException e) 
		{
			// create database(s)
			System.out.println(e);
		}
			
		int collisions = 0;
		java.util.Locale.setDefault(java.util.Locale.ENGLISH);

		Long start = System.currentTimeMillis();
		
		///////////////////////
		//Encoding molecules
		System.out.println("Encoding molecules");
		ProgressBar progressBar = new ProgressBar(reader.getSize());
		SQLiteStatement st = null;
		int cmpdCounter=1;
		try {
			//re-assign fpString to a matching one created previously 
			st=db.prepare("SELECT MAX(compoundnbr) FROM "+tableCompounds);
			while (st.step()) {
				cmpdCounter=st.columnInt(0);
				cmpdCounter=cmpdCounter+1;
			}
		} 
		catch (SQLiteException e2) 
		{       
			cmpdCounter=1;
		}

		for (int i = 0; i < reader.getSize(); i++) {
			IAtomContainer mol = reader.getMol(i);
			FeatureMap featureMap = new FeatureMap(fingerprinter.getFingerprint(mol));
			String molLabel = (String) mol.getProperty(label);
			if (molLabel != null) {
				featureMap.setLabel(molLabel);
			} else {
				featureMap.setLabel(ExporterHelper.getMolName(mol) + "_INDEX=" + cmpdCounter);
			}

			String cmpdLabel=featureMap.getLabel();
			String featureString=null;
			int fpInteger=-1;
			try {
				db.exec("INSERT INTO "+tableCompounds+"(compoundid,compoundnbr) VALUES ('"+cmpdLabel+"','"+cmpdCounter+"');");
				if(createPivotedTable){
					db.exec("INSERT INTO "+tableFingerprintPivoted+"(compoundid,"+cmpdLabel+") VALUES ('"+cmpdLabel+"','"+cmpdCounter+"');");
				}
				db.exec("BEGIN;");
			} 
			catch (SQLiteException e) 
			{   
				// compound exists, but for which fingerprint routine?
				try {
					st=db.prepare("SELECT compoundnbr FROM "+tableCompounds+" WHERE compoundid = ?");
					st.bind(1, cmpdLabel);
					int cmpdCounter2use=-1;
					while (st.step()) {
						cmpdCounter2use=st.columnInt(0);
					}
					st=db.prepare("SELECT compoundnbr FROM "+tableFingerprint+" WHERE compoundnbr = ?");
					st.bind(1, cmpdCounter2use);
					while (st.step()) {
						cmpdCounter2use=st.columnInt(0);
					}
					System.out.println("Compound exists already, skipping "+fingerprinterName+" calculation for '"+cmpdLabel+"' ("+cmpdCounter2use+")");
					continue;
					//skip this calculation routine and do not add anything to the DB
				} 
				catch (SQLiteException e2) 
				{       
					//All fine compound does not encode this fingerprint
				}
			}
			
			Set<IFeature> featureKeys = featureMap.getKeySet();
			HashMap<Integer, SortableFeature> features = new HashMap<Integer, SortableFeature>();
			for (IFeature feature : featureKeys) {
				int hashCode = feature.hashCode();
				if (features.containsKey(hashCode)) {
					collisions++;
				} else {
					features.put(hashCode, new SortableFeature(feature, useAromaticFlag));
				}
			}
			
			featureString="";
			for (Integer hashCode : features.keySet()){
				SortableFeature feature=features.get(hashCode);
				fpInteger=hashCode;
				//DISABLED, since time complexity bottleneck
				//USE ExporterNumericSQLite is really needed
				//featureString=feature.getString(useAromaticFlag) + ":" + df.format(feature.getValue());
				//featureString=feature.getString(useAromaticFlag);
				double fpValue = feature.getValue();
				try {
					db.exec("INSERT INTO "+tableDictionary+"(encoding, fp) VALUES ('"+featureString+"','"+fpInteger+"');");
				} 
				catch (SQLiteException e) 
				{       
					// skipping duplicates
				}
				//System.out.println("Details: encoding='"+featureString+"', fp='"+fpString+"'");
				try {
					db.exec("INSERT INTO "+tableFingerprint+"(compoundnbr, fp, value) VALUES ('"+cmpdCounter+"','"+fpInteger+"','"+fpValue+"');");
				} 
				catch (SQLiteException e) 
				{       
					System.out.println(e);
				}
			}
			
			try {
				db.exec("COMMIT;");
			} 
			catch (SQLiteException e) 
			{       
				System.out.println(e);
			}
			progressBar.DisplayBar();
			
			cmpdCounter=cmpdCounter+1;
		}

		Long end = null;
		///////////////////////
		//create pivoted fingerprint table
		if(createPivotedTable){
			end = System.currentTimeMillis();
			System.out.println("Time elapsed: " + (end - start) + " ms");
			System.out.println("Creating fingerprint pivot table");
			try {
				//get all fingerprints, add columns to the pivot table, and initialize them with '0' 
				st=db.prepare("SELECT fp FROM "+tableDictionary);
				String fpString=null;
				String fpHead="fp";
				while (st.step()) {
					fpString=fpHead+st.columnInt(0);
					db.exec("ALTER TABLE "+tableFingerprintPivoted+" ADD COLUMN "+fpString+" INTEGER");
				}
				//now initialize with OFF bits to '0' 
				db.exec("BEGIN;");
				st=db.prepare("SELECT fp FROM "+tableDictionary);
				while (st.step()) {
					fpString=fpHead+st.columnString(0);
					SQLiteStatement st2=db.prepare("SELECT compoundnbr FROM "+tableCompounds);
					int cmpdNbr=-1;
					while (st2.step()) {
						cmpdNbr=st2.columnInt(0);
						db.exec("UPDATE "+tableFingerprintPivoted+" SET "+fpString+" = 0 WHERE compoundnbr='"+cmpdNbr+"'");
					}
				}
				db.exec("COMMIT;");
				//now set ON bits to '1' 
				db.exec("BEGIN;");
				st=db.prepare("SELECT compoundid, fp FROM "+tableFingerprint);
				int cmpdNbr=-1;
				while (st.step()) {
					cmpdNbr=st.columnInt(0);
					fpString=fpHead+st.columnString(1);
					db.exec("UPDATE "+tableFingerprintPivoted+" SET "+fpString+" = 1 WHERE compoundnbr='"+cmpdNbr+"'");
				}
				db.exec("COMMIT;");
			} 
			catch (SQLiteException e) 
			{       
				System.out.println(e);
			}
		}
		///////////////////////
		//creating table indices
		end = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (end - start) + " ms");
		System.out.println("Creating table indices");
		try {
			db.exec("CREATE INDEX IF NOT EXISTS Index_"+tableFingerprint+"_compoundnbr ON "+tableFingerprint+"(compoundnbr);");
			db.exec("CREATE INDEX IF NOT EXISTS Index_"+tableFingerprint+"_fp ON "+tableFingerprint+"(fp);");
			db.exec("CREATE INDEX IF NOT EXISTS Index_"+tableCompounds+"_compoundnbr ON "+tableCompounds+"(compoundnbr);");
		}
		catch (SQLiteException e) 
		{       
			System.out.println(e);
		}
		end = System.currentTimeMillis();
		System.out.println("Time elapsed: " + (end - start) + " ms");
		System.out.println("Collisions:" + collisions);
		db.dispose();
	}

}