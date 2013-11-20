package com.muro.cube;


public class EncodeStrategyCorner implements EncodeStrategy {

	/**
	 * Test doEncode
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		EncodeStrategy encodeStrategyCorner = new EncodeStrategyCorner();
		
		RubiksCube solved = new RubiksCube(new byte[]{0,3,6,9,12,15,18,21});
		RubiksCube cubeA = new RubiksCube(new byte[]{1,4,7,10,13,16,19,22});
		RubiksCube cubeB = new RubiksCube(new byte[]{2,5,8,11,14,17,20,23});
		RubiksCube cubeC = new RubiksCube(new byte[]{3,6,9,12,15,18,21,0});
		RubiksCube cubeD = new RubiksCube(new byte[]{4,7,10,13,16,19,22,1});
		
		System.out.println(encodeStrategyCorner.doEncode(solved));
		System.out.println(encodeStrategyCorner.doEncode(cubeA));
		System.out.println(encodeStrategyCorner.doEncode(cubeB));
		System.out.println(encodeStrategyCorner.doEncode(cubeC));
		System.out.println(encodeStrategyCorner.doEncode(cubeD));
		
		RubiksCube maxedCube = new RubiksCube(new byte[] {
				23, 20, 17, 14, 11, 8, 5, 2,
				23, 21, 19, 17, 15, 13,
				11, 9, 7, 5, 3, 1
			});
		
		System.out.println(encodeStrategyCorner.doEncode(maxedCube));

	}

	@Override
	public int doEncode(RubiksCube aCube) {

		int[] shiftFactors = {0, 0, 0, 0, 0, 0, 0, 0}; // The amount to subtract from the cubie encoding value, indexed by position.
		final int cornerCubes = 8; // Represents the index of the final corner cubie in the state array.
		int encoding = 0; // Holds the final integer value that represents this particular corner configuration. 

		// Calculate the encoding value using all eight of the the corner cubies.
		for (int i = 0; i < cornerCubes; i+=1) {
			
			// Calculate the position of this cubie. This allows us to shift all subsequent positions.
			int position = aCube.state[i] / 3;
			
			// Calculate the value of this encoding given the current shift factors.
			int shiftedValue = aCube.state[i] - shiftFactors[position];
			
			// Calculate the encoded value of this cubie.
			encoding += CORNER_WEIGHTS[i] * shiftedValue;
			
			// Shift all cubies by a factor of three. This effectively reduces the base of each encoding value.
			for (int j=position+1; j < shiftFactors.length; j+=1) {
				shiftFactors[j] += 3;
			}
		}

		return encoding;
	}

}
