import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.hstairs.ppmajal.conditions.ComplexCondition;
import com.hstairs.ppmajal.conditions.Predicate;
import com.hstairs.ppmajal.problem.GroundAction;

public class GoalRecognition {

	public static void main(String[] args) {

	}

	public static void computeAchievedLandmarksInObservations(HashSet<Predicate> init,
			HashSet<ComplexCondition> candidateGoals, ArrayList<GroundAction> observations,
			HashMap<ComplexCondition, LGG> candidateGoalsPlusLMs) {

		HashMap<ComplexCondition, HashSet<Predicate>> goalsPlusAchievedLMs = new HashMap<ComplexCondition, HashSet<Predicate>>();

		// for each goal G in G do
		for (ComplexCondition goal : candidateGoals) {

			LGG LG = candidateGoalsPlusLMs.get(goal);		
			HashSet<Predicate> LI = (HashSet<Predicate>) init.clone();
			LI.retainAll(LG.getPredicates());
			HashSet<Node> L = new HashSet<Node>();
			
			
			HashSet<Predicate> AL = new HashSet<Predicate>();

			// for each observed action o in O do
			for (GroundAction observation : observations) {

				HashSet<Predicate> prePlusAdd = new HashSet<Predicate>();
				prePlusAdd.addAll(observation.getPreconditions().getInvolvedPredicates());
				prePlusAdd.addAll(observation.getAddList().getInvolvedPredicates());

				// Set L
				for (Node node : LG.getNodes()) {

					if (prePlusAdd.contains(node.getNode())) {
						L.add(node);
					}
				}

				// Set Predecessors L
				HashSet<Predicate> Lpred = new HashSet<Predicate>();

				for (Node node : L) {

					Lpred.addAll(LG.getAllPredecessors(node));
				}

				// Set achieved LMs for goal				
				AL.addAll(LI);
				for (Node node : L) {
					AL.add(node.getNode());
				}
				AL.addAll(Lpred);			

			}			
			
			goalsPlusAchievedLMs.put(goal, AL);		

		}

	}

}
