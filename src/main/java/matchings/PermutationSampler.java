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
	
	List<Integer> CurrentConnections = permutation.getConnections(); 
	ArrayList<Integer> deepcopyCurrentConnections = new ArrayList<Integer>(CurrentConnections); 
	
	final double CurrentDensity = logDensity(); 
	// System.out.println(CurrentDensity); 
	
	// Perform "independence" Uniform sampling, which is available in Permutation.xtend 
	permutation.sampleUniform(rand); 
	List<Integer> NextConnections = permutation.getConnections(); 
	
	final double NextDensity = logDensity(); 
	// System.out.println(NextDensity); 
	
	final double probab = Math.min(1.0, Math.exp(NextDensity)/Math.exp(CurrentDensity)); 
	boolean bern = Generators.bernoulli(rand, probab); 
	if (!bern) {
		for (int i = 0; i < deepcopyCurrentConnections.size(); i ++) { 
			permutation.getConnections().set(i, deepcopyCurrentConnections.get(i)); 
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
