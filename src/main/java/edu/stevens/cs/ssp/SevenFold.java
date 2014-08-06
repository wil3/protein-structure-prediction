package edu.stevens.cs.ssp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.biojava.bio.structure.StructureException;
import org.encog.Encog;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainerUtility;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;

import edu.stevens.cs.ssp.data.DatasetBuilder;
import edu.stevens.cs.ssp.data.FileConcatenation;
import edu.stevens.cs.ssp.data.OrthogonalSecondaryStructureEncoding;
import edu.stevens.cs.ssp.data.PhysicoChemicalEncoding;

public class SevenFold {
	public static Logger plotter = Logger.getLogger("plotter");
	
	//Test, train datasets
	private final String [][] datasets = new String[][]{
			
			{"0.csv", "1,2,3,4,5,6.csv"},
			{"1.csv", "0,2,3,4,5,6.csv"},
			{"2.csv", "0,1,3,4,5,6.csv"},
			{"3.csv", "0,1,2,4,5,6.csv"},
			{"4.csv", "0,1,2,3,5,6.csv"},
			{"5.csv", "0,1,2,3,4,6.csv"},
			{"6.csv", "0,1,2,3,4,5.csv"}

			
	};
	private final int NUMBER_AA = 20;
	File datasetDirectory;

	
	public SevenFold(File datasetDirectory){
		this.datasetDirectory = datasetDirectory;
	}
	
	public void runParrellel(int windowSize, int numberOutputs, int numberHiddenNodes, int vectorSize){
		ExecutorService pool = Executors.newFixedThreadPool(datasets.length);
		Set<Future<ThreadResponse>> set = new HashSet<Future<ThreadResponse>>();

		int numberInputs = windowSize * vectorSize;
	//	int i=0;
		double q3Mean = 0;
		double errMean = 0;
		for (int i=0; i<datasets.length; i++){
			System.out.println("Testing " + i);

			File testFile = new File(datasetDirectory, datasets[i][0]);
			File trainFile = new File(datasetDirectory, datasets[i][1]);
			MLDataSet testDataset = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH,testFile.getAbsolutePath(), false, numberInputs, numberOutputs);
			MLDataSet trainDataset = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH,trainFile.getAbsolutePath(), false, numberInputs, numberOutputs);
			int [] topology = new int [] {numberInputs, numberHiddenNodes, numberOutputs};
			
			Callable<ThreadResponse> callable = new ExperimentThread(windowSize,topology, trainDataset, testDataset);
			Future<ThreadResponse> future = pool.submit(callable);
			set.add(future);
		
		}
		
		for (Future<ThreadResponse> future : set) {
			try {
				ThreadResponse	res = future.get();
				q3Mean += res.getQ3();
				errMean += res.getError();

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}
		System.out.println("Mean Error=" + (errMean/datasets.length));
		System.out.println("Mean Q3=" + (q3Mean/datasets.length));
		System.out.println("");
		System.out.println("");
		
		
		
	}
	public void run(int windowSize, int numberOutputs, int numberHiddenNodes){
		int numberInputs = windowSize * NUMBER_AA;
	//	int i=0;
		double q3Mean = 0;
		double errMean = 0;
		for (int i=0; i<datasets.length; i++){
	//	for (double j=0.0; j < 0.9; j+=0.1 ){
			System.out.println("Testing " + i);

			File testFile = new File(datasetDirectory, datasets[i][0]);
			File trainFile = new File(datasetDirectory, datasets[i][1]);
			MLDataSet testDataset = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH,testFile.getAbsolutePath(), false, numberInputs, numberOutputs);
			MLDataSet trainDataset = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH,trainFile.getAbsolutePath(), false, numberInputs, numberOutputs);

			//System.out.println("Training...");
			BasicNetwork network = new NeuralNetwork().build(new int [] {numberInputs, numberHiddenNodes, numberOutputs});
			double err = TrainerUtility.backpropagationTrainer(network, trainDataset);//, 0.01, 0.2);
			errMean += err;
			//System.out.println("Error=" + err);
			//System.out.println("Testing...");
			double q3 = new TestNeuralNetwork(network, testDataset).getQ3();
			q3Mean += q3;
			//System.out.println("Q3=" + q3);
			//System.out.println("");
		}
		
		System.out.println("Mean Training Error=" + (errMean/datasets.length));
		System.out.println("Mean Q3=" + (q3Mean/datasets.length));

		System.out.println("");
		System.out.println("");
		
		
		
	}
	
	
	
	/**
	 * 
	 * Parameters:
	 * 
	 * -range to init weights
	 * -learning rate
	 * -momentum
	 * -number hidden units
	 * -number of outputs (ie helix or not, strand or not)
	 * -window size
	 * -training algorithm
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int lookAhead = 9;
		int lookBehind = lookAhead;
		//createDatasets(8);
		
		//FIXME 
		File datasetDirectory = new File("datasets");

		SevenFold sf = new SevenFold(datasetDirectory);
		int vectorSize = 2;
		int outputNodes = 2;
		
		int windowSize = lookBehind + lookAhead + 1;
		int hiddenNodes = 15;
	//	for (int i=2; i<20;i++){
		//int i = 2;
			System.out.println("***" + hiddenNodes);
		
			sf.runParrellel(windowSize, outputNodes, hiddenNodes, vectorSize);
	//	}
		Encog.getInstance().shutdown();

	}
	
	public static void createDatasets(int win){
		//FIXME to correction location, change to cfg file or pass as parameter
		String pdbHome = "data";
		//A file where every line corresponds to the proteins in the partition
		File partitionDescriptionFile = new File("proteins.csv");
		File datasetDirectory = new File("datasets");
		
		
		int lookAhead = win;
		int lookBehind = lookAhead;
		DatasetBuilder builder = new DatasetBuilder(pdbHome,lookAhead,lookBehind);
		
		//builder.setInputEncoder(new OrthogonalAminoAcidEncoding());
		builder.setInputEncoder(new PhysicoChemicalEncoding());

		builder.setOutputEncoder(new OrthogonalSecondaryStructureEncoding());
		
		try {
			builder.build(datasetDirectory, partitionDescriptionFile);
		} catch (StructureException e) {
			e.printStackTrace();
		}
		
		//set up all possible files for use
		File [] files = new File[7];
		for (int i=0;i<files.length; i++){
			files[i] = new File(datasetDirectory, i + ".csv");
		}
		/*
		 * Create data sets
		 */
		int [] six = new int[]{0,1,2,3,4,5};
		FileConcatenation.concat(filterFiles(files, six), new File(datasetDirectory,fileName(six)));
		
		int [] five = new int[]{0,1,2,3,4,6};
		FileConcatenation.concat(filterFiles(files, five), new File(datasetDirectory,fileName(five)));
		
		int [] four = new int[]{0,1,2,3,5,6};
		FileConcatenation.concat(filterFiles(files, four), new File(datasetDirectory,fileName(four)));
		
		int [] three = new int[]{0,1,2,4,5,6};
		FileConcatenation.concat(filterFiles(files, three), new File(datasetDirectory,fileName(three)));
		
		int [] two = new int[]{0,1,3,4,5,6};
		FileConcatenation.concat(filterFiles(files, two), new File(datasetDirectory,fileName(two)));
		
		int [] one = new int[]{0,2,3,4,5,6};
		FileConcatenation.concat(filterFiles(files, one), new File(datasetDirectory,fileName(one)));
		
		int [] zero = new int[]{1,2,3,4,5,6};
		FileConcatenation.concat(filterFiles(files, zero), new File(datasetDirectory,fileName(zero)));
		
		
		
		
	}
	private static String fileName(int [] indices){ 
		String name = "";
		for (int i=0; i<indices.length; i++){
			name += i;
			if (i < indices.length -1){
				name += ",";
			}
		}
		return name + ".csv";
	}
	private static File [] filterFiles(File []  files, int [] indices ){
		File [] newFiles = new File[indices.length];
		for (int i=0; i<indices.length; i++){
			newFiles[i] = files[indices[i]];
		}
		return newFiles;
	}
}
