import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

	public LinkedHashSet<Condition> getFacts() {

		return this.facts;
	}

	public void initialize(ComplexCondition g) {

		this.goals = g;

//		facts.addAll(g.sons);
		
		for(Condition c : (Collection<Condition>) g.sons) {
			this.addNode(c);
		}

	}

	public Node_new getNodeFromComplexCond(ComplexCondition p) {

		for (Node_new n : nodes) {
			if (n.getNode().equals(p)) {
				return n;
			}
		}

		return null;

	}

	public void addEdge(Condition node, Condition next) {
		
		
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
					
					//System.out.println("FOUND NODE");

					nodee = n;
					foundNode = true;
				}

				if (!foundNext && n.node.equals(next)) {
					
					//System.out.println("FOUND NEXT");

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
		nextt.addPrevious(nodee);

	}

	public HashSet<Node_new> getNodes() {

		return this.nodes;
	}

	public HashSet<Node_new> getAllPredecessors(Node_new n) {

		LinkedHashSet<Node_new> predecessors = new LinkedHashSet<Node_new>();

		ArrayList<Node_new> tmp = new ArrayList<Node_new>();

		tmp.add(n);

		Iterator it = tmp.listIterator();

		while (it.hasNext()) {

			Node_new tmpNode = (Node_new) it.next();
			predecessors.addAll(tmpNode.getPrev());
			tmp.addAll(tmpNode.getPrev());

			tmp.remove(it);
		}

		return predecessors;

	}

}
