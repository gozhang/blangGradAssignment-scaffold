package matchings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import bayonet.math.*; 
import bayonet.distributions.Multinomial;
import bayonet.distributions.Random;
import blang.core.LogScaleFactor;
import blang.distributions.Generators;
import blang.mcmc.ConnectedFactor;
import blang.mcmc.SampledVariable;
import blang.mcmc.Sampler;

import static java.util.Collections.shuffle;


/**
 * Each time a Permutation is encountered in a Blang model, 
 * this sampler will be instantiated. 
 */
public class BipartiteMatchingSampler implements Sampler {
  /**
   * This field will be populated automatically with the 
   * permutation being sampled. 
   */
  @SampledVariable BipartiteMatching matching;
  /**
   * This will contain all the elements of the prior or likelihood 
   * (collectively, factors), that depend on the permutation being 
   * resampled. 
   */
  @ConnectedFactor List<LogScaleFactor> numericFactors;

  @Override
  public void execute(Random rand) {
    // Fill this. 
	// Similar to the filled code in PermutationSampler, but now sample from non-perfect matching 
	// Refer to the file for the data structure: BipartiteMatching.xtend 
	  
	  List<Integer> CurrentState = matching.getConnections(); 
//	  System.out.println("The current state is: " + CurrentState); 
	    // Deep copy the current state 
		ArrayList<Integer> CurrentStateDeepCopy = new ArrayList<Integer>(CurrentState); 
		
		// get the log density of the current state 
		double CurrentDensity = logDensity(); 
//		System.out.println(CurrentDensity); 
				
//		// Perform "independence" Uniform sampling, which is available in BipartiteMatching.xtend 
//		matching.sampleUniform(rand); 
//		List<Integer> NextConnections = matching.getConnections(); 
		
//		// Create a list that consists of 0, 1, ..., size and the same number of -1's 
//		ArrayList<Integer> fullList = new ArrayList<Integer>();
//		for (int i=0; i < matching.getConnections().size(); i++) {
//			fullList.add(i); 
//			fullList.add(-1); 
//		}
//
//		
//		// Uniformly permute the fullList 
//		java.util.Collections.shuffle(fullList); 
//		System.out.println("The permuted list is: " + fullList); 
//		
//		// Deep copy the fullList 
////		ArrayList<Integer> fullListDeepCopy = new ArrayList<Integer>(fullList); 
//		
//		ArrayList<Integer> ProposedState = new ArrayList<Integer>(); 
//		
////		// Sample the proposed state uniformly from the fullList 
////		for (int j=0; j < matching.getConnections().size(); j++) {
////			int Index = rand.nextInt(2*(matching.getConnections().size()) - j);
////			ProposedState.add(fullListDeepCopy.get(Index));
////			fullListDeepCopy.remove(Index); 
//////			System.out.println(fullList_deepcopy); 
////		}
//		
//		// Count the number of -1's in the current state and the proposed state 
//		int countProposedNegOne = 0;
//		int countCurrentNegOne = 0;
//		for (int i=0; i < matching.getConnections().size(); i++) {
//			ProposedState.add(fullList.get(i)); // Take the first few elements in the permuted fullList, which is of length = size 
//			if (fullList.get(i) == -1) {countProposedNegOne++;} 
//			if (CurrentStateDeepCopy.get(i) == -1) {countCurrentNegOne++;}
//		} 
//		
////		System.out.println(ProposedState); 
//		// Temporarily set the next state to be the proposed state 
//		for (int i=0; i < matching.getConnections().size(); i++) {
//			matching.getConnections().set(i, ProposedState.get(i));
//		} 
		
		// Randomly select one vertex from the current state 
		int Index_Position = rand.nextInt(matching.getConnections().size()); 
		
		ArrayList<Integer> PossibleVertices = new ArrayList<Integer>(); 
				
		for (int i=0; i < matching.getConnections().size(); i++) {
			if (!CurrentStateDeepCopy.contains(i)) {PossibleVertices.add(i);} 
		} 
		
		// Always add itself in the list of all possible vertices, so that it has positive probability to propose to stay 
        PossibleVertices.add(CurrentStateDeepCopy.get(Index_Position)); 
        
        // Always add -1 in the list if it is not there yet 
        if (!PossibleVertices.contains(-1)) {PossibleVertices.add(-1);} 
		
		// Randomly select one from the list of all possible vertices 
		int Index_Vertex = rand.nextInt(PossibleVertices.size()); 

		// Make the swap
		matching.getConnections().set(Index_Position, PossibleVertices.get(Index_Vertex)); 
		
		double NextDensity = logDensity(); 
//		System.out.println(NextDensity); 
		
		// The (scaled) proposal probabilities q (x) and q(x') 
//		double ProposalDensityUp = 1.0/factorial(matching.getConnections().size()-countCurrentNegOne);
//		double ProposalDensityDown = 1.0/factorial(matching.getConnections().size()-countProposedNegOne); 
		
		// Ratio in the MH Algorithm 
//		double q_ratio = ProposalDensityUp/ProposalDensityDown;
		double pi_ratio = Math.exp(NextDensity)/Math.exp(CurrentDensity);
//		double ratio = pi_ratio*q_ratio; 
		
		// Define the acceptance probability 
		double AcceptProb = Math.min(1.0, pi_ratio); 
//		System.out.println("Acceptance probability is: " + AcceptProb); 
		
		// Generate a Uniform [0, 1] random variable 
		double u = Math.random(); 
//		System.out.println("The generated uniform random variable is: " + u); 
		
		
//		boolean bern = Generators.bernoulli(rand, Math.min(1.0, accept_prob)); 
		
		
//		double NextDensity = logDensity(); 
////		// System.out.println(NextDensity); 
//		
		
		if (AcceptProb < u) { // Rejection step 
			for (int i = 0; i < CurrentStateDeepCopy.size(); i ++) { 
				matching.getConnections().set(i, CurrentStateDeepCopy.get(i)); 
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
  
  // Define the factorial function 
  private static double factorial(int N) {
	  double multi = 1;
	  for (int i = 1; i <= N; i++) {
		  multi = multi*i;
	  }
	  return multi;
  }
}
