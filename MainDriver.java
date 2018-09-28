import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class MainDriver {

	public static ArrayList<Character> list = new ArrayList<Character>();
	public static final String GOAL_STATE = "012345678";
	public static final Puzzle PUZZLE_SOLVER = new Puzzle(GOAL_STATE, 0, 0, null);
	public static Scanner input = new Scanner(System.in);

	public static void main(String[] args) {
		
		for (char c : GOAL_STATE.toCharArray()) {
			list.add(c);
		}

		int choice = -1;
		while (choice != 3) {
			promptPuzzle();
			choice = getInput();
			performOptions(choice);
		}
	}

	// given user-input
	public static void performOptions(int choice) {
		String configuration = "";
		int input = -1;

		switch (choice) {
		case 1: // single random
			configuration = makeRandomPuzzle();
			System.out.println("Random Puzzle: " + configuration);
			singleSolve(input, configuration);
			break;
		case 2: // single user input
			System.out.println("Enter a 8 puzzle String: ");
			configuration = MainDriver.input.nextLine();
			if (!isSolvable(configuration)) {
				System.out.println("Invalid puzzle");
			} else {
				System.out.println("User Puzzle: " + configuration);
				singleSolve(input, configuration);
			}
			break;
    case 3: // exit
			System.out.println("Exiting...");
			break;
		default:
			System.out.println("Invalid Input");
			break;
		}
	}
	
	public static boolean isSolvable(String state) {
		// input validation
		if (state.length() != 9 || !state.matches("[0-9]+")) {
			return false;
		}

		// more validation
		int counter = 0;
		for (int a = 0; a < state.length(); a++) {
			int value = state.charAt(a) - '0';
			if ((counter & (1 << value)) > 0) {
				return false;
			} else {
				counter |= (1 << value);
			}
		}

		// check inversions
		int inversions = 0;
		char[] arr = state.replace("0", "").toCharArray();

	
		for (int i = 0; i < arr.length; i++) {
			for (int k = i + 1; k < arr.length; k++) {
				if (arr[i] < arr[k]) {
					inversions++;
				}
			}
		}

		// Odd # of inversions is unsolvable
		if (inversions % 2 == 1) {
			return false;
		}

		return true;
	}

	// Generate a random solvable puzzle.
	public static String makeRandomPuzzle() {
		String configuration = "";
		do {
			Collections.shuffle(list);
			configuration = "";
			for (Character c : list) {
				configuration += c;
			}
		} while (!isSolvable(configuration));
		return configuration;
	}

	// Choose between the two heuristics for a solution.
	public static SolutionData solve(int input, String configuration) {
		SolutionData solution = null;
		
		if (input == 1) {
			// Heuristic 1
			solution = PUZZLE_SOLVER.solve(configuration, 1);
		} else {
			// Heuristic 2
			solution = PUZZLE_SOLVER.solve(configuration, 2);
		}
		return solution;
	}

	// Produce a single solution result with statistics.
	public static void singleSolve(int input, String configuration) {
		promptSolution();
		while (input == -1) {
			input = getInput();
		}

		SolutionData solution = solve(input, configuration);

		// Visually display each change in state from initial to final
		for (String state : solution.path) {
			System.out.println(PUZZLE_SOLVER.print(state));
		}
		System.out.println("Number of steps/depth: " + (solution.depth));
		System.out.println("Time(ms) taken: " + (solution.timeElapsed));
		System.out.println("Search Cost: " + (solution.searchCost));
	}
	
	public static int getInput() {
		try {
			return Integer.parseInt(input.nextLine());
		} catch (NumberFormatException e) {
			return -1;
		}
	}
		
	public static void promptSolution() {
		System.out.println("(1) Solve using heuristic 1");
		System.out.println("(2) Solve using heuristic 2");
	}

	public static void promptPuzzle() {
		System.out.println("(1) Randomly genereated Puzzle");
		System.out.println("(2) User defined Puzzle");
    System.out.println("(3) Exit Program");
	}
}
