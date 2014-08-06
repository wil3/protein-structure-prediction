package edu.stevens.cs.ssp.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.StructureException;
import org.biojava.bio.structure.align.util.AtomCache;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.biojava.bio.structure.io.PDBFileReader;
import org.biojava.bio.structure.io.StructureIOFile;

import edu.stevens.cpe.nih.DataParsingException;

/**
 * Given the proteins generate a data set
 * @author wil
 *
 */
public class DatasetBuilder {
		public static String DELIMITER = ",";

		private int lookBehind;
		private int lookAhead;
		private InputEncoding aminoAcidEncoding;
		private SecondaryStructureEncoding secondaryStructureEncoding;
		BufferedWriter out = null;
		private AtomCache cache;
		
	public DatasetBuilder(String pdbHome, int lookBehind, int lookAhead){
		this.lookBehind = lookBehind;
		this.lookAhead = lookAhead;
		this.cache = new AtomCache();
		cache.setPath(pdbHome);
		FileParsingParameters params = new FileParsingParameters();
		params.setParseSecStruc(true);
		cache.setFileParsingParams(params);
	}
	
	/**
	 * 
	 * @param pdbFile
	 * @return	Array list of pairs which include the amino acid character and the String of the SS.
	 * @throws StructureException 
	 * @throws IOException 
	 */
	private ArrayList<AminoAcidSecondaryStructurePair> getStructure(String name) throws IOException, StructureException{
		ArrayList<AminoAcidSecondaryStructurePair> pairs = new ArrayList<AminoAcidSecondaryStructurePair>();
		//StructureIOFile pdbreader = new PDBFileReader();
		//FileParsingParameters params = new FileParsingParameters();
		//params.setParseSecStruc(true);
		//pdbreader.setFileParsingParameters(params);
			
			Structure s = cache.getStructure(name);
			List<Chain> chains = s.getChains();
			
			for (Chain chain : chains){
				for (Group group : chain.getAtomGroups()){
					if ( group instanceof AminoAcid) {
						AminoAcid a = (AminoAcid)group;
						Collection<String> secondaries = a.getSecStruc().values();
						String secondary = null;
						
						if (secondaries.size() == 0){
							
						} else if (secondaries.size() == 1) {
							secondary = secondaries.iterator().next();
						} else {
							//warn
						}
					//	System.out.println(a.getPDBName()+ " " + secondary );
						pairs.add(new AminoAcidSecondaryStructurePair(a.getAminoType(),secondary));
						
					}
				}
			}
		
		
		return pairs;
	}
	
	private void print(ArrayList<AminoAcidSecondaryStructurePair> structure){
		
		for (AminoAcidSecondaryStructurePair pair : structure){
			System.out.print(pair.getAminoAcid() + " ");
		}
		System.out.println();
		for (AminoAcidSecondaryStructurePair pair : structure){
			String struct = pair.getStructure();
			String single = "C";
			if (struct == "HELIX"){
				single = "H";
			} else if (struct == "STRAND"){
				single = "E";
			}
			
			System.out.print(single + " ");
		}
		System.out.println();

	}
	
	private void slide(ArrayList<AminoAcidSecondaryStructurePair> structure) throws IOException{
		
		for (int center=0; center<structure.size(); center++){
			int left =  Math.max(center - lookBehind , 0);
			int right = Math.min(center + lookAhead +1, structure.size());
			Character [] aas = getWindowSequence(structure, left,center, right);
			writeSample(aas, structure.get(center).getStructure());
		}
	}
	private Character [] getWindowSequence(ArrayList<AminoAcidSecondaryStructurePair> structure, int left,int center, int right){
		Character [] seq = new Character [lookAhead + lookBehind + 1];
		
		final int windowCenter = lookAhead;
		//add the ones to the left
		int j =1;  
		for (int i=center - 1; i >= left; i--,j++){
			seq[windowCenter - j] = structure.get(i).getAminoAcid();
		}
		//add center
		seq[windowCenter] = structure.get(center).getAminoAcid();
		
		//add the ones to the right
		j = 1;
		for (int i= center + 1; i < right; i++,j++){
			seq[windowCenter + j] = structure.get(i).getAminoAcid();
		}
	//	System.out.println(Arrays.toString(seq));
		return seq;
	}
	
	
	
	private void writeSample(Character [] pdbNames, String ss) throws IOException{
		double [][] aminoAcidEncodings = aminoAcidEncoding.encode(pdbNames);
		//inputs first
		for (int i=0; i<aminoAcidEncodings.length; i++){
			double [] aminoAcidEncoding = aminoAcidEncodings[i];
			for (int j=0; j<aminoAcidEncoding.length; j++){
				out.write(aminoAcidEncoding[j] + DELIMITER);
			
			}
		}
		
		// write outputs
		double [] structureEncoding = secondaryStructureEncoding.encode(ss);
		for (int i=0; i<structureEncoding.length; i++){
			out.write(structureEncoding[i]+"");
			if (i < structureEncoding.length -1 ){
				out.write(DELIMITER);
			}
		}
		out.write("\r\n");

	}
	private void printHeader(){
		
	}
	
	public void build(File datasetDirectory, File partitionDescriptionFile) throws StructureException{
	
		 
	   	FileReader reader = null;
	    BufferedReader input = null;
	    try {
	   	  reader = new FileReader(partitionDescriptionFile);
	      input =  new BufferedReader(reader);
	      String line = null;
	      int fileName = 0;
	        while (( line = input.readLine()) != null){
	        	String [] names = line.split(DELIMITER);
	        	File file = new File(datasetDirectory, fileName + ".csv");
	        	build(file,names);
	        	fileName++;
	        }
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	      }
		    
	}
	private void build(File dataset, String[] pdbNames) throws StructureException{
		System.out.println(dataset.getAbsolutePath());
	FileWriter fstream = null;
		
		try {
			fstream = new FileWriter(dataset);
			out = new BufferedWriter(fstream);
			
			//Print Headers
			for (int i=0; i<pdbNames.length; i++){
				if (pdbNames[i].length() != 0){
					String name = pdbNames[i].replace("_", "."); //biojava wants chains separated by '.' not '_'
					System.out.println("\t" + name);

					//loop for all proteins
					//get sequence structure
					ArrayList<AminoAcidSecondaryStructurePair> structure = getStructure(name);
					//print(structure);
					//slide
					slide(structure);
				}
			}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}  finally {

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	public void setInputEncoder(InputEncoding aminoAcidEncoding){
		this.aminoAcidEncoding = aminoAcidEncoding;
	}
	public void setOutputEncoder(SecondaryStructureEncoding secondaryStructureEncoding){
		this.secondaryStructureEncoding = secondaryStructureEncoding;
	}
	
	class AminoAcidSecondaryStructurePair {
		private Character aminoAcid;
		private String structure;
		
		public AminoAcidSecondaryStructurePair(){
			
		}
		public AminoAcidSecondaryStructurePair(Character aminoAcid, String structure){
			this.aminoAcid = aminoAcid;
			this.structure = structure;
					
		}
		
		/**
		 * @return the pdbName
		 */
		public Character getAminoAcid() {
			return aminoAcid;
		}
		/**
		 * @param pdbName the pdbName to set
		 */
		public void setAminoAcid(Character aminoAcid) {
			this.aminoAcid = aminoAcid;
		}
		/**
		 * @return the structure
		 */
		public String getStructure() {
			return structure;
		}
		/**
		 * @param structure the structure to set
		 */
		public void setStructure(String structure) {
			this.structure = structure;
		}
	}
	//when should we do the encoding?
	public static void main(String [] args){
		
		//FIXME to correction location, change to cfg file or pass as parameter
		//root dir where all the protein data exists
		String pdbHome = "data";
		//The location where the csv file containing the proteins for fold 
		File partitionDescriptionFile = new File("proteins.csv");
		//Location containing all the 7-fold datasets
		File datasetDirectory = new File("datasets");
		
		int lookAhead = 9;
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
		
		/*
		try {
			ArrayList<AminoAcidSecondaryStructurePair> pairs = builder.getStructure("256b.A");
			builder.print(pairs);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (StructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
	}
}
