package edu.stevens.cs.ssp.data;

import java.util.Arrays;

public class OrthogonalAminoAcidEncoding implements InputEncoding{
	private double low = 0;
	private double high = 1;
	String aminoAcids = "ARNDCEQGHILKMFPSTWYV";
	@Override
	public double[][] encode(Character[] aminoAcids) {

		double [][] encode = new double[aminoAcids.length][];
		
		for (int i=0; i<aminoAcids.length; i++){
			encode [i] = getEncoding(aminoAcids[i]);
		}
		
		return encode;
	}
	
	private double [] getEncoding(Character aminoAcid){
		double [] encode = new double [aminoAcids.length()];
		Arrays.fill(encode, low);
		if (aminoAcid != null){
			encode[aminoAcids.indexOf(aminoAcid)] = high;
		}
		return encode;
	}

}
