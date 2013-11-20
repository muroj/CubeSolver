package com.muro.cube;


public class EncodeStrategyEdge implements EncodeStrategy {

	/**
	 * Test this strategies doEncode method.
	 * 
	 * @param args don't care
	 */
	public static void main(String[] args) {
		RubiksCube solvedCube = new RubiksCube();
		
		EncodeStrategyEdge encodeStrategyOne = new EncodeStrategyEdge(CubieGroup.EDGE_ONE);
		System.out.println(encodeStrategyOne.doEncode(solvedCube));
		
		EncodeStrategyEdge encodeStrategyTwo = new EncodeStrategyEdge(CubieGroup.EDGE_ONE);
		System.out.println(encodeStrategyTwo.doEncode(solvedCube));
		
		RubiksCube maxedCube = new RubiksCube(new byte[] {
									23, 20, 17, 14, 11, 8, 5, 2,
									23, 21, 19, 17, 15, 13,
									11, 9, 7, 5, 3, 1
								});
		
		System.out.println(encodeStrategyOne.doEncode(maxedCube));
		System.out.println(encodeStrategyTwo.doEncode(maxedCube));
	}
	
	/**
	 * Constructs a new EdgeEncodeStrategy configured to encode the specified group.
	 * Assumes that the value of group corresponds to the public constants EDGE_GROUP_ONE
	 * or EDGE_GROUP_TWO.
	 * 
	 * @param group an integer specifying the group of edge cubies to be encoded
	 */
	public EncodeStrategyEdge(CubieGroup group) {
		
		if (group == CubieGroup.EDGE_ONE) {
			groupStart = 8;
			groupEnd = 14;
		} else if (group == CubieGroup.EDGE_TWO) {
			groupStart = 14;
			groupEnd = 20;
		}
	}

	@Override
	public int doEncode(RubiksCube cube) {
		
		// The amount to subtract from the cubie encoding value, indexed by position.
		int[] shiftFactors = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		int encoding = 0; // Final encoding value. 
		
		// Calculate the encoding value using six of the the twelve edge cubies.
		for (int i = groupStart, k = 0; i < groupEnd; i+=1, k+=1) {

			// Calculate the position of this cubie. This allows us to shift all subsequent positions.
			int position = cube.state[i] / 2;

			encoding += EDGE_WEIGHTS[k] * (cube.state[i] - shiftFactors[position]);

			for (int j = (position + 1); j < shiftFactors.length; j += 1) {
				shiftFactors[j] += 2;
			}
		}
		
		return encoding;
	}
	
	/**
	 * The offset into the cube's state array where this group of edges begins.
	 */
	private int groupStart;
	
	/**
	 * The offset into the cube's state array where this group of edges ends.
	 */
	private int groupEnd;
}
