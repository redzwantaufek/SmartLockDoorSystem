package service;

import model.FSM;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Safe FSM Parser implementation.
 * Hyper-defensive parsing to handle varied line formatting, comments, and empty lines.
 * Guards against standard syntax errors and the "Last Line Final State" trap.
 */
public class FsmParser {

    /**
     * Parses an FSM from a list of raw strings.
     *
     * @param rawLines List of lines read from the FSM specification.
     * @return A fully populated FSM object.
     * @throws IllegalArgumentException If syntax is invalid, initial state is missing, or final state is missing.
     */
    public static FSM parse(List<String> rawLines) {
        if (rawLines == null) {
            throw new IllegalArgumentException("Raw lines list cannot be null");
        }

        FSM fsm = new FSM();
        boolean isFirstStateSet = false;

        for (int i = 0; i < rawLines.size(); i++) {
            String raw = rawLines.get(i);
            if (raw == null) continue;
            
            String line = raw.trim();
            // Skip empty lines and comment lines (starting with # or //)
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }

            // Split by any consecutive whitespace characters
            String[] tokens = line.split("\\s+");

            if (tokens.length == 3) {
                String from = tokens[0];
                String symbolStr = tokens[1];
                String to = tokens[2];

                if (symbolStr.length() != 1) {
                    throw new IllegalArgumentException(
                        "Syntax Error at line " + (i + 1) + ": Transition symbol must be a single character. Found: '" + symbolStr + "'"
                    );
                }
                char inputChar = symbolStr.charAt(0);

                // The first state encountered in any valid transition is defined as the initial state
                if (!isFirstStateSet) {
                    fsm.setInitialState(from);
                    isFirstStateSet = true;
                }

                fsm.addTransition(from, inputChar, to);

            } else if (tokens.length == 1) {
                // S4 last line / final state trap handled here safely
                String finalState = tokens[0];
                fsm.setFinalState(finalState);
            } else {
                throw new IllegalArgumentException(
                    "Syntax Error at line " + (i + 1) + ": Expected 3 tokens for transition or 1 token for final state. Found: '" + line + "'"
                );
            }
        }

        // Post-parsing validations
        if (fsm.getInitialState() == null) {
            throw new IllegalArgumentException("Invalid FSM specification: No initial state defined.");
        }
        if (fsm.getFinalState() == null) {
            throw new IllegalArgumentException("Invalid FSM specification: No final state defined (missing final state line).");
        }

        return fsm;
    }

    /**
     * Loads and parses an FSM from a file.
     *
     * @param file The FSM specification file.
     * @return A fully populated FSM object.
     * @throws IOException If a file reading error occurs.
     * @throws IllegalArgumentException If parsing or syntax errors occur.
     */
    public static FSM parseFromFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File parameter cannot be null");
        }
        if (!file.exists()) {
            throw new java.io.FileNotFoundException("FSM file not found: " + file.getAbsolutePath());
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return parse(lines);
    }

    /**
     * Loads and parses an FSM from a file path.
     *
     * @param filePath The path to the FSM file.
     * @return A fully populated FSM object.
     * @throws IOException If file reading error occurs.
     */
    public static FSM parseFromFilePath(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        return parseFromFile(new File(filePath.trim()));
    }
}
