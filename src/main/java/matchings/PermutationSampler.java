package matchings;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList; 

import bayonet.distributions.Random;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;
import briefj.collections.UnorderedPair;

/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class PermutationSampler implements Sampler {
  /**
   * This field will be populated automatically with the 
   * permutation being sampled. 
   */
  @SampledVariable Permutation permutation;
  /**
   * This will contain all the elements of the prior or likelihood 
   * (collectively, factors), that depend on the permutation being 
   * resampled. 
   */
  @ConnectedFactor List<LogScaleFactor> numericFactors;

  @Override
  public void execute(Random rand) {
    // Fill this. 
	// Refer to the file: Permutation.xtend 
	
	// Current State 
	List<Integer> CurrentState = permutation.getConnections(); 
	ArrayList<Integer> deepcopyCurrentState = new ArrayList<Integer>(CurrentState); 
//	System.out.println("Current State is: " + CurrentState); 
	
	// Find the log density of the current state 
	final double CurrentDensity = logDensity(); 
	// System.out.println(CurrentDensity); 
	
//	// Perform "independence" Uniform sampling, which is available in Permutation.xtend 
//	permutation.sampleUniform(rand); 
//	List<Integer> NextConnections = permutation.getConnections(); 
	
	// Make the swap of the randomly chosen 2 vertices in the current state 
	int index1 = rand.nextInt(permutation.getConnections().size()); 
	int index2 = rand.nextInt(permutation.getConnections().size()); // could be same to the previous one 
//	System.out.println(index1); 
//	System.out.println(index2); 
	Collections.swap(permutation.getConnections(), index1, index2); 
	
//	// Proposed State 
//	List<Integer> ProposedState = permutation.getConnections(); 
////	System.out.println("Next State is: " + ProposedState); 
	
	// Find the log density of the proposed state 
	final double NextDensity = logDensity(); 
	// System.out.println(NextDensity); 
	
	final double Accept_Prob = Math.min(1.0, Math.exp(NextDensity)/Math.exp(CurrentDensity)); 
	
//	boolean bern = Generators.bernoulli(rand, Accept_Prob); 
//	if (!bern) {
//		for (int i = 0; i < deepcopyCurrentState.size(); i ++) { 
//			permutation.getConnections().set(i, deepcopyCurrentState.get(i)); 
//		} 
//		// System.out.println("Next state is: \n \n \n" + permutation.getConnections()); 
//	} 
	
	// Generate a Uniform [0, 1] random variable 
	double u = Math.random();  
	if (u>Accept_Prob) {
		for (int i = 0; i < deepcopyCurrentState.size(); i ++) { 
			permutation.getConnections().set(i, deepcopyCurrentState.get(i)); // Rejection step: recover to the previous state 
		} 
		// System.out.println("Next state is: \n \n \n" + permutation.getConnections()); 
	} 

  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
