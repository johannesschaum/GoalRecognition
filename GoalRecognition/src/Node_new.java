import com.hstairs.ppmajal.conditions.*;
import com.hstairs.ppmajal.problem.GroundAction;
import com.hstairs.ppmajal.problem.PDDLState;
import com.hstairs.ppmajal.problem.RelState;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class Node_new {

	MultiValuedMap<Integer, Node_new> previous = new ArrayListValuedHashMap<>();
	LinkedHashSet<Node_new> next = new LinkedHashSet<Node_new>();
	Condition node;
	LinkedHashSet<Condition> sons = new LinkedHashSet<Condition>();
	HashSet<Integer> lmSetIDs = new HashSet<Integer>();

	public Node_new(Condition c) {
		this.node = c;
		if (c instanceof Predicate) {
			sons.add(c);
		} else {
			sons.addAll(((ComplexCondition) c).sons);
		}
	}

	public HashSet<Integer> getLmSetIDs() {

		return lmSetIDs;

	}

	public void addLmSetID(Integer i) {
		this.lmSetIDs.add(i);
	}

	public Node_new(Condition c, Node_new next) {

		this.node = c;
		this.next.add(next);

		if (c instanceof Predicate) {
			sons.add(c);
		} else {
			sons.addAll(((ComplexCondition) c).sons);
		}

	}

	public void addPrevious(Integer i, Node_new p) {

		this.previous.put(i, p);
	}

	public void addNext(Node_new p) {

		this.next.add(p);
	}

	public Condition getNode() {

		return node;
	}

	public LinkedHashSet<Condition> getSons() {
		return this.sons;
	}

	public MultiValuedMap<Integer, Node_new> getPrev() {
		return previous;
	}

	public HashSet<Node_new> getNext() {
		return next;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer("[  ");

		for (Entry<Integer, Node_new> e : previous.entries()) {
			sb.append(e.getValue().getNode() + "|" + e.getKey() + ", ");

		}

		sb.append("--->");

		sb.append(node);
		sb.append("| " + lmSetIDs);

		sb.append("--->");

		for (Node_new p : next) {

			sb.append(p.getNode() + ", ");

		}

		sb.append("]  ");

		return sb.toString();
	}

}
