package com.group6;
public class Main {
    public static void main(String[] args) {
        try {
            FiniteStateMachine fsm = FileParser.loadFromFile("assignment_q4.txt");

            System.out.println("=== SYSTEM VERIFICATION ===");
            System.out.println("Automaton Type: " + fsm.getAutomatonType()); // Should print DFA
            System.out.println("Generated Strings: " + fsm.generateAcceptedStrings(6)); 

            String testPIN = "aba";
            boolean result = fsm.isStringAccepted(testPIN);
            System.out.println("Testing PIN '" + testPIN + "': " + (result ? "ACCEPTED" : "REJECTED"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}