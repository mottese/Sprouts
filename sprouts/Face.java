package sprouts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Max Ottesen
 */
public class Face {
	private List<Boundary> boundaries;

	public Face(List<Boundary> boundaries) {
		this.boundaries = boundaries;
	}
	public Face() {
		this.boundaries = new ArrayList<Boundary>(0);
	}


	public List<Node> getNodes() {
		List<Node> list = new ArrayList<Node>(0);

		for(Boundary b : boundaries) {
			for(Node n : b.getNodes()) {
				if(!list.contains(n)) {
					list.add(n);
				}
			}
		}

		return list;
	}

	public void removeBoundaries(List<Boundary> list) {
		boundaries.removeAll(list);
	}

	public List<Boundary> getBoundaries() {
		return boundaries;
	}

	//returns the boundary that contains Node n
	public Boundary getBoundary(Node n) {
		for(Boundary b : boundaries) {
			if(b.containsNode(n)) {
				return b;
			}
		}
		return null;
	}


	public void addBoundary(Boundary b) {
		this.boundaries.add(b);
	}

	public void removeBoundary(Boundary b) {
		this.boundaries.remove(b);
	}

	public boolean containsBoundary(Boundary b) {
		return boundaries.contains(b);
	}
}
