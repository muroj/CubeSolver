package com.muro.cube;
/**
 * Provides a common interface for classes that want to implement a specific encoding strategy for a RubiksCube. The main motivation behind this class 
 * is preventing duplicate code when generating the heuristic tables. The process of generating the heuristic tables follows the same algorithm for each 
 * of the three groups of cubies -- depth-limited search to a depth-limit of eleven. The only process that differs is the method by which the encoding value 
 * is calculated. By specifying the encoding process in an interface we can use the same method to generate the heuristic tables while varying the encoding 
 * strategy depending on which tables need to be generated.
 *  
 * @author Joe M
 */

public interface EncodeStrategy {
	
	/**
	 * Calculates the encoding value of the specified cube.
	 * 
	 * Assumptions: 
	 * 		aCube contains an array of twenty bytes representing a cube state.
	 * 
	 * @param aCube an array of twenty bytes representing a particular cube state
	 * 
	 * @return an integer representing the encoded cube state.
	 */
	public int doEncode(RubiksCube aCube);
	
	public static final int[] CORNER_WEIGHTS = {3_674_160, 174_960, 9_720, 648, 54, 6, 1, 0};

	public static final int[] EDGE_WEIGHTS = {1_774_080, 80_640, 4_032, 224, 14, 1};

	enum CubieGroup {
		CORNER, EDGE_ONE, EDGE_TWO
	}
}
