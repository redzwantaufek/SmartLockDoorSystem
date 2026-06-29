package model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * FSM model representing the Finite State Machine.
 * Hyper-defensive design: immutable structures where possible, strict null checks,
 * and defensive copies or unmodifiable collections on queries.
 */
public class FSM {
    private String initialState;
    private String finalState;
    // Map of State -> (Input Character -> Set of target States)
    private final Map<String, Map<Character, Set<String>>> transitions;

    public FSM() {
        this.transitions = new HashMap<>();
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        if (initialState == null || initialState.trim().isEmpty()) {
            throw new IllegalArgumentException("Initial state cannot be null or empty");
        }
        this.initialState = initialState.trim();
    }

    public String getFinalState() {
        return finalState;
    }

    public void setFinalState(String finalState) {
        if (finalState == null || finalState.trim().isEmpty()) {
            throw new IllegalArgumentException("Final state cannot be null or empty");
        }
        this.finalState = finalState.trim();
    }

    /**
     * Adds a transition from one state to another given an input character.
     * Hyper-defensive checks ensure no null or blank state names are accepted.
     */
    public void addTransition(String from, char input, String to) {
        if (from == null || from.trim().isEmpty()) {
            throw new IllegalArgumentException("From state cannot be null or empty");
        }
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("To state cannot be null or empty");
        }
        
        String fromClean = from.trim();
        String toClean = to.trim();

        transitions.putIfAbsent(fromClean, new HashMap<>());
        transitions.get(fromClean).putIfAbsent(input, new HashSet<>());
        transitions.get(fromClean).get(input).add(toClean);
        
        // Explicitly guarantee that the target state is registered in the transitions map,
        // even if it has no outgoing transitions yet. This helps prevent NullPointerExceptions
        // when querying states.
        transitions.putIfAbsent(toClean, new HashMap<>());
    }

    /**
     * Returns an unmodifiable view of all registered transitions.
     */
    public Map<String, Map<Character, Set<String>>> getTransitions() {
        return Collections.unmodifiableMap(transitions);
    }

    /**
     * Safely retrieves the next states from a given state on an input character.
     * Guards against null states and missing transitions (Trap 3).
     *
     * @param state The current state.
     * @param input The transition character.
     * @return A Set of next state names (empty if none exist, never null).
     */
    public Set<String> getNextStates(String state, char input) {
        if (state == null) {
            return Collections.emptySet();
        }
        Map<Character, Set<String>> stateTransitions = transitions.get(state.trim());
        if (stateTransitions == null) {
            return Collections.emptySet();
        }
        Set<String> next = stateTransitions.get(input);
        if (next == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(next);
    }

    /**
     * Checks if a given state is registered in the FSM.
     */
    public boolean hasState(String state) {
        if (state == null) return false;
        return transitions.containsKey(state.trim()) 
               || (initialState != null && initialState.equals(state.trim()))
               || (finalState != null && finalState.equals(state.trim()));
    }
}
