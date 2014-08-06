package edu.stevens.cs.ssp.data;

import org.biojava.bio.structure.io.PDBFileParser;
import org.encog.mathutil.Equilateral;

public class EquilateralSecondaryStructureEncoding implements SecondaryStructureEncoding{

	private double low = 0;
	private double high = 1;
	
	
	@Override
	public double[] encode(String structureName) {
		Equilateral eq = new Equilateral(3,high,low); 
		
		//Default all zeros is TURN
		double [] encode = new double [2];
		int clazz=0;
		if (structureName == null){
			clazz = 2;

		} else if (structureName.equals(PDBFileParser.HELIX)){
			clazz = 0;

		} else if (structureName.equals(PDBFileParser.STRAND)){
			
			clazz = 1;

		}
		
		
		
		return eq.encode(clazz);
	}

}
