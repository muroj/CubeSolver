package com.muro.cube;

import java.util.Arrays;

import com.muro.cube.EncodeStrategy.CubieGroup;

/**
 * RubiksCube is a positional representation of a RubiksCube.
 * 
 * @author Joe M
 */

public class RubiksCube {

	public static void main(String[] args) {
		RubiksCube cube = new RubiksCube();

		if (cube.isSolved()) {
			System.out.println("Final state:\n" + cube);
		}


		for (int i = 0; i < 6; i+=1) {
			cube.rotateCCW(Face.RIGHT);
			cube.rotateCCW(Face.BOTTOM);
			cube.rotateCW(Face.RIGHT);
			cube.rotateCW(Face.TOP);

			System.out.println(i + " turn " + cube);
		}

		if (cube.isSolved()) {
			System.out.println("Final state:\n" + cube);
		}

	}

	/**
	 * Construct a new RubiksCube initialized to the solved state.
	 */
	public RubiksCube() {
		this.state = new byte[20];
		setToSolvedState();
	}

	/**
	 * Construct a new RubiksCube initialized to the specified state.
	 * 
	 * @param state an array of 20 integers representing a valid cube state
	 */
	public RubiksCube(byte[] state) {
		this.state = Arrays.copyOf(state, state.length);
	}

	/**
	 * Creates a new cube and initializes it to the state of this cube.
	 * 
	 * @return a new copy of the specified cube
	 */
	public RubiksCube copyCube() {
		return new RubiksCube(Arrays.copyOf(state, state.length));
	}

	/**
	 * Creates a new cube and initializes it to the solved state.
	 * 
	 * @return a new cube in the solved state
	 */
	public RubiksCube createSolvedCube() {
		return new RubiksCube();
	}

	/**
	 * Performs the rotation specified by type on the specified face.
	 * 
	 * @param rotation
	 * @param face an enum value specifying the face to be rotated
	 * @return this cube
	 */
	public RubiksCube performRotation(Rotation rotation, Face face) {

		switch (rotation) {

		case CLOCKWISE:
			return rotateCW(face);
		case COUNTER_CLOCKWISE:
			return rotateCCW(face);
		case HALF_TURN:
			return rotate180(face);
		default:
			System.out.println("Invalid rotation specified.");
			break;
		}

		return null;
	}

	/**
	 * Performs a clockwise rotation on the specified face.
	 * 
	 * @param aFace the face to rotate
	 * @return the modified cube
	 */
	public RubiksCube rotateCW(Face aFace) {
		return rotate(aFace, Rotation.CLOCKWISE);
	}

	/**
	 * Performs a counter-clockwise rotation on the specified face.
	 * 
	 * @param aFace the face to rotate
	 * @return the modified cube
	 */
	public RubiksCube rotateCCW(Face aFace) {
		return rotate(aFace, Rotation.COUNTER_CLOCKWISE);
	}

	/**
	 * Performs a double rotation on the specified face. This
	 * results in a 180-degree rotation of the specified face.
	 * 
	 * @param aFace the face to rotate
	 * @return the modified cube
	 */
	public RubiksCube rotate180(Face aFace) {
		return rotate(aFace, Rotation.CLOCKWISE).rotate(aFace, Rotation.CLOCKWISE);
	}

	/**
	 * Returns a copy of this RubiksCube's state..
	 * 
	 * @return an array of bytes
	 */
	public byte[] getState() {
		return Arrays.copyOf(this.state, this.state.length);
	}

	/**
	 * Indicates whether this cube is in the solved state.
	 * 
	 * @return true if the cube is in the solved state, false otherwise.
	 */
	public boolean isSolved() {

		// Check the corner cubies.
		for(int i=0, j=0; i < 8; i+=1, j+=3) {
			if (this.state[i] != j)
				return false;
		}

		// Check the edge cubies.
		for(byte i=8, j=0; i < state.length; i+=1, j+=2) {
			if (this.state[i] != j)
				return false;
		}

		return true;
	}

	/**
	 * Indicates whether the specified cube is in the solved state.
	 * 
	 * @param aCube an array of bytes representing a cube state.
	 * 
	 * @return true if the cube is in the solved state, false otherwise.
	 */
	public static boolean isSolved(byte[] aCube) {
		// Check the corner cubies.
		for(int i=0, j=0; i < 8; i+=1, j+=3) {
			if (aCube[i] != j)
				return false;
		}

		// Check the edge cubies.
		for(byte i=8, j=0; i < aCube.length; i+=1, j+=2) {
			if (aCube[i] != j)
				return false;
		}

		return true;
	}

	/**
	 * Indicates whether the specified cubes are equal. Two cubes are equal
	 * if each matching cubie is in the same position and orientation.
	 * 
	 * @param other 
	 * 
	 * @return true if the cubes are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object other) {

		if (other == null || !(other instanceof RubiksCube)) {
			return false;
		}

		RubiksCube toTest = (RubiksCube)other;

		for (int i=0; i < state.length; i+=1) {
			if (this.state[i] != toTest.state[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Initializes this cube to the solved state.
	 * 
	 */
	public void setToSolvedState() {

		// Initialize the corner cubies.
		for(byte i=0, j=0; i < 8; i+=1, j+=3) {
			this.state[i] = j;
		}
		// Initialize the edge cubies.
		for(byte i=8, j=0; i < state.length; i+=1, j+=2) {
			this.state[i] = j;
		}
	}
	
	/**
	 * Calculates and returns the encoding of this cube's corner cubies.
	 * 
	 * @return an integer
	 */
	public int getCornerEncoding() {
		return cornerEncoder.doEncode(this);
	}
	
	/**
	 * Calculates and returns the encoding of this cube's edge group one cubies.
	 * 
	 * @return an integer
	 */
	public int getEdgeOneEncoding() {
		return edgeOneEncoder.doEncode(this);
	}
	
	/**
	 * Calculates and returns the encoding of this cube's edge group two cubies.
	 * 
	 * @return an integer
	 */
	public int getEdgeTwoEncoding() {
		return edgeTwoEncoder.doEncode(this);
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder cube = new StringBuilder();

		cube.append("{ ");

		for(int x : state) {
			cube.append(x + " ");
		}

		cube.append("}");

		return cube.toString();
	}

	/**
	 * Performs the specified rotation on the specified face of the cube.
	 * 
	 * @param aFace a valid Rubik's Cube face
	 * @param aDirection the direction (CW, CCW) to rotate the cube
	 * @param aCube an encoded representation of a Rubik's cube
	 * @return the cube state after performing the specified rotation
	 * 
	 */
	private RubiksCube rotate(Face aFace, Rotation rotation) {

		/**
		 * Locate the cubies that make up the specified face.
		 */
		int tlCubieIdx = findCubieAtPosition(aFace.top_left);
		int trCubieIdx = findCubieAtPosition(aFace.top_right);
		int brCubieIdx = findCubieAtPosition(aFace.bottom_right);
		int blCubieIdx = findCubieAtPosition(aFace.bottom_left);
		int topCubieIdx = findCubieAtPosition(aFace.top);
		int rightCubieIdx = findCubieAtPosition(aFace.right);
		int bottomCubieIdx = findCubieAtPosition(aFace.bottom);
		int leftCubieIdx = findCubieAtPosition(aFace.left);

		switch (aFace) {

		case FRONT:
			return doRotate(rotation, Face.FRONT, 0, tlCubieIdx, trCubieIdx, brCubieIdx, blCubieIdx,
					topCubieIdx, rightCubieIdx, bottomCubieIdx, leftCubieIdx);
		case REAR:
			return doRotate(rotation, Face.REAR, 0, tlCubieIdx, trCubieIdx, brCubieIdx, blCubieIdx,
					topCubieIdx, rightCubieIdx, bottomCubieIdx, leftCubieIdx);
		case LEFT:
			return doRotate(rotation, Face.LEFT, 1, tlCubieIdx, trCubieIdx, brCubieIdx, blCubieIdx,
					topCubieIdx, rightCubieIdx, bottomCubieIdx, leftCubieIdx);
		case RIGHT:
			return doRotate(rotation, Face.RIGHT, 1, tlCubieIdx, trCubieIdx, brCubieIdx, blCubieIdx,
					topCubieIdx, rightCubieIdx, bottomCubieIdx, leftCubieIdx);
		case TOP:
			return doRotate(rotation, Face.TOP, 2, tlCubieIdx, trCubieIdx, brCubieIdx, blCubieIdx,
					topCubieIdx, rightCubieIdx, bottomCubieIdx, leftCubieIdx);
		case BOTTOM:
			return doRotate(rotation, Face.BOTTOM, 2, tlCubieIdx, trCubieIdx, brCubieIdx, blCubieIdx,
					topCubieIdx, rightCubieIdx, bottomCubieIdx, leftCubieIdx);
		default:
			return null;
		}
	}

	/**
	 * Locates the cubie at the specified position.
	 * 
	 * @param thePos an integer representing a cubie position
	 * @param aCube 
	 * @return the index to the cubie at the specified position
	 */
	private int findCubieAtPosition(int thePos) {

		int cubieIndex = -1; // this will hold the index of the cubie found to be at thePos
		int start = -1; // the index from which to start searching the cubie enum
		int end = -1; // the index to search until
		int divisor = -1; // the value used to calculate the position of the cubie
		int normalizer = -1; // the value used to "normalize" the array indices to 0.

		Cubie[] cubies = Cubie.values();

		if (thePos > 7) { // we are looking for an edge cubie
			start = 8;
			end = cubies.length;
			divisor = 2;
			normalizer = 8;

		} else { // we are looking for a corner cubie
			start = 0;
			end = 8;
			divisor = 3;
			normalizer = 0;
		}

		// loop over each cubie until the cubie at position "thePos" is found.
		for (int i=start; i < end; i+=1) {
			// Get the integer value associated with this cubie enum
			cubieIndex = cubies[i].ordinal();

			// Calculate the position of the cubie.
			int cubiePosition = state[cubieIndex] / divisor + normalizer;

			if (cubiePosition == thePos) { // We found the cubie at the specified position...
				break; // ...break out of the loop.
			}
		}

		// Return the index to the cubie.
		return cubieIndex;
	}

	/**
	 * Performs the specified rotation on this cube. The position indices are used to index this cubes
	 * state array.
	 * 
	 * @param aDirection an integer representing the rotation (CW, CCW, 180)
	 * @param aFace an integer representing the face (Front, Back, Top, Bottom, Left, Right)
	 * @param offset an integer corresponding 
	 * @param tlCubieIdx an index to the top-left cubie
	 * @param trCubieIdx an index to the top-right cubie 
	 * @param brCubieIdx an index to the bottom-right cubie
	 * @param blCubieIdx an index to the bottom-left cubie
	 * @param topCubieIdx an index to the top cubie
	 * @param rightCubieIdx an index to the right cubie
	 * @param bottomCubieIdx an index to the bottom cubie
	 * @param leftCubieIdx an index to the left cubie
	 * @return this RubiksCube
	 */
	private RubiksCube doRotate(Rotation aDirection, Face aFace, int offset,
			int tlCubieIdx, int trCubieIdx, int brCubieIdx, int blCubieIdx,
			int topCubieIdx, int rightCubieIdx, int bottomCubieIdx, int leftCubieIdx) {

		// cube's are immutable so we need to return a new cube state.
		byte[] toReturn = new byte[20];
		// initialize the return array to this cube's state.
		for (int i = 0; i < this.state.length; i+=1) {
			toReturn[i] = this.state[i];
		}
		
		// The offsets are used to switch the orientation of the corner cubies.
		// The values of the offsets are determined by the face that is being rotated.
		int offsetOne = 0; 
		int offsetTwo = 0;

		if (offset == 0) { // Front/Rear faces
			offsetOne= 0; 
			offsetTwo = 0;
		} else if (offset == 1) { // Left/Right faces
			offsetOne= 1;
			offsetTwo = 2;
		} else if (offset == 2) { // Top/Bottom faces
			offsetOne = 2;
			offsetTwo = 1;
		}

		// Calculate the orientations of the corner cubies
		int tlOrien = state[tlCubieIdx] % 3; 
		int trOrien = state[trCubieIdx] % 3;
		int brOrien = state[brCubieIdx] % 3;
		int blOrien = state[blCubieIdx] % 3;

		// Calculate the orientations of the edge cubies.
		int topOrien = state[topCubieIdx] % 2; 
		int rightOrien = state[rightCubieIdx] % 2;
		int bottomOrien = state[bottomCubieIdx] % 2;
		int leftOrien = state[leftCubieIdx] % 2;

		// If this is a left or right face than we have to switch the orientation of the edge cubies.
		if (aFace == Face.LEFT || aFace == Face.RIGHT) {
			topOrien = (topOrien + 1) % 2;
			bottomOrien = (bottomOrien + 1) % 2;
			leftOrien = (leftOrien + 1) % 2;
			rightOrien = (rightOrien + 1) % 2;
		}

		if (aDirection == Rotation.CLOCKWISE) {

			// Rotate the corner cubies
			toReturn[tlCubieIdx] = (byte) ((aFace.top_right * 3) + ((tlOrien + offsetOne) % 3));
			toReturn[trCubieIdx] = (byte) ((aFace.bottom_right * 3) + ((trOrien + offsetTwo) % 3));
			toReturn[brCubieIdx] = (byte) ((aFace.bottom_left * 3) + ((brOrien + offsetOne) % 3));
			toReturn[blCubieIdx] = (byte) ((aFace.top_left * 3) + ((blOrien + offsetTwo) % 3));

			// Rotate the edge cubies
			toReturn[topCubieIdx] = (byte) ((aFace.right -8) * 2 + topOrien);
			toReturn[rightCubieIdx] = (byte) ((aFace.bottom-8) * 2 + rightOrien);
			toReturn[bottomCubieIdx] = (byte) ((aFace.left-8) * 2 + bottomOrien);
			toReturn[leftCubieIdx] = (byte) ((aFace.top-8) * 2 + leftOrien);

		} else if (aDirection == Rotation.COUNTER_CLOCKWISE) {

			// Rotate the corner cubies
			toReturn[tlCubieIdx] = (byte) ((aFace.bottom_left * 3) + ((tlOrien + offsetOne) % 3));
			toReturn[trCubieIdx] = (byte) ((aFace.top_left * 3) + ((trOrien + offsetTwo) % 3));
			toReturn[brCubieIdx] = (byte) ((aFace.top_right * 3) + ((brOrien + offsetOne) % 3));
			toReturn[blCubieIdx] = (byte) ((aFace.bottom_right * 3) + ((blOrien + offsetTwo) % 3));

			// Rotate the edge cubies
			toReturn[topCubieIdx] = (byte) ((aFace.left-8) * 2 + topOrien);
			toReturn[rightCubieIdx] = (byte) ((aFace.top-8) * 2 + rightOrien);
			toReturn[bottomCubieIdx] = (byte) ((aFace.right-8) * 2 + bottomOrien);
			toReturn[leftCubieIdx] = (byte) ((aFace.bottom-8)  * 2 + leftOrien);
		}

		return new RubiksCube(toReturn);
	}

	protected byte[] state = null;

	public enum Cubie {
		YGR_CORNER, YBR_CORNER, YBO_CORNER, YGO_CORNER, // Corners
		WGO_CORNER, WBO_CORNER, WBR_CORNER, WGR_CORNER, // Corners
		YR, BR, WR, GR, YG, GO, // Edges
		WG, WO, WB, BO, YO, YB // Edges
	};

	public enum Face {
		FRONT(0,1,2,3,8,19,18,12), 
		REAR(4,5,6,7,10,14,15,16), 
		LEFT(7,0,3,4,11,12,13,14), 
		RIGHT(1,6,5,2,9,16,17,19), 
		TOP(7,6,1,0,10,9,8,11), 
		BOTTOM(3,2,5,4,18,17,15,13);

		Face(int tl, int tr, int br, int bl, int t, int r, int b, int l) {
			top_left = tl;
			top_right = tr;
			bottom_right = br;
			bottom_left = bl;
			top = t;
			right = r;
			bottom = b;
			left = l;
		};

		final int top_left;
		final int top_right;
		final int bottom_right;
		final int bottom_left;
		final int top;
		final int right;
		final int bottom;
		final int left;
	}
	
	public enum Rotation {
		CLOCKWISE, COUNTER_CLOCKWISE, HALF_TURN
	};
	
	private static EncodeStrategy cornerEncoder = new EncodeStrategyCorner();
	
	private static EncodeStrategy edgeOneEncoder = new EncodeStrategyEdge(CubieGroup.EDGE_ONE);
	
	private static EncodeStrategy edgeTwoEncoder = new EncodeStrategyEdge(CubieGroup.EDGE_TWO);
}