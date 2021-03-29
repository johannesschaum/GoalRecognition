import com.hstairs.ppmajal.conditions.*;
import com.hstairs.ppmajal.problem.GroundAction;
import com.hstairs.ppmajal.problem.PDDLState;
import com.hstairs.ppmajal.problem.RelState;

import java.util.*;
public class Node_new {
	
	LinkedHashSet<Node_new> previous = new LinkedHashSet<Node_new>();
	LinkedHashSet<Node_new> next = new LinkedHashSet<Node_new>();
	Condition node;
	LinkedHashSet<Condition> sons = new LinkedHashSet<Condition>();
	
	
	public Node_new(Condition c) {
		this.node = c;
		if(c instanceof Predicate) {
			sons.add(c);
		}
		else {
			sons.addAll(((ComplexCondition)c).sons);
		}
	}
	
	public Node_new(Condition c, Node_new next) {
		
		this.node = c;
		this.next.add(next);
		
		if(c instanceof Predicate) {
			sons.add(c);
		}
		else {
			sons.addAll(((ComplexCondition)c).sons);
		}
		
		
	}
	
	public void addPrevious(Node_new p) {
		
		this.previous.add(p);
	}
	
	public void addNext(Node_new p) {
		
		this.next.add(p);
	}
	
	public Condition getNode() {
		
		return node;
	}
	
	public LinkedHashSet<Condition> getSons(){
		return this.sons;
	}
	
	public HashSet<Node_new> getPrev(){
		return previous;
	}
	
	public HashSet<Node_new> getNext(){
		return next;
	}
	

	
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer("[  ");
		
		for(Node_new p : previous) {
			sb.append(p.getNode()+", ");
			
		}
		
		sb.append("--->");
		
		sb.append(node);
		
		sb.append("--->");
		
		for(Node_new p: next) {
			
			sb.append(p.getNode()+", ");

		}
		
		sb.append("]  ");
		
		return sb.toString();
	}

}
