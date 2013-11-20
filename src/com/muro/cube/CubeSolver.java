package com.muro.cube;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.muro.cube.EncodeStrategy.CubieGroup;
import com.muro.cube.RubiksCube.Face;
import com.muro.cube.RubiksCube.Rotation;


/**
 * This class provides methods for generating the heuristic tables necessary for finding the optimal solution to the Rubik's Cube, 
 * and for performing an IDA* search to find a solution path for a given cube configuration.
 */

/**
 * @author Joe M
 *
 */
public class CubeSolver {

	public static void main(String[] args) {


		// Present the user with the available options.
		System.out.print(menu);

		// Read in the user's choice.
		String userSelection = null; 
		userSelection = stdIn.nextLine().trim();

		while (!userSelection.equalsIgnoreCase("q")) {

			switch (userSelection.charAt(0)) {

			case '0':
				System.out.println("Generating the heuristic values for the corner cubies. This may take a while...");
				iterativeDeepening(new EncodeStrategyCorner(), MAX_CORNER_PERMUTATIONS, "corners.txt");
				System.out.println("Finished generating heuristic values. Results stored in file 'corners.txt'.");
				break;
			case '1':
				System.out.println("Generating the heuristic values for the edge cubies in group one. This may take a while...");
				iterativeDeepening(new EncodeStrategyEdge(CubieGroup.EDGE_ONE), MAX_EDGE_PERMUTATIONS, "edges1.txt");;
				System.out.println("Finished generating heuristic values. Results stored in file 'edges1.txt'.");
				break;
			case '2':
				System.out.println("Generating the heuristic values for the edge cubies in group two. This may take a while...");
				iterativeDeepening(new EncodeStrategyEdge(CubieGroup.EDGE_TWO), MAX_EDGE_PERMUTATIONS, "edges2.txt");
				System.out.println("Finished generating heuristic values. Results stored in file 'edges2.txt'.");
				break;
			case '3':
				RubiksCube startState = loadCubeFromFile();
				if (startState != null) {
					String solution = findOptimalSolution(startState);
					System.out.println("\nSolution Path: " + solution);
					System.out.println();
				}
				break;
			default:
				System.out.println("Not a valid choice. Please select an option from the following...");
			}

			System.out.print(menu);
			userSelection = stdIn.nextLine().trim();
		}

		// Cleanup...
		stdIn.close();

	}


	/**
	 * Finds the optimal solution to the goal state from the state contained in startNode.
	 * 
	 * @param startState the starting state
	 * 
	 * @return a string containing the path to the solution node
	 */
	public static String findOptimalSolution(RubiksCube startState) {

		// Reset statistics...
		nodeCount = 0;
		discoveredCount = 0;

		// Check if the cube is already solved.
		if (startState.isSolved()) {
			return "This cube is already solved!";
		}

		// Check if the heuristic tables have already been loaded into memory.
		if (!isLoaded) {

			System.out.println("Loading heuristic values from file \"corners.txt\".");
			cornerHeuristics = loadHeuristicValues("corners.txt", MAX_CORNER_PERMUTATIONS);

			System.out.println("Loading heuristic values from file \"edges1.txt\".");
			edgeOneHeuristics = loadHeuristicValues("edges1.txt", MAX_EDGE_PERMUTATIONS);

			System.out.println("Loading heuristic values from file \"edges2.txt\".");
			edgeTwoHeuristics = loadHeuristicValues("edges2.txt", MAX_EDGE_PERMUTATIONS);
			isLoaded = true;
		}

		// Create the root node from which the search will branch out.
		CubeNode startNode = new CubeNode(null, startState, null, null, 0, 0);

		// Set the heuristic value for the starting state.
		startNode.heuristic = lookupMaxHeuristic(startNode.state);

		// Begin the search
		System.out.println("Initiating search for optimal solution...");
		CubeNode solution = IDAStar(startNode);

		// Return the solution path.
		return buildSolutionString(solution);
	}

	/**
	 * Returns a RubikCube initialized to the cube state in the specified file. The cube state in the file
	 * is in a face representation of the cube. This method converts the face representation to our positional representation. 
	 * Returns null if an error occurs while reading the file or while attempting to perform the conversion.
	 *  
	 * @return a RubiksCube initialized to the specified state, or null if an error occurred.
	 */
	public static RubiksCube loadCubeFromFile() {

		boolean isLoaded = false;
		String userSelection = null;
		BufferedReader inFile = null;
		RubiksCube cube = null; 

		// Present the user with the available options.
		System.out.print("Please enter the filename where your cube state is stored, or enter \'!\' to return to the main menu.\n" +
				"  Enter Choice: ");

		// Read in the user's choice.
		userSelection = stdIn.nextLine().trim();

		while (!userSelection.equals("!") && !isLoaded) {
			try {
				inFile = new BufferedReader(new FileReader(userSelection));
				cube = doLoadCube(inFile);
				isLoaded = true;
			} catch (FileNotFoundException e) {
				System.out.println("File not found. Please make sure the file exists and is available for reading.");
				// Present the user with the available options.
				System.out.println("Enter the filename where your cube state is stored, or enter \'q\' to return to the main menu.");
				// Read in the user's choice.
				userSelection = stdIn.nextLine().trim();
			}
		}

		// Cleanup resources...
		if (inFile != null) { // Cleanup open files...
			try {
				inFile.close();
			} catch (IOException e) {
				System.out.println("I/O error occurred while cleaning up resources.");
			}
		}

		// Return the resulting cube state.
		return cube;
	}


	/**
	 * Creates a new RubiksCube initialized to the state in the specified file.
	 * 
	 * @param inFile a file containing the cube state
	 * @return a RubiksCube initialized to the cube state in the specified file
	 */
	private static RubiksCube doLoadCube(BufferedReader inFile) {

		StringBuilder stateString = new StringBuilder(); // holds the final result string
		String currentLine = null; // dummy variable to hold string to be processed

		try {
			while ((currentLine = inFile.readLine()) != null) {
				stateString.append(currentLine.trim());
			}
		} catch (IOException e) {
			System.out.println("Error occurred while reading file.");
		}

		if (stateString.length() != 54) { // the cube state in the specified file is invalid
			return null;
		} else { // try to create a RubiksCube from the specified string.
			return new RubiksCube(CubeLoader.loadState(stateString.toString()));
		}
	}


	/**
	 * Loads the heuristic values contained in the specified filename into a byte array.
	 * 
	 * @param filename a string representing the name of the file containing the heuristic values to be loaded.
	 * @param tableSize the number of the table
	 * @return an array of bytes containing the heuristic values
	 */
	private static byte[] loadHeuristicValues(String filename, int tableSize) {

		byte[] toReturn = new byte[tableSize];

		try (BufferedReader inFile = new BufferedReader(new FileReader(filename))) {
			for (int i=0; i < tableSize; i+=1) {
				toReturn[i] = Byte.parseByte(inFile.readLine());
			}
		} catch (IOException e) {
			System.out.println("Unable to load heuristic values contained in file \"" + filename + "\".");
		}

		return toReturn;
	}


	/**
	 * Performs IDAStar search on the specified node.
	 * 
	 * @param root
	 * 
	 * @return the solution node.
	 */
	private static CubeNode IDAStar(CubeNode root) {

		int heuristicMinimum = root.heuristic;

		CubeNode solution = null;

		// Loop until a solution is found.
		while (solution == null) {

			// start the search at the minimum path cost.
			solution = doSearch(root, 0, heuristicMinimum);

			// No solution found within the current minimum -- increase the depth of the search.
			heuristicMinimum += 1;
		}

		return solution;
	}

	/**
	 * Locates the optimal solution path by performing an IDA* search from the specified node.
	 * 
	 * @param toSearch the node to start the search from.
	 * @param pathcost the current pathcost to the specified node
	 * @param currentMin the minimum heuristic value
	 * 
	 * @return the solution node
	 */
	private static CubeNode doSearch(CubeNode toSearch, int pathcost, int currentMin) {

		System.out.println(++nodeCount + " nodes generated. The current minimum heuristic is " + currentMin);

		// Perform a goal test.
		if (toSearch.state.isSolved()) {
			return toSearch;
		}

		// Generate all node successors...
		List<CubeNode> successors = CubeSolver.generateSuccessors(toSearch);

		for (CubeNode successor : successors) {

			// Calculate the f cost for this node.
			int estimatedCost = pathcost + successor.heuristic;

			// If the estimated f cost is below the current minimum...
			if (estimatedCost <= currentMin) { // Try this path...

				// Travel-down the cheapest path searching for the goal state.
				CubeNode solution = doSearch(successor, pathcost + 1, currentMin);

				// Check if the goal state was found.
				if (solution != null) {
					return solution;
				}
			}
		}

		// No solution was found down this path so back the search up...
		return null;
	}


	/**
	 * Returns a list of successor nodes generated from the specified node.
	 * 
	 * @param root a CubeNode
	 * 
	 * @return a list containing the successors of the specified node
	 */
	private static List<CubeNode> generateSuccessors(CubeNode root) {

		List<CubeNode> successors = new ArrayList<CubeNode>(); // To store the successors
		RubiksCube dummyCube = null;
		int heuristic = 0;

		// Perform every possible rotation of each face.
		for (Face currentFace : Face.values()) {
			for (Rotation currentRotation : Rotation.values()) {	

				// The following checks are intended to reduce the generation of duplicate nodes.
				if (root.rotation == currentRotation && root.face == currentFace) {
					continue;
				}

				// Perform the operation
				dummyCube = root.state.performRotation(currentRotation, currentFace);

				// Lookup the maximum heuristic value for this cube state.
				heuristic = lookupMaxHeuristic(dummyCube);

				// Add this successor to the list.
				successors.add(new CubeNode(root, dummyCube, currentRotation, currentFace, heuristic, 0));
			}
		}

		return successors;
	}


	/**
	 * Calculates the maximum of the three heuristic values for the specified cube.
	 * 
	 * @param cube 
	 * @return an integer representing the maximum heuristic value for the specified cube.
	 */
	private static int lookupMaxHeuristic(RubiksCube cube) {

		// Lookup the heuristic values for each of the three cubie subgroups.
		int cornerValue = cornerHeuristics[cube.getCornerEncoding()];
		int edgeGroupOne= edgeOneHeuristics[cube.getEdgeOneEncoding()];
		int edgeGroupTwo = edgeTwoHeuristics[cube.getEdgeTwoEncoding()];

		// Put the values in an array so we can find the max.
		int[] values = {cornerValue, edgeGroupOne, edgeGroupTwo};
		int currentMax = values[0];

		// Loop over all the values and save the maximum.
		for (int i = 1; i < values.length; i += 1) {
			if (values[i] > currentMax)
				currentMax = values[i];
		}

		// Return the max heuristic value.
		return currentMax;
	}


	/**
	 * Returns a string containing the moves required to traverse the path from the solution node
	 * to the initial state.
	 * 
	 * @param solutionNode a CubeNode
	 * 
	 * @return a string representing the path to the optimal solution
	 */
	public static String buildSolutionString(CubeNode solutionNode) {

		Deque<String> moveStack = new ArrayDeque<String>();

		while (solutionNode.parent != null) {
			moveStack.push(solutionNode.face.toString() + ":" + solutionNode.rotation.toString());
			solutionNode = solutionNode.parent;
		}

		StringBuilder solutionString = new StringBuilder();

		while (!moveStack.isEmpty()) {
			solutionString.append(moveStack.pop() + ":");
		}

		return solutionString.toString();
	}


	/**
	 * Generates the heuristic values for the specified group of cubies.
	 * 
	 * @param subproblem specifies the cubie group for which the heuristic values need to be encoded.
	 */
	public static void generateHeuristics(EncodeStrategy.CubieGroup subproblem) {

		switch (subproblem) {

		case CORNER:
			doGenerateRecursive(new EncodeStrategyCorner(), MAX_CORNER_PERMUTATIONS, "corners.txt");
			break;
		case EDGE_ONE:
			doGenerateRecursive(new EncodeStrategyEdge(CubieGroup.EDGE_ONE), MAX_EDGE_PERMUTATIONS, "edges1.txt");
			break;
		case EDGE_TWO:
			doGenerateRecursive(new EncodeStrategyEdge(CubieGroup.EDGE_TWO), MAX_EDGE_PERMUTATIONS, "edges2.txt");
			break;
		}
	}

	/**
	 * A utility method for initiating the recursive depth-limited search. The encoder specifies the encoding algorithm to use
	 * which varies according to which group of cubies are being enumerated. The generated h-values are written to the specified
	 * output file.
	 * 
	 * @param encoder the encoding algorithm
	 * @param tableSize the number of permutations
	 * @param filename the name of the output file
	 */
	private static void doGenerateRecursive(EncodeStrategy encoder, int tableSize, String filename) {

		// Reset statistics...
		nodeCount = 0;
		discoveredCount = 0;

		// Stores the calculated heuristic values.
		byte[] hTable = new byte[tableSize];

		// Start with a solved cube.
		RubiksCube solved = new RubiksCube();
		CubeNode rootNode = new CubeNode(null, solved, null, null, 0, 0);

		// Mark the root as visited...
		hTable[0] = -1;

		// Explore it...
		dlsRecursive(rootNode, encoder, hTable, DEPTH_LIMIT);

		// Write the results to a file.
		CubeSolver.writeToFile(hTable, filename);
	}

	/**
	 * Generates h-values by performing iterative depending depth-limited search from the goal state.
	 * 
	 * @param encoder the encoding algorithm to use (corner, edge group one, edge group two).
	 * @param tableSize the number of permutations for the specified group
	 * @param filename the name of the output file
	 */
	private static void iterativeDeepening(EncodeStrategy encoder, int tableSize, String filename) {

		// Reset statistics...
		nodeCount = 0;
		discoveredCount = 0;
		goalCount = 0;

		// Stores the calculated heuristic values.
		byte[] hTable = new byte[tableSize];

		// Start with a solved cube.
		RubiksCube solved = new RubiksCube();
		CubeNode rootNode = new CubeNode(null, solved, null, null, 0, 0);

		for (int depth=0; depth < DEPTH_LIMIT; depth+=1) {
			dlsRecursive(rootNode, encoder, hTable, depth);
		}

		CubeSolver.writeToFile(hTable, filename);

	}


	/**
	 * Generates h-values by performing a recursive depth-limited search from the goal state. 
	 *
	 * @param startNode the node from which the search will be initiated.
	 * @param encoder an encoding strategy
	 * @param hTable a table of heuristic values
	 * @param depthLimit
	 */
	private static void dlsRecursive(CubeNode startNode, EncodeStrategy encoder, byte[] hTable, int depthLimit) {

		// Output the status of the search.
		System.out.println("total nodes generated " + nodeCount + " : new nodes discovered " + discoveredCount + 
				" : current depth " + startNode.heuristic + " goal nodes encountered " + goalCount + " goal heuristic " + hTable[0]);

		if (startNode.heuristic == depthLimit) {
			// Only record the heuristic if it is the minimum heuristic value.
			// If this state has been encountered previously than the current
			// heuristic value will be greater and therefore less accurate.
			if (hTable[startNode.permId] == 0) {
				hTable[startNode.permId] = (byte)depthLimit;
				++discoveredCount;
			}
			return;
		}

		// loop variables
		int encoding = 0;
		RubiksCube child = null;

		// Generate all successors...
		for (Face currentFace : Face.values()) {
			for(Rotation currentRotation: Rotation.values()) {

				// Prevent generation of duplicate nodes where possible...
				if (currentRotation == startNode.rotation) 
					continue;

				// Perform the twist
				child = startNode.state.performRotation(currentRotation, currentFace);
				// Encode the resulting cubie state.
				encoding = encoder.doEncode(child);
				// If this is a goal node we do not want to explore it...
				if (encoding == 0) 
					continue;

				// For debugging purposes...
				nodeCount++;

				dlsRecursive(new CubeNode(startNode, child, currentRotation, currentFace, startNode.heuristic+1, encoding),
						encoder, hTable, depthLimit);
			}
		}
	}

	@SuppressWarnings("unused")
	private static void dfs(EncodeStrategy encoder, int tableSize, String filename) {

		// Reset statistics...
		nodeCount = 0;
		discoveredCount = 0;

		// Stores the calculated heuristic values.
		byte[] hTable = new byte[tableSize];

		// Store the set of nodes to be explored...
		Deque<CubeNode> frontier = new ArrayDeque<>(300);
		// Tracks the set of explored nodes.
		Set<Integer> explored = new HashSet<Integer>();
		// Tracks the nodes on the frontier.
		Set<Integer> frontierSet = new HashSet<Integer>();

		// initialize the frontier using the initial state of problem
		RubiksCube solved = new RubiksCube();
		CubeNode rootNode = new CubeNode(null, solved, null, null, 0, 0);
		frontier.push(rootNode);
		frontierSet.add(0);

		// loop variables
		CubeNode current = null;
		int encoding = 0;
		RubiksCube child = null;

		while (!frontier.isEmpty()) {

			// choose a leaf node and remove it from the frontier
			current = frontier.pop();
			frontierSet.remove(current.heuristic);

			// record the heuristic value
			hTable[encoding] = (byte)current.heuristic;
			explored.add(current.permId);

			// Output the status of the search.
			System.out.println("total nodes generated " + nodeCount + " : new nodes discovered " + discoveredCount + 
					" : current depth " + current.heuristic + " : total explored " + explored.size() + " : frontier " + frontier.size());

			if (current.heuristic < DEPTH_LIMIT) {

				// Generate all successors...
				for (Face currentFace : Face.values()) {
					for(Rotation currentRotation: Rotation.values()) {
						// Prevent the generation of duplicate nodes.
						if (currentFace == current.face && currentRotation == current.rotation) continue;
						// Perform the twist
						child = current.state.performRotation(currentRotation, currentFace);
						// Encode the resulting cubie state.
						encoding = encoder.doEncode(child);
						// For debugging purposes...
						nodeCount++;

						// expand the chosen node, adding the resulting nodes to the frontier
						// only if not in the frontier or explored set
						if (!frontierSet.contains(encoding) && !explored.contains(encoding)) {
							frontier.add(new CubeNode(current, child, currentRotation, currentFace, current.heuristic + 1, encoding));
							frontierSet.add(encoding);
						}
					}
				}
			}
		}

		writeToFile(hTable, filename);
	}

	/**
	 * Writes the values contained in <code>aTable</code> to the specified file. Each
	 * value is separated by a newline character.
	 * 
	 * @param aTable an array of byte values
	 * @param filename a string specifying the name of the output file
	 */
	private static void writeToFile(byte[] aTable, String filename) {

		int lineCount = 0;

		try (PrintWriter outFile = new PrintWriter(new BufferedOutputStream(new FileOutputStream(filename)))) {
			for (int i=0; i < aTable.length; i+=1) {
				outFile.println(aTable[i]);
				lineCount += 1;
			}
		} catch (IOException e) {
			System.out.println("An error occurred while writing heuristic values to the file.");
			System.out.println("Error occurred at line " + lineCount);
		}
	}

	/**
	 * Stores the heuristic values for the corner cubies.
	 */
	private static byte[] cornerHeuristics;

	/**
	 * Stores the heuristic values for the first group of edge cubies.
	 */
	private static byte[] edgeOneHeuristics;

	/**
	 * Stores the heuristic values for the second group of edge cubies.
	 */
	private static byte[] edgeTwoHeuristics;

	/**
	 * A constant to represent the maximum number of unique corner cubie permutations.
	 */
	private static final int MAX_CORNER_PERMUTATIONS = 88_179_840;

	/**
	 * A constant to represent the maximum number of unique corner cubie permutations.
	 */
	private static final int MAX_EDGE_PERMUTATIONS = 42_577_920;

	/**
	 * The depth-limit of the search tree when performing depth-first search to generate the heuristic values.
	 */
	private static final int DEPTH_LIMIT = 11;

	/**
	 * A flag to indicate whether the heuristic tables have already been loaded into memory. 
	 */
	private static boolean isLoaded = false;

	/**
	 * A global scanner input to allow all methods to parse user input.
	 */
	private static final Scanner stdIn = new Scanner(System.in);

	/**
	 * The menu options to display to the user.
	 */
	private static String menu = 
			"\nPlease select from among the following options.\n" +
					"  0 - Generate heuristic values for the corner cubies.\n" +
					"  1 - Generate heuristic values for edge group one.\n" +
					"  2 - Generate heuristic values for edge group two.\n" +
					"  3 - Search for an optimal solution.\n" +
					"  q - Quit CubeSolver.\n\n" +
					"  Enter Choice : ";

	private static int discoveredCount = 0;

	private static int nodeCount = 0;

	private static int goalCount = 0;

	public static final byte[] SOLVED_STATE = {
		0, 3, 6, 9, 12, 15, 18, 21, 
		0, 2, 4, 6, 8, 10, 
		12, 14, 16, 18, 20, 22
	};

	public static final byte[] MAX_VALUE_STATE = {
		23, 20, 17, 14, 11, 8, 5, 2,
		23, 21, 19, 17, 15, 13, 
		11, 9, 7, 5, 3, 1
	};

}
