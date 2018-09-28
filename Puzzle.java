import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Puzzle implements Comparable<Puzzle> {

    private int emptyIndex;
    private int stepCost;
    private int estimatedCost;
    private String currentState;
    public final String GOAL_STATE = "012345678";
		
		//illegal index
    private final int[] UP = {0, 1, 2};
    private final int[] DOWN = {6, 7, 8};
    private final int[] LEFT = {0, 3, 6};
    private final int[] RIGHT = {2, 5, 8};
    private Puzzle previousPuzzle;

    public Puzzle(String current, int startingCost, int hCost, Puzzle p) {
        currentState = current;
        emptyIndex = current.indexOf('0');
        stepCost = startingCost;
        estimatedCost = hCost;
        previousPuzzle = p;
    }

    // return array of all legal neighbor states 
    public ArrayList<String> getValidMoves() {
        ArrayList<String> validMoves = new ArrayList<>();

        // Up case
        if (!ifContains(UP, (emptyIndex))) {
            validMoves.add(swapString(currentState, emptyIndex, emptyIndex - 3));
        }
        // Down case
        if (!ifContains(DOWN, (emptyIndex))) {
            validMoves.add(swapString(currentState, emptyIndex, emptyIndex + 3));
        }
        // Left case
        if (!ifContains(LEFT, (emptyIndex))) {
            validMoves.add(swapString(currentState, emptyIndex, emptyIndex - 1));
        }
        // Right case
        if (!ifContains(RIGHT, (emptyIndex))) {
            validMoves.add(swapString(currentState, emptyIndex, emptyIndex + 1));
        }

        return validMoves;
    }

    // heuristic 1
    public int misplacedTiles(String state) {
        char[] goal = GOAL_STATE.toCharArray();
        char[] current = state.toCharArray();

        int mismatchTiles = 0;

        for (int i = 0; i < goal.length; i++) {
            if (goal[i] != current[i]) {
                mismatchTiles++;
            }
        }

        return mismatchTiles;
    }

    // heuristic 2
    public int sumOfTheDistances(String state) {
        char[] current = state.toCharArray();

        int sumOfMoves = 0;

        for (int i = 0; i < current.length; i++) {
            sumOfMoves += movesFromSolution(i, Integer.parseInt("" + current[i]));
        }

        return sumOfMoves;
    }

    // calculate the coordinate distances.
    public int movesFromSolution(int currentPosition, int value) {
        int goalPosition = GOAL_STATE.indexOf("" + value);

        int x = Math.abs((currentPosition % 3) - (goalPosition % 3));
        int y = Math.abs((currentPosition / 3) - (goalPosition / 3));

        return x + y;
    }

    public SolutionData solve(String initialState, int h) {
        PriorityQueue<Puzzle> frontier = new PriorityQueue<>();
        HashSet<String> exploredSet = new HashSet<>();

        frontier.add(new Puzzle(initialState, 0, 0, null));
        Puzzle current = null;
        long timeElapsed = 0;
        long timeStart = System.currentTimeMillis();

        while (!frontier.isEmpty()) {
            current = frontier.remove();
            exploredSet.add(current.currentState);

            if (!current.currentState.equals(GOAL_STATE)) {
                ArrayList<String> moves = current.getValidMoves();

                // frontier
                for (String state : moves) {
                    // ignore explored nodes
                    if (!exploredSet.contains(state)) {
                        int hValue;

                        // assign h value to the node
                        if (h == 1) {
                            hValue = misplacedTiles(state);
                        } else {
                            hValue = sumOfTheDistances(state);
                        }

                        frontier.add(new Puzzle((state), current.stepCost + 1, hValue, current));
                    }
                }
            } else {
                timeElapsed = System.currentTimeMillis() - timeStart;
                frontier.clear();
            }
        }

        // rebuild path
        ArrayList<String> path = new ArrayList<>();
        while (current != null) {
            path.add(current.currentState);
            current = current.previousPuzzle;
        }
        Collections.reverse(path);
        return new SolutionData(path, timeElapsed, exploredSet.size() + frontier.size());
    }

    // check if array contains a given value
    public boolean ifContains(int[] arr, int value) {
        for (int i : arr) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    // swapping two characters in a string
    public String swapString(String str, int index1, int index2) {
        char[] arr = str.toCharArray();
        char temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
        return new String(arr);
    }

		//printing string
    public String print(String configuration) {
        String str = configuration.substring(0, 3).replace("", " ").trim();
        str += "\n";
        str += configuration.substring(3, 6).replace("", " ").trim();
        str += "\n";
        str += configuration.substring(6, 9).replace("", " ").trim();
        str += "\n";
        return str;
    }

    @Override
    public int compareTo(Puzzle other) {
        // returns the comparison of f(n) from both puzzles
        // necessary for implementing the priority queue heuristics
        int priority1 = stepCost + estimatedCost;
        int priority2 = other.stepCost + other.estimatedCost;

        if (priority1 < priority2) {
            return -1;
        } else if (priority1 > priority2) {
            return 1;
        } else {
            return 0;
        }
    }
}
