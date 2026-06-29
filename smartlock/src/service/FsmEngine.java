package service;

import model.FSM;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FSM Math Engine.
 * Hyper-defensive implementation of classification, PIN validation, and accepted string generation.
 * Specifically guards against cycles (Trap 2) and dead ends/null pointers (Trap 3).
 */
public class FsmEngine {

    /**
     * Determines whether the FSM is a DFA or an NFA (Output C).
     *
     * @param fsm The FSM instance to classify.
     * @return "DFA (Deterministic)" or "NFA (Non-Deterministic)".
     */
    public static String getMachineType(FSM fsm) {
        if (fsm == null) {
            throw new IllegalArgumentException("FSM cannot be null");
        }
        
        Map<String, Map<Character, Set<String>>> transitions = fsm.getTransitions();
        for (Map<Character, Set<String>> stateTransitions : transitions.values()) {
            if (stateTransitions == null) continue;
            for (Set<String> destinations : stateTransitions.values()) {
                // If any state transition on a character has more than one destination state, it's an NFA.
                if (destinations != null && destinations.size() > 1) {
                    return "NFA (Non-Deterministic)";
                }
            }
        }
        return "DFA (Deterministic)";
    }

    /**
     * PIN Validator using backtracking recursion (Output B).
     * Handles missing transitions and null states safely.
     *
     * @param fsm The FSM model.
     * @param currState The state to evaluate from.
     * @param pin The PIN string sequence.
     * @param index The current index in the PIN string.
     * @return true if the PIN path successfully reaches the final/unlock state at the end of the input.
     */
    public static boolean testPIN(FSM fsm, String currState, String pin, int index) {
        if (fsm == null || pin == null) {
            return false;
        }
        if (currState == null || !fsm.hasState(currState)) {
            return false; // Dead end: state is invalid or does not exist
        }
        
        // Base case: check if we parsed the entire PIN
        if (index == pin.length()) {
            return currState.equals(fsm.getFinalState());
        }
        
        // Safety guard against index out of bounds
        if (index < 0 || index > pin.length()) {
            return false;
        }

        char inputChar = pin.charAt(index);
        Set<String> nextStates = fsm.getNextStates(currState, inputChar);
        if (nextStates == null || nextStates.isEmpty()) {
            return false; // Trap 3: Dead end / undefined transition
        }

        // Backtracking traversal for NFA compatibility
        for (String nextState : nextStates) {
            if (testPIN(fsm, nextState, pin, index + 1)) {
                return true; // Accepted path found
            }
        }
        
        return false;
    }

    /**
     * Generates all accepted string sequences up to a bounded maximum length (Output A).
     * Uses bounded depth-first search (DFS) with a maximum length emergency brake.
     *
     * @param fsm The FSM model.
     * @param maxCharLimit The emergency brake string length limit.
     * @return A list of valid strings accepted by the FSM.
     */
    public static List<String> generateValidStrings(FSM fsm, int maxCharLimit) {
        if (fsm == null || maxCharLimit < 0) {
            return Collections.emptyList();
        }
        
        List<String> results = new ArrayList<>();
        String initialState = fsm.getInitialState();
        if (initialState == null) {
            return results;
        }
        
        dfs(fsm, initialState, "", maxCharLimit, results);
        return results;
    }

    private static void dfs(FSM fsm, String curr, String path, int maxLen, List<String> res) {
        if (curr == null) return;

        // If current state is the final/unlock state, add path to accepted list
        if (curr.equals(fsm.getFinalState())) {
            res.add(path.isEmpty() ? "[Empty String / ε]" : path);
        }

        // Trap 2: Infinite loop prevention (emergency brake limit)
        if (path.length() >= maxLen) {
            return;
        }

        Map<Character, Set<String>> stateTransitions = fsm.getTransitions().get(curr);
        if (stateTransitions != null) {
            for (Map.Entry<Character, Set<String>> entry : stateTransitions.entrySet()) {
                char symbol = entry.getKey();
                Set<String> nextStates = entry.getValue();
                if (nextStates != null) {
                    for (String nextState : nextStates) {
                        dfs(fsm, nextState, path + symbol, maxLen, res);
                    }
                }
            }
        }
    }
}
