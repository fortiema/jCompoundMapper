package de.zbit.jcmapper.tools.moltyping.maccs;

public class MACCSSmartsPattern {
	
	int position;
	String SMARTS;
	int frequency;
	String description;
	
	
	public MACCSSmartsPattern(int pos, String SMARTS, int freq, String des){
		this.position = pos;
		this.SMARTS = SMARTS;
		this.frequency = freq;
		this.description = des;
	}


	public int getPosition() {
		return position;
	}


	public String getSMARTS() {
		return SMARTS;
	}


	public int getFrequency() {
		return frequency;
	}


	public String getDescription() {
		return description;
	}
	
	
	
	
	
	

}
