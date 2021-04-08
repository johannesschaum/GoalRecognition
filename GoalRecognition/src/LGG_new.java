import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;

import com.hstairs.ppmajal.conditions.ComplexCondition;
import com.hstairs.ppmajal.conditions.Condition;
import com.hstairs.ppmajal.conditions.Predicate;

public class LGG_new {

	HashSet<Node_new> nodes = new HashSet<Node_new>();
	LinkedHashSet<Condition> facts = new LinkedHashSet<Condition>();
	ComplexCondition goals;

	public LGG_new() {

	}

	public boolean containsNode(Condition p) {

		if (p instanceof Predicate) {
			return facts.contains(p);
		}

		return facts.containsAll(((ComplexCondition) p).sons);
	}

	public void addNode(Condition node) {

		Node_new n = new Node_new(node);
		nodes.add(n);

		if (node instanceof Predicate) {
			facts.add(node);
		} else {
			facts.addAll(((ComplexCondition) node).sons);
		}

	}

	public void removeNode(Condition node) {

		Node_new nodeToRemove = null;

		for (Node_new n : nodes) {

			if (n.getNode().equals(node)) {

				for (Node_new prev : n.getPrev().values()) {

					prev.getNext().remove(n);
				}
				for (Node_new next : n.getNext()) {

					next.getPrev().remove(n);
				}

				nodeToRemove = n;
				System.out.println("Found Node to Remove");
				break;

			}

		}

		if (nodeToRemove != null) {
			facts.removeAll(nodeToRemove.getSons());
			nodes.remove(nodeToRemove);
		} else {
			System.out.println("Didnt find node to remove");
		}

	}

	public LinkedHashSet<Condition> getFacts() {

		return this.facts;
	}

	public void initialize(ComplexCondition g) {

		this.goals = g;

//		facts.addAll(g.sons);

		for (Condition c : (Collection<Condition>) g.sons) {
			this.addNode(c);
			this.getNodeFromCond(c).addLmSetID(0);
		}

	}

	public Node_new getNodeFromCond(Condition p) {

		for (Node_new n : nodes) {
			if (n.getNode().equals(p)) {
				return n;
			}
		}

		return null;

	}

	public void addEdge(Condition node, Condition next, Integer i) {

//		System.out.println("TRYING TO ADD EDGE");
//		System.out.println("NODE: "+node+" , NEXT: "+next);
//		System.out.println();
//		System.out.println("FACTS IN LGG: " + facts);
//		System.out.println();
//		System.out.println("NODES IN LGG: "+nodes);
//		System.out.println("-------------------------");
//		

		boolean foundNode = false;
		boolean foundNext = false;

		Node_new nodee = null;
		Node_new nextt = null;

		if (node instanceof Predicate && next instanceof Predicate) {

			for (Node_new n : nodes) {

				if (!foundNode && n.node.equals(node)) {

					// System.out.println("FOUND NODE");

					nodee = n;
					foundNode = true;
				}

				if (!foundNext && n.node.equals(next)) {

					// System.out.println("FOUND NEXT");

					nextt = n;
					foundNext = true;
				}

			}
		} else if (node instanceof Predicate && next instanceof ComplexCondition) {

			for (Node_new n : nodes) {

				if (!foundNode && n.node.equals(node)) {

					nodee = n;
					foundNode = true;
				}

				if (!foundNext && n.getSons().containsAll(((ComplexCondition) next).sons)) {

					nextt = n;
					foundNext = true;
				}

			}
		} else if (node instanceof ComplexCondition && next instanceof Predicate) {

			for (Node_new n : nodes) {

				if (!foundNode && n.getSons().containsAll(((ComplexCondition) node).sons)) {

					nodee = n;
					foundNode = true;
				}

				if (!foundNext && n.node.equals(next)) {

					nextt = n;
					foundNext = true;
				}

			}
		} else {

			for (Node_new n : nodes) {

				if (!foundNode && n.getSons().containsAll(((ComplexCondition) node).sons)) {

					nodee = n;
					foundNode = true;
				}

				if (!foundNext && n.getSons().containsAll(((ComplexCondition) next).sons)) {

					nextt = n;
					foundNext = true;
				}

			}
		}

		nodee.addNext(nextt);
		nextt.addPrevious(i, nodee);

	}

	public HashSet<Node_new> getNodes() {

		return this.nodes;
	}

	public void getAllPredecessors(Node_new n, MultiValuedMap<Integer, Node_new> predecessors) {

		predecessors.putAll(n.getPrev());

		for (Node_new prev : n.getPrev().values()) {

			//TODO WORK IN PROGRESS
			if (!predecessors.values().containsAll(prev.getPrev().values())) { //&& !prev.getLmSetIDs().contains(0)) {
				getAllPredecessors(prev, predecessors);
			}
		}

//		LinkedHashSet<Node_new> predecessors = new LinkedHashSet<Node_new>();
//
//		ArrayList<Node_new> tmp = new ArrayList<Node_new>();
//
//		tmp.add(n);
//
//		ListIterator<Node_new> it = tmp.listIterator();
//
//		while (it.hasNext()) {
//
//			Node_new tmpNode = (Node_new) it.next();
//			
//			predecessors.addAll(tmpNode.getPrev());
//			
//			
//			//TODO works correctly?
//			it.remove();
//			
//			for(Node_new node : tmpNode.getPrev()) {
//				it.add(node);
//			}
//			//tmp.addAll(tmpNode.getPrev());
//
//			//tmp.remove(it);
//			
//
//		}
//
//		return predecessors;

	}

}
