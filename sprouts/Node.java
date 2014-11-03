package sprouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: Max Ottesen
 */
public class Node {
	private int        edges;
	private int        id;
	private List<Node> neighbors;
	private List<Boundary> boundaries;
	private boolean    visited;
	private static int counter = 0; //used for giving new nodes a unique id

	public Node(int id) {
		neighbors = new ArrayList<Node>(0);
		boundaries = new ArrayList<Boundary>(0);
		this.id = id;
		if(id >= counter) {
			counter = id+1; //keeps track of the largest node ID so that you can give new nodes a unique idea
		}
		visited = false;
	}

	//creates a new node that has 2 neighbors. This is used when you're connecting two nodes with a line and making a new
	// node in the center of that line
	public Node(Node n, Node m) {
		neighbors = new ArrayList<Node>(0);
		boundaries = new ArrayList<Boundary>(0);
		this.id = counter++;
		neighbors.add(n);
		neighbors.add(m);
		edges = 2;
		visited = false;
	}

	public void addBoundary(Boundary b) {
		if(!boundaries.contains(b)) {
			boundaries.add(b);
		}
	}

	public void removeBoundary(Boundary b) {
		boundaries.remove(b);
	}

	public List<Boundary> getBoundaries() {
		return boundaries;
	}

	public void addNeighbor(Node n, boolean forceAdd) {
		//two nodes cannot be connected by more than two edges and a node cannot be connected with itself
		if(Collections.frequency(neighbors, n) == 2 || this == n) {
			return;
		}
		//forceAdd is used if you are sure that a node should be connected more than once with another node. When you connect
		// a node to itself and make a new node on that line, then the new node and the old node are both connected with
		// each other by two edges
		if(!neighbors.contains(n) || forceAdd) {
			neighbors.add(n);
			edges++;
		}
	}

	//used when doing a dfs
	public boolean getVisited() {
		return visited;
	}

	public void setVisited(boolean b) {
		this.visited = b;
	}

	public int getID() {
		return id;
	}

	public int getEdges() {
		return edges;
	}

	public List<Node> getNeighbors() {
		return neighbors;
	}

	@Override
	public String toString() {
		String s = this.id + "(";
		//for(Node n : neighbors) {
		//	s += n.getID() + ",";   //prints neighbor nodes
		//}
		s += neighbors.size() + ",";
		return s.substring(0, s.length()-1) + ")";
	}
}
