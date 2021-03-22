import com.hstairs.ppmajal.conditions.*;
import com.hstairs.ppmajal.problem.GroundAction;
import com.hstairs.ppmajal.problem.PDDLState;
import com.hstairs.ppmajal.problem.RelState;

import java.util.*;
public class Node {
	
	HashSet<Predicate> previous = new HashSet<Predicate>();
	HashSet<Predicate> next = new HashSet<Predicate>();
	Predicate node;
	
	
	public Node(Predicate p) {
		this.node = p;
	}
	
	public Node(Predicate node, Predicate next) {
		
		this.node = node;
		this.next.add(next);
		
		
	}
	
	public void addPrevious(Predicate p) {
		
		this.previous.add(p);
	}
	
	public void addNext(Predicate p) {
		
		this.next.add(p);
	}
	
	public Predicate getNode() {
		
		return node;
	}
	
	public HashSet<Predicate> getPrev(){
		return previous;
	}
	
	public HashSet<Predicate> getNext(){
		return next;
	}
	

	
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer("[  ");
		
		for(Predicate p : previous) {
			sb.append(p+", ");
			
			
		}
		
		sb.append("--->");
		
		sb.append(node);
		
		sb.append("--->");
		
		for(Predicate p: next) {
			
			sb.append(p+", ");

		}
		
		sb.append("]  ");
		
		return sb.toString();
	}

}
