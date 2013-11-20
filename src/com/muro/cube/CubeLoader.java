package com.muro.cube;

import java.util.HashMap;
import java.util.Map;

public class CubeLoader {

	public static void main(String[] args) {
		byte[] encodedCubieArray;	

		String goal = "RRRRRRRRRGGGYYYBBBGGGYYYBBBGGGYYYBBBOOOOOOOOOWWWWWWWWW";
		
		encodedCubieArray = loadState(goal);

		for(int i = 0; i < encodedCubieArray.length; i++) {
			System.out.print(encodedCubieArray[i] + " ");
		}
	}
	
	/**
	 * Converts a cube from a face representation to a positional representation.
	 * 
	 * @param state a string representing a cube state
	 * @return an array of bytes representing the cube state
	 */
	public static byte[] loadState(String state) {
		
		byte[] cubeState = new byte[20];
		int[] cubieIndices = null;
		int orientation = 0;
		int encoding = 0;
		String cubie = null;
		
		for (int i=0; i < CUBIE_POSITIONS.length; i+=1) {
			
			cubieIndices = CUBIE_POSITIONS[i];
			
			// Determine whether this set of positions
			// correspond to a corner or an edge.
			if (cubieIndices.length == 3) {
				cubie = new String(new char[] {state.charAt(cubieIndices[0]), state.charAt(cubieIndices[1]), state.charAt(cubieIndices[2])});
				orientation = cubieOrientationMap.get(cubie);
				encoding = (byte) ((i * 3) + orientation);
			}
			
			if (cubieIndices.length == 2) {
				// Get the string corresponding to the current cubie positions.
				cubie = new String(new char[] {state.charAt(cubieIndices[0]), state.charAt(cubieIndices[1])});
				// Look up the orientation value for this particular permutation of the cubie.
				orientation = cubieOrientationMap.get(cubie);
				// Calculate the encoding value.
				encoding = (byte) (((i - 8)* 2) + orientation);
			}
			
			// Put the encoding in the correct index in the state array.
			int index = cubieStateIndexMap.get(cubie);
			// Store the encoding value.
			cubeState[index] = (byte)encoding;
		}
		
		return cubeState;
	}
	
	static int[][] CUBIE_POSITIONS = {
			// corner cubies 0-3
			{12, 11, 6}, {14, 15, 8}, {32, 33, 38}, {30, 29, 36},
			// corner cubies 4-7
			{45, 27, 42},{47, 35, 44}, {53, 17, 2}, {51, 9, 0},
			// edge cubies 8-13
			{13, 7}, {16, 5}, {52, 1}, {10, 3}, {21, 20}, {28, 39},
			// edge cubies 14-29
			{48, 18}, {46, 43}, {50, 26}, {34, 41}, {31, 37}, {23, 24}
	};
	
	static Map<String, Integer> cubieOrientationMap = new HashMap<String, Integer>();

	static {
		cubieOrientationMap.put("YGR", 0);
		cubieOrientationMap.put("YRG", 0);
		cubieOrientationMap.put("GRY", 1);
		cubieOrientationMap.put("GYR", 1);
		cubieOrientationMap.put("RYG", 2);
		cubieOrientationMap.put("RGY", 2);
		cubieOrientationMap.put("YRB", 0);
		cubieOrientationMap.put("YBR", 0);
		cubieOrientationMap.put("RBY", 1);
		cubieOrientationMap.put("RYB", 1);
		cubieOrientationMap.put("BYR", 2);
		cubieOrientationMap.put("BRY", 2);
		cubieOrientationMap.put("YBO", 0);
		cubieOrientationMap.put("YOB", 0);
		cubieOrientationMap.put("BOY", 1);
		cubieOrientationMap.put("BYO", 1);
		cubieOrientationMap.put("OYB", 2);
		cubieOrientationMap.put("OBY", 2);
		cubieOrientationMap.put("YOG", 0);
		cubieOrientationMap.put("YGO", 0);
		cubieOrientationMap.put("OGY", 1);
		cubieOrientationMap.put("OYG", 1);
		cubieOrientationMap.put("GYO", 2);
		cubieOrientationMap.put("GOY", 2);
		cubieOrientationMap.put("WGO", 0);
		cubieOrientationMap.put("WOG", 0);
		cubieOrientationMap.put("GOW", 1);
		cubieOrientationMap.put("GWO", 1);
		cubieOrientationMap.put("OWG", 2);
		cubieOrientationMap.put("OGW", 2);
		cubieOrientationMap.put("WBO", 0);
		cubieOrientationMap.put("WOB", 0);
		cubieOrientationMap.put("OBW", 1);
		cubieOrientationMap.put("OWB", 1);
		cubieOrientationMap.put("BWO", 2);
		cubieOrientationMap.put("BOW", 2);
		cubieOrientationMap.put("WBR", 0);
		cubieOrientationMap.put("WRB", 0);
		cubieOrientationMap.put("BRW", 1);
		cubieOrientationMap.put("BWR", 1);
		cubieOrientationMap.put("RWB", 2);
		cubieOrientationMap.put("RBW", 2);
		cubieOrientationMap.put("WGR", 0);
		cubieOrientationMap.put("WRG", 0);
		cubieOrientationMap.put("RGW", 1);
		cubieOrientationMap.put("RWG", 1);
		cubieOrientationMap.put("GWR", 2);
		cubieOrientationMap.put("GRW", 2);
		cubieOrientationMap.put("YR", 0);
		cubieOrientationMap.put("RY", 1);
		cubieOrientationMap.put("BR", 0);
		cubieOrientationMap.put("RB", 1);
		cubieOrientationMap.put("WR", 0);
		cubieOrientationMap.put("RW", 1);
		cubieOrientationMap.put("GR", 0);
		cubieOrientationMap.put("RG", 1);
		cubieOrientationMap.put("YG", 0);
		cubieOrientationMap.put("GY", 1);
		cubieOrientationMap.put("GO", 0);
		cubieOrientationMap.put("OG", 1);
		cubieOrientationMap.put("WG", 0);
		cubieOrientationMap.put("GW", 1);
		cubieOrientationMap.put("WO", 0);
		cubieOrientationMap.put("OW", 1);
		cubieOrientationMap.put("WB", 0);
		cubieOrientationMap.put("BW", 1);
		cubieOrientationMap.put("BO", 0);
		cubieOrientationMap.put("OB", 1);
		cubieOrientationMap.put("YO", 0);
		cubieOrientationMap.put("OY", 1);
		cubieOrientationMap.put("YB", 0);
		cubieOrientationMap.put("BY", 1);

	}
	
	static Map<String, Integer> cubieStateIndexMap = new HashMap<String, Integer>();
	
	static {

		cubieStateIndexMap.put("YGR", 0);
		cubieStateIndexMap.put("YRG", 0);
		cubieStateIndexMap.put("GRY", 0);
		cubieStateIndexMap.put("GYR", 0);
		cubieStateIndexMap.put("RYG", 0);
		cubieStateIndexMap.put("RGY", 0);
		cubieStateIndexMap.put("YRB", 1);
		cubieStateIndexMap.put("YBR", 1);
		cubieStateIndexMap.put("RBY", 1);
		cubieStateIndexMap.put("RYB", 1);
		cubieStateIndexMap.put("BYR", 1);
		cubieStateIndexMap.put("BRY", 1);
		cubieStateIndexMap.put("YBO", 2);
		cubieStateIndexMap.put("YOB", 2);
		cubieStateIndexMap.put("BOY", 2);
		cubieStateIndexMap.put("BYO", 2);
		cubieStateIndexMap.put("OYB", 2);
		cubieStateIndexMap.put("OBY", 2);
		cubieStateIndexMap.put("YOG", 3);
		cubieStateIndexMap.put("YGO", 3);
		cubieStateIndexMap.put("OGY", 3);
		cubieStateIndexMap.put("OYG", 3);
		cubieStateIndexMap.put("GYO", 3);
		cubieStateIndexMap.put("GOY", 3);
		cubieStateIndexMap.put("WGO", 4);
		cubieStateIndexMap.put("WOG", 4);
		cubieStateIndexMap.put("GOW", 4);
		cubieStateIndexMap.put("GWO", 4);
		cubieStateIndexMap.put("OWG", 4);
		cubieStateIndexMap.put("OGW", 4);
		cubieStateIndexMap.put("WBO", 5);
		cubieStateIndexMap.put("WOB", 5);
		cubieStateIndexMap.put("OBW", 5);
		cubieStateIndexMap.put("OWB", 5);
		cubieStateIndexMap.put("BWO", 5);
		cubieStateIndexMap.put("BOW", 5);
		cubieStateIndexMap.put("WBR", 6);
		cubieStateIndexMap.put("WRB", 6);
		cubieStateIndexMap.put("BRW", 6);
		cubieStateIndexMap.put("BWR", 6);
		cubieStateIndexMap.put("RWB", 6);
		cubieStateIndexMap.put("RBW", 6);
		cubieStateIndexMap.put("WGR", 7);
		cubieStateIndexMap.put("WRG", 7);
		cubieStateIndexMap.put("RGW", 7);
		cubieStateIndexMap.put("RWG", 7);
		cubieStateIndexMap.put("GWR", 7);
		cubieStateIndexMap.put("GRW", 7);
		cubieStateIndexMap.put("YR", 8);
		cubieStateIndexMap.put("RY", 8);
		cubieStateIndexMap.put("BR", 9);
		cubieStateIndexMap.put("RB", 9);
		cubieStateIndexMap.put("WR", 10);
		cubieStateIndexMap.put("RW", 10);
		cubieStateIndexMap.put("GR", 11);
		cubieStateIndexMap.put("RG", 11);
		cubieStateIndexMap.put("YG", 12);
		cubieStateIndexMap.put("GY", 12);
		cubieStateIndexMap.put("GO", 13);
		cubieStateIndexMap.put("OG", 13);
		cubieStateIndexMap.put("WG", 14);
		cubieStateIndexMap.put("GW", 14);
		cubieStateIndexMap.put("WO", 15);
		cubieStateIndexMap.put("OW", 15);
		cubieStateIndexMap.put("WB", 16);
		cubieStateIndexMap.put("BW", 16);
		cubieStateIndexMap.put("BO", 17);
		cubieStateIndexMap.put("OB", 17);
		cubieStateIndexMap.put("YO", 18);
		cubieStateIndexMap.put("OY", 18);
		cubieStateIndexMap.put("YB", 19);
		cubieStateIndexMap.put("BY", 19);
	}

}