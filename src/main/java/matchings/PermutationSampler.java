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
	
	// permutation.getConnections(); 
	List<Integer> currentConnections = permutation.getConnections(); 
	System.out.println("Current stats is: \n \n \n" + currentConnections); 
	ArrayList<Integer> deepCurrentConnections = new ArrayList<Integer>(currentConnections); 
	
	double currentDensity = logDensity(); 
	System.out.println(currentDensity); 
	
	permutation.sampleUniform(rand); 
	List<Integer> nextConnections = permutation.getConnections(); 
	
	double newDensity = logDensity(); 
	System.out.println(newDensity); 
	
	double alpha = Math.min(1, Math.exp(newDensity)/Math.exp(currentDensity)); 
	double u = Math.random(); 
	if (u <= alpha) {
		for (int i = 0; i < nextConnections.size(); i ++) { 
			deepCurrentConnections.set(i, nextConnections.get(i)); 
		} 
		System.out.println("Next stats is: \n \n \n" + permutation.getConnections()); 
	} else {
		for (int i = 0; i < deepCurrentConnections.size(); i ++) { 
			deepCurrentConnections.set(i, deepCurrentConnections.get(i)); 
		} 
		System.out.println(permutation.getConnections()); 
	} 
	

  }
  
  private double logDensity() {
    double sum = 0.0;
    for (LogScaleFactor f : numericFactors)
      sum += f.logDensity();
    return sum;
  }
}
