package com.group6;
import java.util.*;

public class FiniteStateMachine {
    private String startState;
    private Set<String> finalStates;
    // Outer Map: Current State | Inner Map: Input Symbol -> Set of Next States
    private Map<String, Map<Character, Set<String>>> transitionTable;

    public FiniteStateMachine(String startState) {
        this.startState = startState;
        this.finalStates = new HashSet<>();
        this.transitionTable = new HashMap<>();
    }

    public void addFinalState(String state) {
        this.finalStates.add(state);
    }

    public void addTransition(String from, char inputSymbol, String to) {
        transitionTable.putIfAbsent(from, new HashMap<>());
        transitionTable.get(from).putIfAbsent(inputSymbol, new HashSet<>());
        transitionTable.get(from).get(inputSymbol).add(to);
        
        // Ensure destination state exists in the main table even if it has no outgoing transitions
        transitionTable.putIfAbsent(to, new HashMap<>());
    }

    // --- REQUIREMENT C: Check if DFA or NFA ---
    public String getAutomatonType() {
        for (String state : transitionTable.keySet()) {
            Map<Character, Set<String>> transitions = transitionTable.get(state);
            for (char symbol : transitions.keySet()) {
                // If any input symbol leads to more than 1 next state, it is an NFA
                if (transitions.get(symbol).size() > 1) {
                    return "NFA";
                }
            }
        }
        return "DFA";
    }

    // --- REQUIREMENT B: Check if string is Accepted or Rejected ---
    public boolean isStringAccepted(String input) {
        Set<String> currentStates = new HashSet<>();
        currentStates.add(startState);

        for (char symbol : input.toCharArray()) {
            Set<String> nextStates = new HashSet<>();
            for (String state : currentStates) {
                Map<Character, Set<String>> transitions = transitionTable.getOrDefault(state, new HashMap<>());
                if (transitions.containsKey(symbol)) {
                    nextStates.addAll(transitions.get(symbol));
                }
            }
            // If we hit a dead end (trap/undefined transition), path dies
            if (nextStates.isEmpty()) {
                return false;
            }
            currentStates = nextStates;
        }

        // Check if any of the states we landed on is an official Final State
        for (String state : currentStates) {
            if (finalStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    // --- REQUIREMENT A: Generate List of Accepted Strings L(M) ---
    public List<String> generateAcceptedStrings(int maxLength) {
        List<String> validStrings = new ArrayList<>();
        dfsGenerate(startState, "", validStrings, maxLength);
        return validStrings;
    }

    private void dfsGenerate(String currentState, String currentString, List<String> validStrings, int maxLength) {
        if (finalStates.contains(currentState)) {
            validStrings.add(currentString);
        }
        if (currentString.length() >= maxLength) {
            return; // Prevent infinite loops (StackOverflow) on cyclic graphs
        }

        Map<Character, Set<String>> transitions = transitionTable.getOrDefault(currentState, new HashMap<>());
        for (char symbol : transitions.keySet()) {
            for (String nextState : transitions.get(symbol)) {
                dfsGenerate(nextState, currentString + symbol, validStrings, maxLength);
            }
        }
    }
}