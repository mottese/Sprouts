package sprouts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Max Ottesen
 */
public class Boundary {
	private List<Node> nodes;

	public Boundary() {
		nodes = new ArrayList<Node>(0);
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public boolean containsNode(Node n) {
		return nodes.contains(n);
	}

	public void addNode(Node n) {
		if(!nodes.contains(n)) {
			nodes.add(n);
		}
	}

	public void addNodes(List<Node> l) {
		nodes.addAll(l);
	}

	public void removeNodes(List<Node> list) {
		nodes.removeAll(list);
	}

	//Just prints out 1 of each node in the boundary. Does NOT produce a valid input boundary string
	@Override
	public String toString() {
		String s = "";
		for(Node n : nodes) {
			s += n + ",";
		}
		return s.substring(0, s.length()-1);
	}
}
