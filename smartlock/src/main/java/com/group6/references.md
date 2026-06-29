# MWE: Finite State Machine (FSM) Secure Door Lock

> **What is this?** This is a zero-dependency, single-file Minimum Working Example (MWE) written in standard Java. It packs the **Model, the Safe Parser, the 3 Homework Algorithms, and a Mock Test-Runner** into one file.

---

### Quickstart (How to run this instantly):

1. Create an empty file named `Main.java`.
2. Copy the Java code block below and paste it inside `Main.java`.
3. Open your terminal / command prompt in that folder and run:
   ```bash
   javac Main.java
   java Main
   ```

***

```java
import java.util.*;

public class Main {

    // ==========================================================
    // 1. THE DATA MODEL
    // ==========================================================
    static class FSM {
        String initialState;
        String finalState;
        // Structure: State -> ( InputChar -> Set<NextStates> )
        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();

        void addTransition(String from, char input, String to) {
            transitions.putIfAbsent(from, new HashMap<>());
            transitions.get(from).putIfAbsent(input, new HashSet<>());
            transitions.get(from).get(input).add(to);
        }
    }

    // ==========================================================
    // 2. THE DEFENSIVE PARSER (Survives bad homework formatting)
    // ==========================================================
    static FSM parseRawLines(List<String> rawLines) {
        FSM fsm = new FSM();
        boolean isFirstLine = true;

        for (String raw : rawLines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue; // Skip blanks & comments

            String[] tokens = line.split("\\s+"); // Regex: split by 1 or more spaces

            if (isFirstLine && tokens.length > 0) {
                fsm.initialState = tokens[0];
                isFirstLine = false;
            }

            if (tokens.length == 3) {
                fsm.addTransition(tokens[0], tokens[1].charAt(0), tokens[2]);
            } else if (tokens.length == 1) {
                fsm.finalState = tokens[0]; // Catches the "S4" last line trap safely
            } else {
                throw new IllegalArgumentException("Syntax Error in FSM file at line: [" + line + "]");
            }
        }
        return fsm;
    }

    // ==========================================================
    // 3. THE ENGINE (The 3 Homework Rubric Answers)
    // ==========================================================

    /** OUTPUT C: Determines if machine is Deterministic or Non-Deterministic */
    static String getMachineType(FSM fsm) {
        for (Map<Character, Set<String>> stateTransitions : fsm.transitions.values()) {
            for (Set<String> destinations : stateTransitions.values()) {
                if (destinations.size() > 1) return "NFA (Non-Deterministic)";
            }
        }
        return "DFA (Deterministic)";
    }

    /** OUTPUT B: PIN Validator using Backtracking Recursion */
    static boolean testPIN(FSM fsm, String currState, String pin, int index) {
        if (index == pin.length()) {
            return currState.equals(fsm.finalState);
        }

        char inputChar = pin.charAt(index);
        Map<Character, Set<String>> stateMap = fsm.transitions.get(currState);

        if (stateMap == null || !stateMap.containsKey(inputChar)) return false; // Dead end

        for (String nextState : stateMap.get(inputChar)) {
            if (testPIN(fsm, nextState, pin, index + 1)) return true; // Branch reached Unlock!
        }
        return false;
    }

    /** OUTPUT A: Generates accepted strings using Bounded Depth-First Search */
    static List<String> generateValidStrings(FSM fsm, int maxCharLimit) {
        List<String> results = new ArrayList<>();
        dfs(fsm, fsm.initialState, "", maxCharLimit, results);
        return results;
    }

    private static void dfs(FSM fsm, String curr, String path, int maxLen, List<String> res) {
        if (curr.equals(fsm.finalState)) {
            res.add(path.isEmpty() ? "[Empty String / ε]" : path);
        }
        if (path.length() >= maxLen) return; // Emergency brake against infinite FSM loops

        Map<Character, Set<String>> stateMap = fsm.transitions.get(curr);
        if (stateMap != null) {
            for (Map.Entry<Character, Set<String>> entry : stateMap.entrySet()) {
                for (String nextState : entry.getValue()) {
                    dfs(fsm, nextState, path + entry.getKey(), maxLen, res);
                }
            }
        }
    }

    // ==========================================================
    // 4. MWE MOCK EXECUTION (Simulating the lecturer's test)
    // ==========================================================
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SMART DOOR LOCK : FSM CORE ENGINE (MWE v1.0)   ");
        System.out.println("==================================================\n");

        // 1. Simulating reading the question's exact text file
        List<String> mockFile = Arrays.asList(
            "S0 a S1",
            "S1 b S3",
            "S2 a S3",
            "S2 b S4",
            "S3 a S4",
            "S4"
        );

        System.out.println("[+] Loading FSM Rules from simulated file...");
        FSM doorLockFSM = parseRawLines(mockFile);
        System.out.println("    Registered Initial State : " + doorLockFSM.initialState);
        System.out.println("    Registered Unlock State  : " + doorLockFSM.finalState + "\n");

        // --- TEST SOAL C ---
        System.out.println("--------------------------------------------------");
        System.out.println("[QUESTION C] Machine Classification Verification:");
        System.out.println(" -> Result: " + getMachineType(doorLockFSM));

        // --- TEST SOAL A ---
        System.out.println("\n--------------------------------------------------");
        System.out.println("[QUESTION A] Valid Master Passwords (Max depth: 6):");
        List<String> validPINs = generateValidStrings(doorLockFSM, 6);
        for (int i = 0; i < validPINs.size(); i++) {
            System.out.printf(" -> PIN #%d: \"%s\"\n", i+1, validPINs.get(i));
        }

        // --- TEST SOAL B ---
        System.out.println("\n--------------------------------------------------");
        System.out.println("[QUESTION B] Live Keypad Access Simulation:");
        String[] keypadAttempts = {"aba", "ab", "a", "bba", "abaa"};

        for (String pin : keypadAttempts) {
            boolean isUnlocked = testPIN(doorLockFSM, doorLockFSM.initialState, pin, 0);
            String doorResponse = isUnlocked ? "[ ACCESS GRANTED - DOOR UNLOCKED ]" : "[ ACCESS DENIED ]";
            System.out.printf(" -> Typed: %-6s | Display: %s\n", "\"" + pin + "\"", doorResponse);
        }
        System.out.println("\n==================================================");
    }
}
```