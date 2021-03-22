
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import com.hstairs.ppmajal.conditions.Predicate;
import com.hstairs.ppmajal.domain.PddlDomain;
import com.hstairs.ppmajal.problem.EPddlProblem;
import com.hstairs.ppmajal.problem.GroundAction;
import com.hstairs.ppmajal.problem.PDDLState;
import com.hstairs.ppmajal.some_computatitional_tool.NumericPlanningGraph;

public class LandmarkExtraction {

	public static PddlDomain domain;
	public static EPddlProblem problem;
	public static MultiValuedMap<Integer, GroundAction> actions;
	public static MultiValuedMap<Integer, Predicate> predicates;
	public static int levels;
	public static HashSet<Predicate> landmarkCandidates = new HashSet<Predicate>();
	public static HashSet<Predicate> landmarks = new HashSet<Predicate>();
	public static MultiValuedMap<Integer, Predicate> goals = new ArrayListValuedHashMap<>();
	public static RPG rpg;
	public static LGG lgg;
	public static String domainFile;
	public static String problemFile;

	public static LGG computeLandmarks(String dF, String pF) throws Exception {

		domainFile = dF;
		problemFile = pF;

		domain = new PddlDomain(domainFile);
		problem = new EPddlProblem(problemFile, domain.getConstants(), domain.types, domain);

		domain.substituteEqualityConditions();

		problem.transformGoal();
		problem.groundingActionProcessesConstraints();

		System.out.println("Simplification..");
		// problem.setAction_cost_from_metric(!ignore_metric);
		problem.simplifyAndSetupInit(true, false);

		System.out.println("Grounding and Simplification finished");
		System.out.println("|A|:" + problem.getActions().size());
		System.out.println("|P|:" + problem.getProcessesSet().size());
		System.out.println("|E|:" + problem.getEventsSet().size());
		System.out.println("Size(X):" + problem.getNumberOfNumericVariables());
		System.out.println("Size(F):" + problem.getNumberOfBooleanVariables());

		rpg = new RPG((PDDLState) problem.getInit());

		lgg = new LGG();
		lgg.initialize(problem.getGoals().getInvolvedPredicates());

		actions = rpg.computeRelaxedPlan(((PDDLState) problem.getInit()), problem.getGoals(),
				(Set) problem.getActions());

		levels = rpg.levels;

		generatePredicateSet();

		System.out.println("Predicates: " + predicates);

		generateLandmarkCandidates();

		System.out.println("LANDMARK CANDIDATES: ");
		System.out.println(landmarkCandidates);

		System.out.println("------------------------------------------");
		evaluateCandidates();

		System.out.println("LANDMARKS: ");
		System.out.println(landmarks);

		System.out.println("LGG: ");
		System.out.println(lgg.nodes);

		return lgg;

	}

	// TODO remove, only for testing purposes
	public static void main(String[] args) throws Exception {

//		domainFile = "./resources/biotope_domain_v2.pddl";
//		problemFile = "./resources/biotope_problem.pddl";
		
		domainFile = "./resources/kitchen_example/FIdomain.pddl";
		problemFile = "./resources/kitchen_example/FIproblem.pddl";
		

		domain = new PddlDomain(domainFile);
		problem = new EPddlProblem(problemFile, domain.getConstants(), domain.types, domain);

		domain.substituteEqualityConditions();

		problem.transformGoal();
		problem.groundingActionProcessesConstraints();

		System.out.println("Simplification..");
		// problem.setAction_cost_from_metric(!ignore_metric);
		problem.simplifyAndSetupInit(true, false);

		System.out.println("Grounding and Simplification finished");
		System.out.println("|A|:" + problem.getActions().size());
		System.out.println("|P|:" + problem.getProcessesSet().size());
		System.out.println("|E|:" + problem.getEventsSet().size());
		System.out.println("Size(X):" + problem.getNumberOfNumericVariables());
		System.out.println("Size(F):" + problem.getNumberOfBooleanVariables());

		rpg = new RPG((PDDLState) problem.getInit());

		lgg = new LGG();
		lgg.initialize(problem.getGoals().getInvolvedPredicates());

		actions = rpg.computeRelaxedPlan(((PDDLState) problem.getInit()), problem.getGoals(),
				(Set) problem.getActions());

		levels = rpg.levels;

		generatePredicateSet();

		System.out.println("Predicates: " + predicates);

		generateLandmarkCandidates();

		System.out.println("LANDMARK CANDIDATES: ");
		System.out.println(landmarkCandidates);

		System.out.println("------------------------------------------");
		evaluateCandidates();

		System.out.println("LANDMARKS: ");
		System.out.println(landmarks);

		System.out.println("LGG: ");
		System.out.println(lgg.nodes);

		Node[] nodess = new Node[6];
		int i = 0;
		for (Node nn : lgg.getNodes()) {
			nodess[i++] = nn;

		}

		System.out.println("...................................................");

		System.out.println("GET ALL PREDECESSORS FOR:" + nodess[3].getNode());

		System.out.println(lgg.getAllPredecessors(nodess[3]));

	}

	public static void generateLandmarkCandidates() {

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

								System.out.println("Adding node");

								lgg.addNode(p);
								lgg.addEdge(p, entryP.getValue());
							}
						}

						temp.putAll((entryP.getKey() - 1), temp2);

						System.out.println("Temp:" + temp);

						C_dash.putAll(temp);
						landmarkCandidates.addAll(temp.values());

					}
				}

			}

			C = C_dash;

		}

	}

	public static void generatePredicateSet() {

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

	public static void evaluateCandidates() throws CloneNotSupportedException {

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
}
