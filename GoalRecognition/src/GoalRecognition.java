import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.collections4.multiset.HashMultiSet;

import com.hstairs.ppmajal.conditions.AndCond;
import com.hstairs.ppmajal.conditions.ComplexCondition;
import com.hstairs.ppmajal.conditions.Condition;
import com.hstairs.ppmajal.conditions.OrCond;
import com.hstairs.ppmajal.conditions.Predicate;
import com.hstairs.ppmajal.problem.GroundAction;

public class GoalRecognition {

	public static HashSet<Predicate> SA_Facts = new HashSet<Predicate>();
	public static HashSet<Predicate> UA_Facts = new HashSet<Predicate>();
	public static HashSet<Predicate> ST_Facts = new HashSet<Predicate>();



	public static HashMap<ComplexCondition, Double> recognizeGoals(ArrayList<GroundAction> observations,
			String domainFile, HashSet<String> problemFiles) throws Exception {
		// assume different Problems as candidate Goals with same initial state

		HashMap<ComplexCondition, Double> recognizedGoals = new HashMap<ComplexCondition, Double>();

		HashSet<ComplexCondition> candidateGoals = new HashSet<ComplexCondition>();
		HashMap<ComplexCondition, LGG> candidateGoalsPlusLMs = new HashMap<ComplexCondition, LGG>();

		boolean initialStateSet = false;
		HashSet<Predicate> initialState = new HashSet<Predicate>();

		for (String problem : problemFiles) {

			LandmarkExtraction le = new LandmarkExtraction();
			le.computeLandmarks(domainFile, problem);

			candidateGoals.add(le.problem.getGoals());
			candidateGoalsPlusLMs.put(le.problem.getGoals(), le.lgg);

			if (!initialStateSet) {

				for (Predicate p : le.problem.getPredicatesInvolvedInInit()) {
					System.out.println("ADDING TO INITIAL STATE................................" + p);
					initialState.add(p);
				}
				initialStateSet = true;
			}

		}

		HashMap<ComplexCondition, HashSet<Predicate>> achievedLMsInObservations = computeAchievedLandmarksInObservations(
				initialState, candidateGoals, observations, candidateGoalsPlusLMs);

		System.out.println("Achieved Landmarks in Observations:");
		for (Entry<ComplexCondition, HashSet<Predicate>> e : achievedLMsInObservations.entrySet()) {

			System.out.println("Goal: " + e.getKey());
			System.out.println("Achieved LMs: " + e.getValue());
		}

		for (Entry<ComplexCondition, HashSet<Predicate>> entry : achievedLMsInObservations.entrySet()) {

			LGG lgg = candidateGoalsPlusLMs.get(entry.getKey());

			double sumCompletionOfSubGoals = 0;

			for (Predicate subGoal : entry.getKey().getInvolvedPredicates()) {

				HashSet<Predicate> LMsOfSubGoal = lgg.getAllPredecessors(lgg.getNodeFromPredicate(subGoal));

				double denominator = LMsOfSubGoal.size() + 1;

				LMsOfSubGoal.retainAll(entry.getValue());

				double numerator = LMsOfSubGoal.size();

				sumCompletionOfSubGoals += (numerator / denominator);
				System.out.println(numerator + " out of " + denominator + "-------------------------");

			}

			System.out.println("Sum Completion of Subgoal: " + sumCompletionOfSubGoals);
			System.out.println("Number of Subgoals: " + entry.getKey().getInvolvedPredicates().size());

			double h_gc = sumCompletionOfSubGoals / entry.getKey().getInvolvedPredicates().size();

			recognizedGoals.put(entry.getKey(), h_gc);

		}

		return recognizedGoals;

	}

	public static HashMap<ComplexCondition, Double> recognizeGoals_new(ArrayList<GroundAction> observations,
			String domainFile, HashSet<String> problemFiles) throws Exception {
		// assume different Problems as candidate Goals with same initial state

		HashMap<ComplexCondition, Double> recognizedGoals = new HashMap<ComplexCondition, Double>();

		HashSet<ComplexCondition> candidateGoals = new HashSet<ComplexCondition>();
		HashMap<ComplexCondition, LGG_new> candidateGoalsPlusLMs = new HashMap<ComplexCondition, LGG_new>();

		boolean initialStateSet = false;
		LinkedHashSet<Predicate> initialState = new LinkedHashSet<Predicate>();

		for (String problem : problemFiles) {

			LandmarkExtraction le = new LandmarkExtraction();
			le.computeLandmarks_new(domainFile, problem);

			candidateGoals.add(le.problem.getGoals());
			candidateGoalsPlusLMs.put(le.problem.getGoals(), le.lgg_new);

			if (!initialStateSet) {

				for (Predicate p : le.problem.getPredicatesInvolvedInInit()) {
					initialState.add(p);
				}
				initialStateSet = true;
			}

		}

		HashMap<ComplexCondition, HashSet<Condition>> achievedLMsInObservations = computeAchievedLandmarksInObservations_new(
				initialState, candidateGoals, observations, candidateGoalsPlusLMs);

//		System.out.println("Achieved Landmarks in Observations:------------------------");
//		for (Entry<ComplexCondition, HashSet<Condition>> e : achievedLMsInObservations.entrySet()) {
//
//			System.out.println("Goal: " + e.getKey());
//			System.out.println("Achieved LMs: " + e.getValue());
//		}

		for (Entry<ComplexCondition, HashSet<Condition>> entry : achievedLMsInObservations.entrySet()) {

			LGG_new lgg = candidateGoalsPlusLMs.get(entry.getKey());

			double sumCompletionOfSubGoals = 0;
			
			//System.out.println("Candidate Goal: " + entry.getKey());


			for (Condition subGoal : (Collection<Condition>) (entry.getKey().sons)) {
				

				MultiValuedMap<Integer, Node_new> LMsOfSubGoal = new ArrayListValuedHashMap<>();
				
				

				lgg.getAllPredecessors(lgg.getNodeFromCond(subGoal), LMsOfSubGoal);


				double denominator = LMsOfSubGoal.keySet().size() + 1;

				MultiSet<Integer> achievedLMSets = new HashMultiSet<Integer>();

				boolean subgoalAchieved = false;
				
				for (Condition c : entry.getValue()) {
					
					if (!subgoalAchieved && subGoal.equals(c)) {
						achievedLMSets.add(0);
						subgoalAchieved = true;
					}
					
					//System.out.println("SUBGOAL:"+c);
					
					for (Entry<Integer, Node_new> n : LMsOfSubGoal.entries()) {

						if (n.getValue().getNode().equals(c)) {
							// System.out.println("ACHIEVED "+c);
							achievedLMSets.add(n.getKey());
						}

						
						
					}
				}

//				System.out.println("LMS OF SUBGOAL:");
//
//				for (Entry<Integer, Node_new> n : LMsOfSubGoal.entries()) {
//					System.out.println(n.getKey()+"---"+n.getValue().getNode());
//				}
				
				double numerator = 0;
				
				MultiSet<Integer> allLMSets = new HashMultiSet<Integer>();
				allLMSets.addAll(LMsOfSubGoal.keys());
				allLMSets.add(0);
				
//				System.out.println("ACHIEVED LM SETS:");
//				System.out.println(achievedLMSets);
				
				for(org.apache.commons.collections4.MultiSet.Entry<Integer> LMSetEntry : allLMSets.entrySet()) {
					for(org.apache.commons.collections4.MultiSet.Entry<Integer> achievedLMSetEntry : achievedLMSets.entrySet()) {
						
						if(LMSetEntry.getElement() == achievedLMSetEntry.getElement() && LMSetEntry.getCount() == achievedLMSetEntry.getCount()) {
							
							numerator++;
						}					
					}

				}


				sumCompletionOfSubGoals += (numerator / denominator);
				System.out.println(subGoal+": "+numerator + " out of " + denominator + "-----------");

			}

//			System.out.println("Sum Completion of Subgoal: " + sumCompletionOfSubGoals);
//			System.out.println("Number of Subgoals: " + entry.getKey().sons.size());

			double h_gc = sumCompletionOfSubGoals / entry.getKey().sons.size();

			recognizedGoals.put(entry.getKey(), h_gc);

		}

		return recognizedGoals;

	}

	public static HashMap<ComplexCondition, HashSet<Predicate>> computeAchievedLandmarksInObservations(
			HashSet<Predicate> initialState, HashSet<ComplexCondition> candidateGoals,
			ArrayList<GroundAction> observations, HashMap<ComplexCondition, LGG> candidateGoalsPlusLMs) {

		HashMap<ComplexCondition, HashSet<Predicate>> goalsPlusAchievedLMs = new HashMap<ComplexCondition, HashSet<Predicate>>();

		// for each goal G in G do
		for (ComplexCondition goal : candidateGoals) {

			LGG LG = candidateGoalsPlusLMs.get(goal);
			HashSet<Predicate> LI = (HashSet<Predicate>) initialState.clone();
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

	public static HashMap<ComplexCondition, HashSet<Condition>> computeAchievedLandmarksInObservations_new(
			LinkedHashSet<Predicate> initialState, HashSet<ComplexCondition> candidateGoals,
			ArrayList<GroundAction> observations, HashMap<ComplexCondition, LGG_new> candidateGoalsPlusLMs) {

		HashMap<ComplexCondition, HashSet<Condition>> goalsPlusAchievedLMs = new HashMap<ComplexCondition, HashSet<Condition>>();

		// for each goal G in G do
		for (ComplexCondition goal : candidateGoals) {

			LGG_new LG = candidateGoalsPlusLMs.get(goal);
			HashSet<Condition> LI = new HashSet<Condition>();

			for (Node_new node : LG.getNodes()) {

				Condition n = node.getNode();

				if (n instanceof Predicate) {

					if (initialState.contains(n)) {
						LI.add(n);
					}

				} else if (n instanceof AndCond) {
					if (initialState.containsAll(((AndCond) n).sons)) {
						LI.add(n);
					}
				} else if (n instanceof OrCond) {

					if (!Collections.disjoint(initialState, ((OrCond) n).sons)) {
						LI.add(n);
					}

				} else {
					System.out.println("Unsupported Condition Type");
				}

			}

			HashSet<Node_new> L = new HashSet<Node_new>();

			HashSet<Condition> AL = new HashSet<Condition>();

			// for each observed action o in O do
			for (GroundAction observation : observations) {

				HashSet<Condition> prePlusAdd = new HashSet<Condition>();
				prePlusAdd.addAll(observation.getPreconditions().sons);
				prePlusAdd.addAll(observation.getAddList().sons);

				// Set L
				for (Node_new node : LG.getNodes()) {

					Condition n = node.getNode();

					if (n instanceof Predicate) {

						if (prePlusAdd.contains(n)) {
							L.add(node);
						}

					} else if (n instanceof AndCond) {

						if (prePlusAdd.containsAll(((AndCond) n).sons)) {
							L.add(node);
						}

					} else if (n instanceof OrCond) {

						if (!Collections.disjoint(prePlusAdd, ((OrCond) n).sons)) {
							L.add(node);
						}

					} else {

						System.out.println("Unsupported Condition Type");
					}

				}

				// Set Predecessors L
				HashSet<Node_new> Lpred = new HashSet<Node_new>();

				for (Node_new node : L) {

					MultiValuedMap<Integer, Node_new> tmpLpred = new ArrayListValuedHashMap<>();
					LG.getAllPredecessors(node, tmpLpred);

					for (Node_new prevv : tmpLpred.values()) {
						Lpred.add(prevv);
					}

				}

				// Set achieved LMs for goal
				AL.addAll(LI);
				for (Node_new node : L) {
					AL.add(node.getNode());
				}
				for (Node_new node : Lpred) {
					AL.add(node.getNode());
				}
			}

			goalsPlusAchievedLMs.put(goal, AL);

		}

		return goalsPlusAchievedLMs;

	}

	public static void partitionFacts(HashSet<GroundAction> actions, HashSet<Predicate> initialFacts) {

		HashSet<Predicate> SA = new HashSet<Predicate>();
		HashSet<Predicate> UA = new HashSet<Predicate>();

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

				// prepare ST facts
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

			boolean foundActions = false;

			for (GroundAction pre : f_element_pre) {
				for (GroundAction del : f_element_del) {

					if (pre != del) {
						UA_Facts.add(p);
						foundActions = true;
						break;
					}
				}

				if (foundActions) {
					break;
				}
			}
		}

		// find Strictly Terminal
		// TODO works like this? KEK
		allAddFacts.removeAll(allDelFacts);
		allAddFacts.removeAll(allPreCondFacts);

		if (!allAddFacts.isEmpty()) {
			ST_Facts.addAll(allAddFacts);
		}

		System.out.println("--------------------");
		System.out.println("Fact Partitioning:");
		System.out.println("Strictly Activating Facts: " + SA_Facts.size());
		System.out.println("Unstable Activating Facts: " + UA_Facts.size());
		System.out.println("Strictly Terminal Facts: " + ST_Facts.size());
		System.out.println("--------------------");

	}

	public static HashMap<Integer, HashSet<Predicate>> partitionFactsAndGoals(LGG lgg) {

		HashMap<Integer, HashSet<Predicate>> factPartitioning = new HashMap<Integer, HashSet<Predicate>>();

		HashSet<Predicate> SA_tmp = (HashSet<Predicate>) SA_Facts.clone();
		HashSet<Predicate> UA_tmp = (HashSet<Predicate>) UA_Facts.clone();
		HashSet<Predicate> ST_tmp = (HashSet<Predicate>) ST_Facts.clone();

		SA_tmp.retainAll(lgg.getPredicates());
		UA_tmp.retainAll(lgg.getPredicates());
		ST_tmp.retainAll(lgg.getPredicates());

		factPartitioning.put(1, SA_tmp);
		factPartitioning.put(2, UA_tmp);
		factPartitioning.put(3, ST_tmp);

		return factPartitioning;
	}

	// Assume LMs for candidate goals are already given -> candidateGoalsPlusLMs
	public static HashMap<ComplexCondition, Double> filterCandidateGoalsInObservations(HashSet<Predicate> initialState,
			HashSet<ComplexCondition> candidateGoals, ArrayList<GroundAction> observations,
			HashMap<ComplexCondition, LGG> candidateGoalsPlusLMs, double threshold, HashSet<GroundAction> actions) {

		partitionFacts(actions, initialState);

		HashMap<ComplexCondition, Double> goalAndAchievedLMsPercentage = new HashMap<ComplexCondition, Double>();

		for (Entry<ComplexCondition, LGG> entry : candidateGoalsPlusLMs.entrySet()) {

			HashMap<Integer, HashSet<Predicate>> factPartitioning = partitionFactsAndGoals(entry.getValue());

			HashSet<Predicate> SA_tmp = factPartitioning.get(1);
			HashSet<Predicate> UA_tmp = factPartitioning.get(2);
			HashSet<Predicate> ST_tmp = factPartitioning.get(3);

			SA_tmp.retainAll(initialState);

			// Goal is no longer possible
			if (SA_tmp.isEmpty()) {
				continue;
			}

			initialState.retainAll(entry.getValue().getPredicates());

			// LMs already present in initial State
			HashSet<Predicate> initialLMs = initialState;

			HashSet<Predicate> achievedLMs = new HashSet<Predicate>();

			boolean discardG = false;

			for (GroundAction oA : observations) {

				HashSet<Predicate> UAplusST = new HashSet<Predicate>();
				UAplusST.addAll(UA_tmp);
				UAplusST.addAll(ST_tmp);

				HashSet<Predicate> PrePlusEff = new HashSet<Predicate>();
				HashSet<Predicate> PrePlusAdd = new HashSet<Predicate>();

				PrePlusEff.addAll(oA.getPreconditions().getInvolvedPredicates());
				PrePlusEff.addAll(oA.getAddList().getInvolvedPredicates());
				PrePlusEff.addAll(oA.getDelList().getInvolvedPredicates());
				PrePlusAdd.addAll(oA.getPreconditions().getInvolvedPredicates());
				PrePlusAdd.addAll(oA.getAddList().getInvolvedPredicates());

				if (Collections.disjoint(UAplusST, PrePlusEff) == false) {

					discardG = true;
					break;

				} else {

					HashSet<Predicate> L = (HashSet<Predicate>) entry.getValue().getPredicates().clone();

					L.removeAll(oA.getDelList().getInvolvedPredicates());

					for (Predicate p : entry.getValue().getPredicates()) {

						if (PrePlusAdd.contains(p)) {
							L.add(p);
						}

					}

					HashSet<Predicate> L_pred = new HashSet<Predicate>();

					for (Predicate p : L) {

						L_pred.addAll(entry.getValue().getAllPredecessors(entry.getValue().getNodeFromPredicate(p)));

					}

					achievedLMs.addAll(L);
					achievedLMs.addAll(L_pred);
					achievedLMs.addAll(initialLMs);

				}

			}

			if (discardG) {
				// TODO
				// break hier richtig? eher continue
				break;
			}

			double percentageOfAchievedLMs = achievedLMs.size() / (((LGG) entry.getValue()).getPredicates()).size();

			goalAndAchievedLMsPercentage.put((ComplexCondition) entry.getKey(), percentageOfAchievedLMs);

		}

		double maxPercentage = 0;

		for (Entry<ComplexCondition, Double> entry : goalAndAchievedLMsPercentage.entrySet()) {

			if (entry.getValue() > maxPercentage) {
				maxPercentage = entry.getValue();
			}
		}

		HashMap<ComplexCondition, Double> result = new HashMap<ComplexCondition, Double>();

		for (Entry<ComplexCondition, Double> entry : goalAndAchievedLMsPercentage.entrySet()) {

			if (entry.getValue() > (maxPercentage - threshold)) {
				result.put(entry.getKey(), entry.getValue());
			}
		}

		return result;

	}

}
