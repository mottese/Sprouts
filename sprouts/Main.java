package sprouts;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Max Ottesen
 */
public class Main {

	//turns a game string into a game object
	public static Game parse(String state) {
		List<Node> nodeList = new ArrayList<Node>(0);
		Game game = new Game();

		//Splits the game into faces
		String[] faces = state.split("/");
		for(String s : faces) {
			Face f = new Face();
			game.addFace(f);

			//split face into boundaries
			String[] boundaries = s.split(";");
			for(String b : boundaries) {
				f.addBoundary(handleBoundary(b, nodeList));
			}
		}

		game.addNodes(nodeList);

		return game;
	}

	private static Boundary handleBoundary(String s, List<Node> nodeList) {
		Boundary boundary = new Boundary();

		Node onePreviousNode = null;
		String[] nodes = s.split(",");
		for(String n : nodes) {
			Node node = null;

			//Get the actual Node object if it already exists, otherwise make one
			for(Node l : nodeList) {
				if(l.getID() == Integer.parseInt(n)) {
					node = l;
					break;
				}
			}
			if(node == null) {
				node = new Node(Integer.parseInt(n));
				nodeList.add(node);
			}

			boundary.addNode(node);
			node.addBoundary(boundary);

			if(onePreviousNode != null) {
				//add the previous node as a neighbor to the current node and vice versa
				node.addNeighbor(onePreviousNode, false);
				onePreviousNode.addNeighbor(node, false);


				//The rest of this code determines if nodes are in multiple boundaries. If they are in multiple boundaries, then
				// you know that you have some type of loop in the game. Loops have to be handled carefully so that the nodes
				// have the correct neighbors and the correct number of edges
				node.removeBoundary(boundary);
				onePreviousNode.removeBoundary(boundary);

				boolean length1 = false;
				boolean length2 = false;
				for(Boundary b : node.getBoundaries()) {
					if(b.getNodes().size() == 2) {
						length1 = true;
						break;
					}
				}
				for(Boundary b : onePreviousNode.getBoundaries()) {
					if(b.getNodes().size() == 2) {
						length2 = true;
						break;
					}
				}

				if(node.getBoundaries().size() > 0 && onePreviousNode.getBoundaries().size() > 0 && (nodes.length == 2 || (length1 && length2))) {
					node.addNeighbor(onePreviousNode, true);
					onePreviousNode.addNeighbor(node, true);
				}

				node.addBoundary(boundary);
				onePreviousNode.addBoundary(boundary);
			}

			onePreviousNode = node;
		}

		//connect the first node in the boundary string to the last node in the boundary string
		Node firstNode = boundary.getNodes().get(0);
		Node lastNode = boundary.getNodes().get(boundary.getNodes().size() - 1);

		firstNode.addNeighbor(lastNode, false);
		lastNode.addNeighbor(firstNode, false);


		return boundary;
	}

	public static void main(String[] args) {
		Game game;

		while (true) {
			String gameString = JOptionPane.showInputDialog(null, "Enter the Game String you would like to decide if it is a win or loss");
			if(gameString == null || gameString.length() == 0) {
				break;
			}
			Long time = System.currentTimeMillis();
			game = parse(gameString);
			game.generateGameGraph(game);
			boolean isWin = game.isWinning(game);
			time = System.currentTimeMillis() - time;
			if (isWin){
				JOptionPane.showMessageDialog(null, "You will win! yay!\nGraph size: " + game.graphSize(game) + "\n" + time + " ms");
			}
			else {
				JOptionPane.showMessageDialog(null, "You will lose\nGraph size: " + game.graphSize(game) + "\n" + time + " ms");
			}
		}


		//TESTING CASES

		//game = parse("1,2/1,2");
		//game.printInfo();
		//game.testMove();

		//game = parse("2,4,3,4,2,5;6/2,5;1");
		//game.printInfo();
		//game.testMove();

		//game = parse("5,2;1/3,4,2,5,2,4");
		//game.printInfo();

		//game = parse("4,3,4,2,5,2/1;2,5");
		//game.printInfo();



		//game = parse("2,3,4,1,3/4,3,1");
		//game.printInfo();

		//game = parse("3,1,4/3,4,1,3,2");
		//game.printInfo();



		//game = parse("1,4,5,2,6,3,6,2,4/4,5,2");
		//game.printInfo();



		//game = parse("1;2;3");
		//game.printInfo();
		//System.out.println("---------");
		//game.testMove();



		//game = parse("1,3,2,4/1,3,2,4;5");
		//game.printInfo();
		//game.testMove();
	}
}



