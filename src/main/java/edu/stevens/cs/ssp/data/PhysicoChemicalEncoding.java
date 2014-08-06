package edu.stevens.cs.ssp.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

public class PhysicoChemicalEncoding implements InputEncoding {
	

	public static final double NORMALIZED_LOW = -1;
	public static final double NORMALIZED_HIGH = 1;

	private final HashMap<Character,Double> v = new HashMap<Character,Double>();
	private final HashMap<Character,Double> h = new HashMap<Character,Double>();

	
	
	public PhysicoChemicalEncoding(){
		
		initVolumes();
		initHydrophobicities();
		normalize(v);
		normalize(h);
	}

	private void initVolumes(){
		v.put(null, 0.0);
		v.put('A', 88.6);
		v.put('R', 173.4);
		v.put('N', 114.1);
		v.put('D', 111.1);
		v.put('C', 108.5);
		v.put('Q', 143.8);
		v.put('E', 138.4);
		v.put('G', 60.1);
		v.put('H', 153.2);
		v.put('I', 166.7);
		v.put('L', 166.7);
		v.put('K', 168.6);
		v.put('M', 162.9);
		v.put('F', 189.9);
		v.put('P', 112.7);
		v.put('S', 89.0);
		v.put('T', 116.1);
		v.put('W', 227.8);
		v.put('Y', 193.6);
		v.put('V', 140.0);
	}
	private void initHydrophobicities(){
		h.put(null, 0.0);
		h.put('A', 1.8);
		h.put('R', -4.5);
		h.put('N', -3.5);
		h.put('D', -3.5);
		h.put('C', 2.5);
		h.put('Q', -3.5);
		h.put('E', -3.5);
		h.put('G', -0.4);
		h.put('H', -3.2);
		h.put('I', 4.5);
		h.put('L', 3.8);
		h.put('K', -3.9);
		h.put('M',1.9);
		h.put('F', 2.8);
		h.put('P',-1.6);
		h.put('S', -0.8);
		h.put('T', -0.7);
		h.put('W', -0.9);
		h.put('Y', -1.3);
		h.put('V', 4.2);
	}
	
	//FIXME dont have hardcoded
	private void normalize(HashMap<Character,Double> data){	

		double alow = getLowestValue(data);
		double ahigh = getHighestValue(data);

		NormalizedField norm = new NormalizedField(NormalizationAction.Normalize, 
			null,ahigh,alow,NORMALIZED_HIGH,NORMALIZED_LOW);
	
		Iterator<Character> it = data.keySet().iterator();
		while (it.hasNext()){
			Character key = it.next();
			Double value = data.get(key);
			double normalized = norm.normalize(value);
			data.put(key, normalized);
		}
	}
	
	private double getLowestValue(HashMap<Character,Double> data){
		double lowest = Double.POSITIVE_INFINITY;
		for (Double value : data.values()){
			if (value < lowest){
				lowest = value;
			}
		}
		return lowest;
	}
	private double getHighestValue(HashMap<Character,Double> data){
		double highest = Double.NEGATIVE_INFINITY;
		for (Double value : data.values()){
			if (value > highest){
				highest = value;
			}
		}
		return highest;
	}
	
	
	
	@Override
	public double[][] encode(Character[] aminoAcids) {
		double [][] encoded = new double [aminoAcids.length][2];
		for (int i=0; i<aminoAcids.length; i++){
			Character aa = aminoAcids[i];
			double [] pair = new double [2];
			pair[0] = v.get(aa);
			pair[1] = h.get(aa);
			encoded[i] = pair;
		}
		return encoded;
	}

}
