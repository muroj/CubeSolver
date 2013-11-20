package com.muro.cube;

import com.muro.cube.RubiksCube.Face;
import com.muro.cube.RubiksCube.Rotation;


/**
 * @author Joe M
 *
 */
class CubeNode {
	
	CubeNode(CubeNode parent, RubiksCube aCube, Rotation rotation, Face face, int heuristic, int id) {
		this.state = aCube;
		this.parent = parent;
		this.rotation = rotation;
		this.face = face;
		this.heuristic = heuristic;
		this.permId = id;
	}
	
	/**
	 * The cube state at this particular node.
	 */
	protected RubiksCube state = null;
	
	/**
	 * A reference to the node from which this node was generated.
	 */
	protected CubeNode parent = null;
	
	/**
	 * Represents the rotation (CW, CCW, 180) that generated this state.
	 */
	protected RubiksCube.Rotation rotation;
	
	/**
	 * Represents the face (F,B,L,R,T,B) that generated this state.
	 */
	protected RubiksCube.Face face;
	
	/**
	 * The path cost to this node from the goal state.
	 */
	protected int heuristic = 0;
	
	/**
	 * A unique integer representing the state of this cube.
	 */
	protected int permId = -1;
	
}
