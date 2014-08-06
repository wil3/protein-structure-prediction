package edu.stevens.cs.ssp.data;

import org.biojava.bio.structure.io.PDBFileParser;

public class OrthogonalSecondaryStructureEncoding implements SecondaryStructureEncoding{

	private double low = -1;
	private double high = 1;
	//@Override
	public double[] encode2(String structureName) {
		//Default all zeros is TURN
		double [] encode = new double [3];
		if (structureName == null){
			encode[0] = low;
			encode[1] = low;
			encode[2] = high;

		} else if (structureName.equals(PDBFileParser.HELIX)){
			encode[0] = high;
			encode[1] = low;
			encode[2] = low;
		} else if (structureName.equals(PDBFileParser.STRAND)){
			encode[0] = low;
			encode[1] = high;
			encode[2] = low;

		}
		
		return encode;
	}

	@Override
	public double[] encode(String structureName) {
		//Default all zeros is TURN
		double [] encode = new double [2];
		if (structureName == null){
			encode[0] = low;
			encode[1] = high;

		} else if (structureName.equals(PDBFileParser.HELIX)){
			encode[0] = low;
			encode[1] = high;
		} else if (structureName.equals(PDBFileParser.STRAND)){
			encode[0] = high;
			encode[1] = low;

		}
		
		return encode;
	}

}
