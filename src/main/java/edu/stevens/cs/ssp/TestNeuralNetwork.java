package edu.stevens.cs.ssp;

import java.util.Arrays;

import org.encog.mathutil.Equilateral;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;

public class TestNeuralNetwork {
	BasicNetwork network;
	MLDataSet dataset;
	double threshold = .3;

	public TestNeuralNetwork(BasicNetwork network, MLDataSet dataset){
		this.network = network;
		this.dataset = dataset;
	}
	private double test(){
		
		double [] mean = new double [] {0,0};
		double [] h_s_c = new double []{0,0,0};
		int correctPredicted = 0;
		for(MLDataPair example: dataset) {
			double[] ideal = example.getIdealArray();
			
			updateCount(ideal,h_s_c);
			
			double [] output = network.compute(example.getInput()).getData();
			if (ideal[0] == 0 && ideal[1] == 0){
				//System.out.println(Arrays.toString(output));
				mean[0] = mean[0] + output[0];
				mean[1] = mean[1] + output[1];

			}
			/*
			int winningNode = network.winner(example.getInput());
			
			if (ideal[winningNode] == 1){
				correctPredicted++;
				//h_s_c[winningNode] = h_s_c[winningNode] + 1;
			}
			*/
			if (Arrays.equals(ideal, getWinner(output))){
				correctPredicted++;
			}
		}
		
		System.out.println("c=" + mean[0]/h_s_c[2] + "," + mean[1]/h_s_c[2]);
		System.out.println( "h=" + h_s_c[0]/dataset.size() + " s=" + h_s_c[1]/dataset.size() + " c=" + h_s_c[2]/dataset.size() );
		return ((double)correctPredicted)/ ((double)dataset.size());//Q3(h_s_c, dataset.size());
	}
	private double winnerTakesAll(){
		
		//double [] mean = new double [] {0,0};
		//double [] h_s_c = new double []{0,0,0};
		double correctPredicted = 0;
		
		double totalSS=0;
		for(MLDataPair example: dataset) {
			double[] ideal = example.getIdealArray();
			
			//updateCount(ideal,h_s_c);
			if (ideal[0] == 1){  // its a sheet
				totalSS++;
				int winningNode = network.winner(example.getInput());
				if (winningNode == 0){
					correctPredicted++;
				}
			}
			
			
			//if (ideal[winningNode] == 1){
				//correctPredicted++;
			//}
			
			
		}
		
		//System.out.println("c=" + mean[0]/h_s_c[2] + "," + mean[1]/h_s_c[2]);
		//System.out.println( "h=" + h_s_c[0]/dataset.size() + " s=" + h_s_c[1]/dataset.size() + " c=" + h_s_c[2]/dataset.size() );
		//return ((double)correctPredicted)/ ((double)dataset.size());//Q3(h_s_c, dataset.size());
		return correctPredicted / totalSS;
	}
	
	private double equilateral(){
		int correctPredicted = 0;
		Equilateral eq = new Equilateral(3,1,0); 

		for(MLDataPair example: dataset) {
			double[] ideal = example.getIdealArray();						
			int winningNode = eq.decode(network.compute(example.getInput()).getData());
			int idealNode = eq.decode(ideal);
			if (idealNode == winningNode){
				correctPredicted++;
			}
			
			
		}
		
		return ((double)correctPredicted)/ ((double)dataset.size());
	}
	
	private double[] getWinner(double [] output){
		double [] clean = new double[2];
		if (output[0] <= threshold && output[1] <= threshold){
			clean[0] = 0;
			clean[1] = 0;
		} else if (output[0] > output[1]){
			clean[0] = 1;
			clean[1] = 0;
		} else {
			clean[0] = 0;
			clean[1] = 1;
		}
		
		return clean;
	}
	
	private void updateCount(double[] ideal,double [] h_s_c){
		if (ideal[0] == 1){
			h_s_c[0] = h_s_c[0] + 1;
		}else if (ideal[1] == 1){
			h_s_c[1] = h_s_c[1] + 1;
		} else {
			h_s_c[2] = h_s_c[2] + 1;
		}
	}
	
	public double getQ3(){
		//return equilateral();
		return winnerTakesAll();
	}
	private  double Q3(int[] h_s_c, int N){
		return (double)(h_s_c[0] + h_s_c[1] + h_s_c[2]) / (double) N;
	}
}
