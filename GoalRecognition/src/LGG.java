import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hstairs.ppmajal.conditions.Predicate;

public class LGG {

	HashSet<Node> nodes = new HashSet<Node>();
	HashSet<Predicate> predicates = new HashSet<Predicate>();
	HashSet<Predicate> goals = new HashSet<Predicate>();

	public LGG() {

	}

	public boolean containsNode(Predicate p) {

		return predicates.contains(p);
	}

	public void addNode(Predicate node) {

		Node n = new Node(node);
		nodes.add(n);
		predicates.add(node);

	}
	
	public HashSet<Predicate> getPredicates(){
		
		return this.predicates;
	}

	public void initialize(Collection<Predicate> g) {

		this.goals.addAll(g);

		for (Predicate p : g) {

			Node n = new Node(p);
			nodes.add(n);
			predicates.add(p);

		}

	}

	public void addEdge(Predicate node, Predicate next) {

		boolean foundNode = false;
		boolean foundNext = false;

		for (Node n : nodes) {

			if (!foundNode && n.node.equals(node)) {

				n.addNext(next);
				foundNode = true;
			}

			if (!foundNext && n.node.equals(next)) {

				n.addPrevious(node);
				foundNext = true;
			}

		}

	}

	public HashSet<Node> getNodes() {

		return this.nodes;
	}

	public HashSet<Predicate> getAllPredecessors(Node n) {

		HashSet<Predicate> predecessors = new HashSet<Predicate>();

		HashSet<Predicate> tmp = new HashSet<Predicate>();

		HashSet<Node> newNodes = new HashSet<Node>();

		predecessors.addAll(n.getPrev());

		tmp.addAll(n.getPrev());

		while (true) {

			for (Node node : nodes) {
				for (Predicate p : tmp) {

					if (node.getNode().equals(p)) {
						newNodes.add(node);
					}
				}
			}
			
			//System.out.println("TMP: " + tmp);

			tmp = new HashSet<Predicate>();

			for (Node node : newNodes) {

				tmp.addAll(node.getPrev());

			}
			
			predecessors.addAll(tmp);
			
			//System.out.println("NEW NODES: " + newNodes);
			
			newNodes = new HashSet<Node>();			

			if (tmp.isEmpty()) {
				break;
			}
		}

		return predecessors;

	}

	public void approximateReasonableOrders() {

		// TODO

	}

}
