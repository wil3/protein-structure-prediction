package edu.stevens.cs.ssp;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.TrainerUtility;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;

public class NeuralNetwork {

	public NeuralNetwork(){
		
	}
	public BasicNetwork build(int [] structure){
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,false,structure[0]));

		for (int i=1; i<structure.length; i++){
			network.addLayer(new BasicLayer(new ActivationTANH(),false, structure[i]));
		}
		network.getStructure().finalizeStructure();
		network.reset();
		new ConsistentRandomizer(-2,2,500).randomize(network);

		return network;
	}
	
	
}
