import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.hstairs.ppmajal.conditions.ComplexCondition;
import com.hstairs.ppmajal.problem.GroundAction;

public class Main {

	public static void main(String[] args) throws Exception {
		
		
//		 ------------------------ BLOCKSWORLD EXAMPLE COMPLETE ------------------------
		
		
		LandmarkExtraction le1 = new LandmarkExtraction();
		LandmarkExtraction le2 = new LandmarkExtraction();
		LandmarkExtraction le3 = new LandmarkExtraction();

		String domainFile = "./resources/blocksworld/domain.pddl";
		String problemFile1 = "./resources/blocksworld/problemRED.pddl";
		String problemFile2 = "./resources/blocksworld/problemBED.pddl";
		String problemFile3 = "./resources/blocksworld/problemSAD.pddl";

		le1.computeLandmarks_new(domainFile, problemFile1);
		le2.computeLandmarks_new(domainFile, problemFile2);
		le3.computeLandmarks_new(domainFile, problemFile3);
		

		ArrayList<GroundAction> observations = new ArrayList<GroundAction>();

		int i = 0;
		for (Entry e : le1.action_levels.entries()) {

			GroundAction gr = (GroundAction) e.getValue();
			if (i == 0 || i == 3) {
				observations.add(gr);
			}
			i++;
		}
		
		System.out.println("-------------- BLOCKSWORLD EXAMPLE --------------");

		System.out.println();
		System.out.println("OBSERVATIONS:");
		
		for(GroundAction gr : observations) {
			System.out.println(gr.getName()+" "+gr.getParameters());
		}
		
		System.out.println("--------------------");
		System.out.println();


		System.out.println("LGG for RED:");

		for (Node_new n : le1.lgg_new.getNodes()) {

			System.out.println(n);
		}
		System.out.println("--------------------");


		System.out.println();
		System.out.println("LANDMARK CANDIDATES for RED:");
		System.out.println(le1.landmarkCandidates_new);

		System.out.println("LANDMARKS for RED:");
		System.out.println(le1.landmarks_new);
		System.out.println();


		HashSet<ComplexCondition> candidateGoals = new HashSet<ComplexCondition>();
		candidateGoals.add(le1.problem.getGoals());
		candidateGoals.add(le2.problem.getGoals());
		candidateGoals.add(le3.problem.getGoals());

		HashMap<ComplexCondition, LGG_new> candidateGoalsPlusLMs = new HashMap<ComplexCondition, LGG_new>();

		candidateGoalsPlusLMs.put(le1.problem.getGoals(), le1.lgg_new);
		candidateGoalsPlusLMs.put(le2.problem.getGoals(), le2.lgg_new);
		candidateGoalsPlusLMs.put(le3.problem.getGoals(), le3.lgg_new);

		
		System.out.println("--------------------");

		HashSet<String> problemFiles = new HashSet<String>();
		problemFiles.add(problemFile1);
		problemFiles.add(problemFile2);
		problemFiles.add(problemFile3);

		HashMap<ComplexCondition, Double> recognizedGoals = GoalRecognition.recognizeGoals_new(observations,
				le1.domainFile, problemFiles);

		
		System.out.println();
		System.out.println("RECOGNIZED GOALS: ");
		System.out.println();
		for (Entry<ComplexCondition, Double> e : recognizedGoals.entrySet()) {
			System.out.println("GOAL: " + e.getKey());
			System.out.println("COMPLETION: " + e.getValue());
			System.out.println("-----------------------");
		}
		
		
		
//		------------------------ KITCHEN EXAMPLE LANDMARK EXTRACTION ------------------------
		
		System.out.println("\n\n\n\n\n\n\n");
		System.out.println("-------------- KITCHEN EXAMPLE --------------");

		
		LandmarkExtraction le4 = new LandmarkExtraction();
		

		String domainFileKitchen = "./resources/kitchen_example/FIdomain.pddl";
		String problemFileKitchen = "./resources/kitchen_example/FIproblem.pddl";

		le4.computeLandmarks_new(domainFileKitchen, problemFileKitchen);
	
	

		System.out.println();
		System.out.println("LGG");

		for (Node_new n : le4.lgg_new.getNodes()) {

			System.out.println(n);
		}
		System.out.println("--------------------");


		System.out.println();
		System.out.println("LANDMARK CANDIDATES");
		System.out.println(le4.landmarkCandidates_new);

		System.out.println("LANDMARKS for RED:");
		System.out.println(le4.landmarks_new);
		
	}

}
