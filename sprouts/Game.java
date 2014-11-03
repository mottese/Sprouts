package sprouts;

import java.util.*;

/**
 * @author: Max Ottesen
 */
public class Game {
	private List<Face> faces;
	private List<Node> nodes;
	private List<Game> children; //a Game stores children that correspond to moves that can be made from the current game state. This is the game tree
	private boolean p2Turn;

	
	
	
	
	public Game() {
		this.faces = new ArrayList<Face>(0);
		this.nodes = new ArrayList<Node>(0);
		this.children = new ArrayList<Game>(0);
	}

	//does a deep copy of the current Game object
	public Game copy(){
		Game game = new Game();
		List<Face> facescopy = new ArrayList<Face>();
		List<Boundary> boundariescopy = new ArrayList<Boundary>();
		HashMap<Integer, Node> idToNode = new HashMap<Integer, Node>();

		//copies all nodes in the game
		for (Node n : nodes){
			Node n1 = new Node(n.getID());
			idToNode.put(n1.getID(), n1);
		}
		
		for (Node n : nodes){
			for (Node n1 : n.getNeighbors()){
				idToNode.get(n.getID()).addNeighbor(idToNode.get(n1.getID()), true);
			}
		}

		//copies all faces in the game
		for (Face f  : faces){
			Face fcopy = new Face();
			for (Boundary b : f.getBoundaries()){ //copies all boundaries in the game
				Boundary bcopy = new Boundary();
				for (Node n : b.getNodes()){
					bcopy.addNode(idToNode.get(n.getID()));
					idToNode.get(n.getID()).addBoundary(bcopy);
				}
				boundariescopy.add(bcopy);
				fcopy.addBoundary(bcopy);
			}
			facescopy.add(fcopy);
		}
	
		for (Face f : facescopy){
			game.addFace(f);
		}

		game.addNodes(idToNode.values());
		
		return game;
			
	}
	
	public boolean isP1sMove(){
		return (!p2Turn);
	}
	
	public void setP1sMove(boolean p1sMove){
		this.p2Turn = (!p1sMove);
	}
	
	public void addChild(Game g){
		this.children.add(g);
	}
	
	public List<Game> getChildren(){
		return this.children;
	}
	
	public List<Face> getFaces(){
		return faces;
	}

	public void addFace(Face f) {
		this.faces.add(f);
	}

	public void addNode(Node n) {
		this.nodes.add(n);
	}

	public void addNodes(Collection<Node> l) {
		this.nodes.addAll(l);
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public Node getNode(int id) {
		for(Node n : nodes) {
			if(n.getID() == id) {
				return n;
			}
		}
		return null;
	}

	//finds a cycle in a boundary between the two given nodes. This is used when you're making a move that creates a new Face
	private List<Node> findCycle(Node n, Node m) {
		List<Node> list = new ArrayList<Node>(0);
		if(n != m) {
			list = dfs(n, m);
		}
		list.add(n);
		for(Node w : nodes) {
			w.setVisited(false);
		}
		return list;
	}

	//the DFS algorithm used by the findCycle method
	private List<Node> dfs(Node n, Node m) {
		n.setVisited(true);
		for(Node child : n.getNeighbors()) {
			if(child == m) {
				List<Node> list = new ArrayList<Node>(0);
				list.add(m);
				return list;
			}
			if(!child.getVisited()) {
				List<Node> list = dfs(child, m);
				if(list != null) {
					list.add(child);
					return list;
				}
			}

		}
		return null;
	}

	//Makes a move between Node n and m surrounding the given boundaries
	public Game makeMove(Node n, Node m, List<Boundary> bounds, Game g) {
		Game newGame = g.copy();

		n = newGame.getNode(n.getID());
		m = newGame.getNode(m.getID());

		//changes which player makes the next move
		if (g.isP1sMove() == true){
			newGame.setP1sMove(false);
		}
		else newGame.setP1sMove(true);

		List<Boundary> boundaries = new ArrayList<Boundary>(0);

		Face oldFace = newGame.getFace(n);

		//legality check when surrounding boundaries
		if(bounds != null && bounds.size() > 0) {
			for(Boundary b : bounds) {
				Node tn = newGame.getNode(b.getNodes().get(0).getID());
				Boundary temp = oldFace.getBoundary(tn);
				if(temp != null) {
					boundaries.add(temp);
				}
			}
			boundaries.removeAll(Collections.singleton(null));
			if(boundaries == null || boundaries.size() == 0) return null;
			for(Boundary b : boundaries) {
				if(!newGame.isLegal(n, m, b)) {
					return null;
				}
			}
		}
		//legality check when not surrounding anything
		else {
			if(!newGame.isLegal(n, m, null)) {
				return null;
			}
		}




		//debugging information

		//if(bounds != null && bounds.size() > 0) {
		//	for(Boundary b : oldFace.getBoundaries()) {
		//		System.out.print(b + ";");
		//	}
		//	for(Boundary b : boundaries) {
		//		System.out.print(b + ";");
		//	}
		//	System.out.println();
		//}
		//if(n == m && n.getEdges() >= 2) System.out.print("true");
		//System.out.print(n + "," + m + ":");
		//for(Boundary b : boundaries) {
		//	System.out.print(b + ";");
		//}
		//System.out.println();





		//one boundary move (ie a move that creates a new face)
		if(newGame.inSameBoundary(n, m)) {
			Boundary oldBoundary = oldFace.getBoundary(n);
			Face newFace = new Face(boundaries);
			oldFace.removeBoundaries(boundaries);

			Node newNode = new Node(n, m);
			n.addNeighbor(newNode, true);
			m.addNeighbor(newNode, true);
			oldBoundary.addNode(newNode);
			newNode.addBoundary(oldBoundary);
			Boundary newBoundary = new Boundary();


			List<Node> list = findCycle(n, m);
			newBoundary.addNodes(list);
			newBoundary.addNode(newNode);
			newNode.addBoundary(newBoundary);

			if(n != m) {
				list.remove(n);
				list.remove(m);
				list.remove(newNode);
				oldBoundary.removeNodes(list);
			}

			newFace.addBoundary(newBoundary);
			newGame.addFace(newFace);
			newGame.addNode(newNode);
		}
		//two boundary move (ie - a move that does not create a new face
		else {
	  	Boundary nBoundary = oldFace.getBoundary(n);
			Boundary mBoundary = oldFace.getBoundary(m);

			nBoundary.addNodes(mBoundary.getNodes());

			Node newNode = new Node(n, m);
			nBoundary.addNode(newNode);

			n.addNeighbor(newNode, true);
			m.addNeighbor(newNode, true);

			oldFace.removeBoundary(mBoundary);
			newGame.addNode(newNode);
		}

		return newGame;
	}

	//finds whether or not two nodes are in a boundary together
	private boolean inSameBoundary(Node n, Node m) {
		for(Face f : faces) {
			for(Boundary b : f.getBoundaries()) {
				if(b.containsNode(n) && b.containsNode(m)) {
					return true;
				}
			}
		}
		return false;
	}

	//finds a face that contains the given node
	public Face getFace(Node n) {
		for(Face f : faces) {
			if(f.getNodes().contains(n)) {
				return f;
			}
		}
		return null;
	}

	//checks if a move from node n to node m surrounding the given boundary is a legal move
	private boolean isLegal(Node n, Node m, Boundary b) {
		if(n.getEdges() >= 3 || m.getEdges() >= 3) { //nodes can only have 3 edges
			return false;
		}
		if(m == n && n.getEdges() >= 2) { //if the line being drawn is from one node to itself, then that node can only have 2 edges
			return false;
		}
		if(b != null && (b.containsNode(n) || b.containsNode(m))) { //you can't surround the boundary that one of the nodes is in
			return false;
		}

		for(Face f : faces) { //the nodes and boundary have to all be in the same face
			List<Node> nodes = f.getNodes();
			if(nodes.contains(n) && nodes.contains(m) && (b == null || f.containsBoundary(b))) {
				return true;
			}
		}
		return false;
	}



	public boolean isGameOver(Game g){
		for (Face f : g.getFaces()){
			int connCount  = 0;
			int nodeCount = 0;
			for (Boundary b : f.getBoundaries()){
				for (Node n : b.getNodes()){
					nodeCount++;
					connCount += n.getEdges();
				}
			}
			if (((nodeCount * 3)-1) > connCount){
				return false;
			}
		}

		return true;
	}

	//finds all combinations of boundaries. Used when making all possible moves
	public List<List<Boundary>> getBoundaryPowerSet(List<Boundary> bs){
		List<List<Boundary>> ps = new ArrayList<List<Boundary>>();
		if (bs.isEmpty()) {
			ps.add(new ArrayList<Boundary>());
			return ps;
		}
		List<Boundary> list = new ArrayList<Boundary>(bs);
		Boundary head = list.get(0);
		List<Boundary> rest = new ArrayList<Boundary>(list.subList(1, list.size()));
		for (List<Boundary> set : getBoundaryPowerSet(rest)) {
			List<Boundary> newList = new ArrayList<Boundary>();
			newList.add(head);
			newList.addAll(set);
			ps.add(newList);
			ps.add(set);
		}

		return ps;
	}

	//determines whether player 1 is winning or losing
	public boolean isWinning(Game g){
		if (g.getChildren().isEmpty()){
			return (!(g.isP1sMove()));
		}
		boolean decision = false;
		if (g.isP1sMove()){
			List<Boolean> childrenValues = new ArrayList<Boolean>();
			for (Game child : g.getChildren()){
				childrenValues.add(isWinning(child));
			}
			for (Boolean b : childrenValues){
				decision = b || decision;
			}
		}
		else {
			decision = true;
			List<Boolean> childrenValues = new ArrayList<Boolean>();
			for (Game child : g.getChildren()){
				childrenValues.add(isWinning(child));
			}
			for (Boolean b : childrenValues){
				decision = b && decision;
			}
		}
		return decision;
	}

	//makes all possible moves for a staring game
	public void generateGameGraph(Game g){
		if (isGameOver(g)){
			return;
		}
		for (Node n : g.getNodes()){
			for (Node m : g.getNodes()){
				List<Face> common = getCommonFaces(n, m, g);
				if (!(common.isEmpty())){
					for (Face f : common){
						for (List<Boundary> b : getBoundaryPowerSet(f.getBoundaries())){
							if (b.contains(n.getBoundaries()) || b.contains(m.getBoundaries())) continue;
							Game child = makeMove(n,m,b,g);
							if(child == null) continue;
							g.addChild(child);
							generateGameGraph(child);
						}
					}
				}
			}
		}
	}

	//finds all the faces that the given nodes are both in
	public List<Face> getCommonFaces(Node n, Node m, Game g){
		List<Face> facesInCommon = new ArrayList<Face>();
		for (Face f : g.getFaces()){
			for (Boundary b : f.getBoundaries()){
				if (b.getNodes().contains(n) && b.getNodes().contains(m)){
					facesInCommon.add(f);
				}
			}
		}
		return facesInCommon;
	}

	//counts how big our game graph is
	public int graphSize(Game rootGame) {
		int size = rootGame.getChildren().size();
		for(Game g : rootGame.getChildren()) {
			size += graphSize(g);
		}
	  return size;
	}






	//used for degugging
	public void printInfo() {

		System.out.println(faces.size() + " face(s)");
		for(Face f : faces) {
			System.out.println("face");
			for(Boundary b : f.getBoundaries()) {
				if(b == null) {
					continue;
				}
				for(Node n : b.getNodes()) {
					System.out.print(n.getID() + ",");
				}
				System.out.println("\n");
			}
		}

		System.out.println();



		List<Node> list = new ArrayList<Node>(0);
		for(Face f : faces) {
			for(Node n : f.getNodes()) {
				if(!list.contains(n)) list.add(n);
			}
		}

		Node v = null, w = null;
		for(Node n : list) {
			if(n.getID() == 3) {
				v = n;
			}
			if(n.getID() == 4) {
				w = n;
			}

			System.out.print(n.toString() + ":");
			for(Node t : n.getNeighbors()) {
				System.out.print(t.toString() + ",");
			}
			System.out.print("(" + n.getEdges() + ")");
			System.out.println();
		}
		System.out.println();

		//for(Node n : findCycle(v, w)) {
		//	System.out.print(n + ",");
		//}
		for(Node n : list) {
			n.setVisited(false);
		}
		System.out.println();

	}

	//used for debugging
	//VERY dependent on the game, so make sure to only call this with a specific game in mind
	public void testMove() {
		Node v = null, w = null;
		for(Node n : this.nodes) {
			if(n.getID() == 1) {
				v = n;
			}
			if(n.getID() == 1) {
				w = n;
			}
		}

		List<Boundary> boundariesToSurround = new ArrayList<Boundary>(0);

		//for(Node n : nodes) {
		//	if(n.getID() == 1) {
				//boundariesToSurround.addAll(n.getBoundaries());
		//	}
		//}

		Game newGame = this.makeMove(v, w, boundariesToSurround, this);
		newGame.printInfo();
	}
}
