import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.hstairs.ppmajal.conditions.ComplexCondition;
import com.hstairs.ppmajal.conditions.Predicate;
import com.hstairs.ppmajal.problem.GroundAction;

public class GoalRecognition {

	public static HashSet<Predicate> SA_Facts = new HashSet<Predicate>();
	public static HashSet<Predicate> UA_Facts = new HashSet<Predicate>();
	public static HashSet<Predicate> ST_Facts = new HashSet<Predicate>();

	public static void main(String[] args) {

	}

	public static HashMap<ComplexCondition, HashSet<Predicate>> computeAchievedLandmarksInObservations(
			HashSet<Predicate> init, HashSet<ComplexCondition> candidateGoals, ArrayList<GroundAction> observations,
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

		return goalsPlusAchievedLMs;

	}

	public static void partitionFacts(HashSet<GroundAction> actions, HashSet<Predicate> initialFacts) {

		HashSet<Predicate> SA = new HashSet<Predicate>();
		HashSet<Predicate> UA = new HashSet<Predicate>();
		HashSet<Predicate> ST = new HashSet<Predicate>();

		HashSet<Predicate> allAddFacts = new HashSet<Predicate>();
		HashSet<Predicate> allPreCondFacts = new HashSet<Predicate>();
		HashSet<Predicate> allDelFacts = new HashSet<Predicate>();

		boolean prep_ST_facts = false;

		// find Strictly Activating + prepare Unstable Activating and Strictly Terminal
		for (Predicate p : initialFacts) {

			boolean f_notElement_eff = true;
			boolean f_notElement_add = true;
			boolean f_element_pre = false;

			for (GroundAction a : actions) {

				HashSet<Predicate> add = (HashSet<Predicate>) a.getAddList().getInvolvedPredicates();
				HashSet<Predicate> del = (HashSet<Predicate>) a.getDelList().getInvolvedPredicates();
				HashSet<Predicate> eff = new HashSet<Predicate>();
				eff.addAll(add);
				eff.addAll(del);

				if (!f_notElement_eff && !f_notElement_add) {
					break;
				}

				if (eff.contains(p)) {
					f_notElement_eff = false;
				}

				if (add.contains(p)) {
					f_notElement_add = false;
				}

				if (f_element_pre == false && a.getPreconditions().getInvolvedPredicates().contains(p)) {
					f_element_pre = true;
				}
				
				//prepare ST facts
				if (prep_ST_facts == false) {

					allAddFacts.addAll(add);
					allPreCondFacts.addAll(a.getPreconditions().getInvolvedPredicates());
					allDelFacts.addAll(del);

				}

			}
			
			prep_ST_facts = true;

			if (f_notElement_eff && f_element_pre) {
				SA.add(p);
			}

			if (f_notElement_add) {
				UA.add(p);
			}

		}

		SA_Facts = SA;

		// find Unstable Activating
		for (Predicate p : UA) {

			HashSet<GroundAction> f_element_pre = new HashSet<GroundAction>();
			HashSet<GroundAction> f_element_del = new HashSet<GroundAction>();

			for (GroundAction a : actions) {

				if (a.getPreconditions().getInvolvedPredicates().contains(p)) {
					f_element_pre.add(a);
				}

				if (a.getDelList().getInvolvedPredicates().contains(p)) {
					f_element_del.add(a);
				}
			}

			if (!CollectionUtils.disjunction(f_element_pre, f_element_del).isEmpty()) {
				UA_Facts.add(p);
			}

		}

		// find Strictly Terminal		
		actions.removeAll(allDelFacts);
		actions.removeAll(allPreCondFacts);
		allAddFacts.retainAll(actions);
		
		if(!allAddFacts.isEmpty()) {
			ST_Facts.addAll(allAddFacts);
		}		

	}

}
