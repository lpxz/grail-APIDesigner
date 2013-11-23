package ca.pfv.spmf.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.frequentpatterns.eclat_and_charm.AlgoCharm;
import ca.pfv.spmf.frequentpatterns.eclat_and_charm.AlgoCharmMFI;
import ca.pfv.spmf.frequentpatterns.eclat_and_charm.Context;
import ca.pfv.spmf.frequentpatterns.eclat_and_charm.Itemsets;

/**
 * Class to test the Charm-mfi algorithm.
 * @author Philippe Fournier-Viger (Copyright 2009)
 */
public class MainTestCharmMFI {

	public static void main(String [] arg){

		long startTime = System.currentTimeMillis();
		
		// Loading the binary context
		Context context = new Context();
		try {
			context.loadFile(fileToPath("contextZart.txt"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		context.printContext();
		
		// Applying the Charm algorithm
		AlgoCharm algo = new AlgoCharm(context, 100000);
		Itemsets frequents = algo.runAlgorithm(0.6, false);
		// if you change use "true" in the line above, CHARM will use
		// a triangular matrix  for counting support of itemsets of size 2.
		// For some datasets it should make the algorithm faster.
		
		long endTime = System.currentTimeMillis();
		
		// print the frequent itemsets found
		frequents.printItemsets(context.size());
		
		// print the frequent closed itemsets found
		algo.getClosedItemsets().printItemsets(context.size());

		System.out.println("total Time : " + (endTime - startTime) + "ms");
		
		// Run CHARM MFI
		AlgoCharmMFI algo2 = new AlgoCharmMFI();
		algo2.runAlgorithm(algo.getClosedItemsets());
		algo2.printStats(context.size());
		algo2.getItemsets().printItemsets(context.size());
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestCharmMFI.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
