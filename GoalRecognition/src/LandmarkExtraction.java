
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.hstairs.ppmajal.conditions.AndCond;
import com.hstairs.ppmajal.conditions.ComplexCondition;
import com.hstairs.ppmajal.conditions.Condition;
import com.hstairs.ppmajal.conditions.OrCond;
import com.hstairs.ppmajal.conditions.Predicate;
import com.hstairs.ppmajal.domain.PddlDomain;
import com.hstairs.ppmajal.problem.EPddlProblem;
import com.hstairs.ppmajal.problem.GroundAction;
import com.hstairs.ppmajal.problem.PDDLState;
import com.hstairs.ppmajal.some_computatitional_tool.NumericPlanningGraph;

public class LandmarkExtraction {

	public PddlDomain domain;
	public EPddlProblem problem;
	public MultiValuedMap<Integer, GroundAction> actions;
	public MultiValuedMap<Integer, Predicate> predicates;
	public MultiValuedMap<Integer, GroundAction> action_levels = new ArrayListValuedHashMap<>();
	public MultiValuedMap<Integer, Condition> fact_levels = new ArrayListValuedHashMap<>();;
	public int levels;
	public HashSet<Predicate> landmarkCandidates = new HashSet<Predicate>();
	public HashSet<Condition> landmarkCandidates_new = new HashSet<Condition>();

	public HashSet<Predicate> landmarks = new HashSet<Predicate>();
	public HashSet<Condition> landmarks_new = new HashSet<Condition>();

	public MultiValuedMap<Integer, Predicate> goals = new ArrayListValuedHashMap<>();
	public MultiValuedMap<Integer, Condition> goals_new = new ArrayListValuedHashMap<>();

	public RPG rpg;
	public LGG lgg;
	public LGG_new lgg_new;
	public String domainFile;
	public String problemFile;

	public LGG computeLandmarks(String dF, String pF) throws Exception {

		domainFile = dF;
		problemFile = pF;

		domain = new PddlDomain(domainFile);
		problem = new EPddlProblem(problemFile, domain.getConstants(), domain.types, domain);

		domain.substituteEqualityConditions();

		problem.transformGoal();
		problem.groundingActionProcessesConstraints();

//		System.out.println("Simplification..");
		// problem.setAction_cost_from_metric(!ignore_metric);
		problem.simplifyAndSetupInit(false, false);

//		System.out.println("Grounding and Simplification finished");
//		System.out.println("|A|:" + problem.getActions().size());
//		System.out.println("|P|:" + problem.getProcessesSet().size());
//		System.out.println("|E|:" + problem.getEventsSet().size());
//		System.out.println("Size(X):" + problem.getNumberOfNumericVariables());
//		System.out.println("Size(F):" + problem.getNumberOfBooleanVariables());

		rpg = new RPG((PDDLState) problem.getInit());

		lgg = new LGG();
		lgg.initialize(problem.getGoals().getInvolvedPredicates());

		actions = rpg.computeRelaxedPlan(((PDDLState) problem.getInit()), problem.getGoals(),
				(Set) problem.getActions());

		levels = rpg.levels;

		generatePredicateSet();

//		System.out.println("Predicates: " + predicates);

		generateLandmarkCandidates();

//		System.out.println("LANDMARK CANDIDATES: ");
//		System.out.println(landmarkCandidates);
//
//		System.out.println("------------------------------------------");
		evaluateCandidates();

//		System.out.println("LANDMARKS: ");
//		System.out.println(landmarks);
//
//		System.out.println("LGG: ");
//		System.out.println(lgg.nodes);

		return lgg;

	}

	public LGG_new computeLandmarks_new(String dF, String pF) throws Exception {

		domainFile = dF;
		problemFile = pF;

		domain = new PddlDomain(domainFile);
		problem = new EPddlProblem(problemFile, domain.getConstants(), domain.types, domain);

		domain.substituteEqualityConditions();

		problem.transformGoal();
		problem.groundingActionProcessesConstraints();

		problem.simplifyAndSetupInit(false, false);

		createRPG();

		lgg_new = new LGG_new();
		lgg_new.initialize(problem.getGoals());

		generateLandmarkCandidates_new();

		evaluateCandidates_new();

		return lgg_new;

	}

	public void createRPG() throws CloneNotSupportedException {

		this.rpg = new RPG((PDDLState) this.problem.getInit());

		ArrayList[] relPlan = rpg.computeRelaxedPlan_new(((PDDLState) this.problem.getInit()), this.problem.getGoals(),
				(Set) this.problem.getActions());

		this.levels = rpg.goal_reached_at;

		for (int i = 0; i < relPlan.length; i++) {

			ArrayList<GroundAction> actionss = (ArrayList<GroundAction>) relPlan[i];

			for (GroundAction gr : actionss) {
				action_levels.put(i, gr);
			}

		}

		Iterable<Predicate> init = this.problem.getPredicatesInvolvedInInit();

		

		fact_levels.putAll(0, init);

		for (int i = 0; i <= levels; i++) {

			for (GroundAction gr : action_levels.get(i)) {

				fact_levels.putAll(i + 1, gr.getAddList().sons);
			}

//			if (i > 0) {
//				fact_levels.putAll(i, fact_levels.get(i - 1));
//			}
		}



		for (Condition c : (Collection<Condition>) problem.getGoals().sons) {

			for (int i = levels; i >= 0; i--) {

				HashSet<Condition> fact_level = new HashSet<Condition>();

				for (Condition cc : fact_levels.get(i)) {

					if (cc instanceof Predicate) {
						fact_level.add(cc);
					} else {
						fact_level.addAll(((ComplexCondition) cc).sons);
					}
				}

//				System.out.println("Fact Level " + i + " contains goal " + c + ": " + fact_level.contains(c));
//
//				
//				System.out.println("Fact level "+i+": "+fact_level);

				if (!fact_level.contains(c)) {

					goals_new.put(i + 1, c);
					break;
				}
			}
		}

	}

	

	public void generateLandmarkCandidates() {

		MultiValuedMap<Integer, Predicate> C = new ArrayListValuedHashMap<>();
		C = goals;

		MultiValuedMap<Integer, Predicate> C_dash;

		while (C.size() > 0) {

			C_dash = new ArrayListValuedHashMap<>();

			for (Entry<Integer, Predicate> entryP : C.entries()) {

				if (entryP.getKey() > 0) {

					MultiValuedMap<Integer, GroundAction> A = new ArrayListValuedHashMap<>();

					// let A be the set of all actions a such that L_dash is element of add(a), and
					// level(a) = level(L_dash) - 1
					for (Entry<Integer, GroundAction> entryA : actions.entries()) {

						if (entryA.getValue().getAddList().getInvolvedPredicates().contains(entryP.getValue())
								&& entryA.getKey() == (entryP.getKey() - 1)) {

							A.put(entryA.getKey(), entryA.getValue());
						}

					}

					// for all facts L such that for all a element A : L is element of pre(a)

					if (A.size() == 0) {

						System.out.println("A is empty");

					} else {

						MultiValuedMap<Integer, Predicate> temp = new ArrayListValuedHashMap<>();
						ArrayList<Predicate> temp2 = new ArrayList<Predicate>();
						boolean newList = true;

						for (Entry<Integer, GroundAction> a : A.entries()) {

							if (A.size() == 1) {

								temp2.addAll(a.getValue().getPreconditions().getInvolvedPredicates());

								break;
							} else {

								if (newList) {
									temp2.addAll(a.getValue().getPreconditions().getInvolvedPredicates());
									newList = false;
								} else {
									temp2.retainAll(a.getValue().getPreconditions().getInvolvedPredicates());
								}

							}

						}

						for (Predicate p : temp2) {

							if (!lgg.containsNode(p)) {

//								System.out.println("Adding node");

								lgg.addNode(p);
								lgg.addEdge(p, entryP.getValue());
							}
						}

						temp.putAll((entryP.getKey() - 1), temp2);

//						System.out.println("Temp:" + temp);

						C_dash.putAll(temp);
						landmarkCandidates.addAll(temp.values());

					}
				}

			}

			C = C_dash;

		}

	}

	public void generateLandmarkCandidates_new() {

		MultiValuedMap<Integer, Condition> C = new ArrayListValuedHashMap<>();
		
		for(Condition c : goals_new.values()) {
			C.put(this.getFactLevel(c), c);
		}

		MultiValuedMap<Integer, Condition> C_dash;

		int lmSetID = 1;

		while (C.size() > 0) {

			C_dash = new ArrayListValuedHashMap<>();

			for (Entry<Integer, Condition> entry : C.entries()) {
				
			//	System.out.println("Condition:"+ entry.getValue()+", Level:"+ entry.getKey());

				if (entry.getKey() > 0) {

					MultiValuedMap<Integer, GroundAction> A = new ArrayListValuedHashMap<>();

					// let A be the set of all actions a such that L_dash is element of add(a), and
					// level(a) = level(L_dash) - 1
					for (Entry<Integer, GroundAction> entryA : action_levels.entries()) {

						if (entry.getValue() instanceof Predicate) {

							if (entryA.getValue().getAddList().sons.contains(entry.getValue())
									&& entryA.getKey() == (entry.getKey() - 1)) {

								A.put(entryA.getKey(), entryA.getValue());
							}
						}
						// if AndCond
						else if (entry.getValue() instanceof AndCond) {

							if (entryA.getValue().getAddList().sons
									.containsAll(((ComplexCondition) entry.getValue()).sons)
									&& entryA.getKey() == (entry.getKey() - 1)) {

								A.put(entryA.getKey(), entryA.getValue());
							}

						} else if (entry.getValue() instanceof OrCond) {

							if (!Collections.disjoint(entryA.getValue().getAddList().sons,
									((ComplexCondition) entry.getValue()).sons)
									&& entryA.getKey() == (entry.getKey() - 1)) {

								A.put(entryA.getKey(), entryA.getValue());
							}

						} else {
							System.out.println("UNSUPPORTED FACT TYPE");
						}

					}

//					System.out.println("AAAAAA");
//					System.out.println(A);

					// for all facts L such that for all a element A : L is element of pre(a)
					
		//			System.out.println("Number of Actions:"+A.size());

					if (A.size() == 0) {

						// System.out.println("A is empty");

					} else {

						MultiValuedMap<Integer, Condition> temp = new ArrayListValuedHashMap<>();
						ArrayList<Condition> temp2 = new ArrayList<Condition>();
						boolean newList = true;

					//	System.out.println("ACTIONS:");
						for (Entry<Integer, GroundAction> a : A.entries()) {
							
						//	System.out.println(a.getKey()+":"+a.getValue().getName()+"-"+a.getValue().getParameters());

							if (A.size() == 1) {

								temp2.addAll(a.getValue().getPreconditions().sons);
								break;
							} else {

								if (newList) {
									temp2.addAll(a.getValue().getPreconditions().sons);
									newList = false;
								} else {
									temp2.retainAll(a.getValue().getPreconditions().sons);
								}

							}

						}

						for (Condition c : temp2) {

							if (!lgg_new.containsNode(c)) {

						//		 System.out.println("Adding node: " + c +" for top level goal "+ entry.getValue());
								
								 //TODO   WORK IN PROGRESS
								 temp.put(this.getFactLevel(c), c);

								lgg_new.addNode(c);

							}

							lgg_new.getNodeFromCond(c).addLmSetID(lmSetID);
					//		System.out.println("Adding edge: " + c + "--->" + entry.getValue());
							lgg_new.addEdge(c, entry.getValue(), lmSetID);
						}

						// temp.putAll((entry.getKey() - 1), temp2);

//						System.out.println("Temp:" + temp);

						C_dash.putAll(temp);
						landmarkCandidates_new.addAll(temp.values());

					}
				}

				lmSetID++;

			}

			C = C_dash;

		}

	}

	public void generatePredicateSet() {

		predicates = new ArrayListValuedHashMap<>();

		Iterable<Predicate> initialPredicates = problem.getPredicatesInvolvedInInit();

		for (Predicate p : initialPredicates) {

			predicates.put(0, p);
		}

		for (int i = 1; i <= levels; i++) {

			for (GroundAction a : actions.get(i - 1)) {

				predicates.putAll(i, a.getAddList().getInvolvedPredicates());

			}

			predicates.putAll(i, predicates.get(i - 1));
		}

		for (Predicate p : problem.getGoals().getInvolvedPredicates()) {

			for (int i = levels; i >= 0; i--) {

				if (!predicates.get(i).contains(p)) {

					goals.put(i + 1, p);
					break;
				}
			}
		}
	}

	public void evaluateCandidates_new() throws CloneNotSupportedException {

		Set<Predicate> initialFacts = (Set<Predicate>) problem.getPredicatesInvolvedInInit();

		for (Condition c : landmarkCandidates_new) {

			if (c instanceof Predicate) {
				if (initialFacts.contains(c)) {
					landmarks_new.add(c);
					continue;
				}

			} else {
				if (initialFacts.containsAll(((ComplexCondition) c).sons)) {
					landmarks_new.add(c);
					continue;
				}
			}

			HashSet<GroundAction> actionss = (HashSet<GroundAction>) ((HashSet<GroundAction>) problem.getActions())
					.clone();

			HashSet<GroundAction> temp = new HashSet<GroundAction>();

			// System.out.println("Action Size before: "+actionss.size());

			for (GroundAction ga : actionss) {

				if (c instanceof Predicate) {
					if (ga.getAddList().sons.contains(c)) {
						temp.add(ga);
						// System.out.println("Adding action to remove");
					}

				} else {

					if (ga.getAddList().sons.containsAll(((ComplexCondition) c).sons)) {
						temp.add(ga);
						// System.out.println("Adding action to remove---------");

					}

				}

			}

			actionss.removeAll(temp);
//			System.out.println("------------------------");
//			System.out.println("Fact " + c);
//			// System.out.println("Action Size after: "+actionss.size());
//			for (GroundAction a : temp) {
//				System.out.println("Removing " + a.getName() + a.getParameters());
//			}
//			System.out.println("------------------------");

			RPG rpg = new RPG((PDDLState) problem.getInit());
			// System.out.println("Task Status:");
			ArrayList[] list = rpg.computeRelaxedPlan_new(((PDDLState) problem.getInit()), problem.getGoals(),
					actionss);

//			System.out.println("PLAN without "+c);
//			if (list != null) {
//				for (int i = 0; i < list.length; i++) {
//					ArrayList<GroundAction> o = list[i];
//					
//					for(GroundAction a : o) {
//						System.out.println(a.getName()+a.getParameters());
//					}
//					
//				}
//				System.out.println("------");
//			}
			if (list == null) {

				landmarks_new.add(c);
			}
		}

		HashSet<Condition> notLMs = (HashSet<Condition>) landmarkCandidates_new.clone();
		notLMs.removeAll(landmarks_new);

		for (Condition c : notLMs) {

			lgg_new.removeNode(c);
		}
	}

	public void evaluateCandidates() throws CloneNotSupportedException {

		for (Predicate p : landmarkCandidates) {

			HashSet<GroundAction> actionss = (HashSet) problem.getActions();

			HashSet<GroundAction> temp = new HashSet<GroundAction>();

			for (GroundAction ga : actionss) {

				if (ga.getAddList().getInvolvedPredicates().contains(p)) {
					temp.add(ga);
				}
			}

			actionss.removeAll(temp);

			RPG rpg = new RPG((PDDLState) problem.getInit());

			if (rpg.computeRelaxedPlan(((PDDLState) problem.getInit()), problem.getGoals(), actionss) == null) {

				landmarks.add(p);
			}
		}

		HashSet<Predicate> notLMs = (HashSet<Predicate>) landmarkCandidates.clone();
		notLMs.removeAll(landmarks);

		HashSet<Node> nodesToRemove = new HashSet<Node>();

		for (Predicate p : notLMs) {
			for (Node n : lgg.getNodes()) {

				if (n.getNode().equals(p)) {

					nodesToRemove.add(n);

					// no predecessors or successors
					if (n.getPrev().isEmpty() && n.getNext().isEmpty()) {

						// fine

					}
					// no predecessors, only successors
					else if (n.getPrev().isEmpty() && !n.getNext().isEmpty()) {

						for (Predicate pred : n.getNext()) {
							for (Node node : lgg.getNodes()) {

								if (pred.equals(node.getNode())) {

									node.getPrev().remove(p);
								}

							}
						}

					}
					// no successors, only predecessors
					else if (!n.getPrev().isEmpty() && n.getNext().isEmpty()) {

						for (Predicate pred : n.getPrev()) {
							for (Node node : lgg.getNodes()) {

								if (pred.equals(node.getNode())) {

									node.getNext().remove(p);
								}
							}
						}

					}
					// in the middle of a chain, predecessors and successors
					else if (!n.getPrev().isEmpty() && !n.getNext().isEmpty()) {

						for (Node node : lgg.getNodes()) {

							for (Predicate prev : n.getPrev()) {

								if (node.getNode().equals(prev)) {

									node.getNext().remove(p);
									node.getNext().addAll(n.getNext());
								}

							}

							for (Predicate next : n.getNext()) {

								if (node.getNode().equals(next)) {

									node.getPrev().remove(p);
									node.getPrev().addAll(n.getPrev());
								}

							}

						}

					}

				}

			}
		}

		lgg.getNodes().removeAll(nodesToRemove);
	}
	
	public int getFactLevel(Condition c) {
		
	//	System.out.println("GET LEVEL OF "+c);
		for(int i=0;i<=levels;i++) {
			
			Collection<Condition> level = fact_levels.get(i);
		//	System.out.println("LEVEL:"+level);
			
			if(level.contains(c)) {
				return i;
			}
			
		}
		
		System.out.println("NOT FOUND");
		
		return 999;
	}
}
