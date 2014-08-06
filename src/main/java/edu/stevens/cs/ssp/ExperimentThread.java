package edu.stevens.cs.ssp;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainerUtility;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;


public class ExperimentThread implements Callable<ThreadResponse>{
	public static Logger plotter = Logger.getLogger("plotter");

	private MLDataSet trainingSet;
	private MLDataSet testingSet;
	private int [] topology;
	private int windowSize;
	BasicNetwork network;

	public ExperimentThread(int windowSize, int [] topology, MLDataSet trainingSet,MLDataSet testingSet){
		this.topology = topology;
		this.trainingSet = trainingSet;
		this.testingSet = testingSet;
		this.windowSize = windowSize;
		this.network = new NeuralNetwork().build(topology);

	}
	@Override
	public ThreadResponse call() throws Exception {


		//System.out.println("Training...");
		TrainerUtility.RESOLUTION = Math.pow(10, -8);
		  long start = System.currentTimeMillis( );

		double err = TrainerUtility.backpropagationTrainer(network, trainingSet);//, 0.01, 0.2);
		long end = System.currentTimeMillis( );
        long diff = end - start;
         
        
        System.out.println("t=\t" + diff);
         
		//errMean += err;
		System.out.println("Error=" + err);
		//System.out.println("Testing...");
		double q3 = new TestNeuralNetwork(network, testingSet).getQ3();
		System.out.println("Q3=" + q3);
		System.out.println("");
		
		plotter.info(windowSize + "\t" + topology[1] + "\t" + q3 + "\t" + diff);
		
		return new ThreadResponse(err, q3);
	}
	

}
