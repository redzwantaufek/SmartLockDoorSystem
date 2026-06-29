package com.group6;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileParser {
    
    public static FiniteStateMachine loadFromFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        
        // We assume the first line contains the Start State (e.g., "S0 a S1" -> start is S0)
        FiniteStateMachine fsm = null;
        String lastLine = "";

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");

            if (parts.length == 3) {
                String fromState = parts[0];
                char inputSymbol = parts[1].charAt(0);
                String toState = parts[2];

                if (fsm == null) {
                    fsm = new FiniteStateMachine(fromState);
                }
                fsm.addTransition(fromState, inputSymbol, toState);
            } 
            lastLine = line;
        }
        scanner.close();

        // According to assignment prompt: "the last line is the final state"
        if (fsm != null && !lastLine.isEmpty()) {
            // Handles cases where last line is just "S4" or a transition that ends on final state
            String[] parts = lastLine.split("\\s+");
            String finalState = parts[parts.length - 1]; 
            fsm.addFinalState(finalState);
        }

        return fsm;
    }
}