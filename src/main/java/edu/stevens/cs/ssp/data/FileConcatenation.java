package edu.stevens.cs.ssp.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileConcatenation {

	public static void concat(File [] files, File target){
		FileWriter fstream = null;
		BufferedWriter out = null;

		try {
			fstream = new FileWriter(target);
			out = new BufferedWriter(fstream);
			
			for (int i=0; i<files.length; i++){
				write(out, files[i]);
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
	/**
	 * Read file and write to target
	 */
	private static void write(BufferedWriter out, File file){
		 
	   	FileReader reader = null;
	    BufferedReader input = null;
	    try {
	   	  reader = new FileReader(file);
	      input =  new BufferedReader(reader);
	      String line = null;
	        while (( line = input.readLine()) != null){
	        	out.write(line);
	        	out.write("\r\n");
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
}
