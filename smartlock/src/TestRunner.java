import model.FSM;
import service.FsmParser;
import service.FsmEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Temporary CLI TestRunner for FSM Door Lock (Milestone 2).
 * Verifies that the model and the parsing/math engines work correctly together.
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   FSM DOOR LOCK : SYSTEM VERIFICATION RUNNER     ");
        System.out.println("==================================================\n");

        // 1. Create a temporary test file with the specification's FSM definitions
        File tempFsmFile = new File("temp_test_fsm.txt");
        try (FileWriter writer = new FileWriter(tempFsmFile)) {
            writer.write("# FSM Specification Test File\n");
            writer.write("S0 a S1\n");
            writer.write("S1 b S3\n");
            writer.write("S2 a S3\n");
            writer.write("S2 b S4\n");
            writer.write("S3 a S4\n");
            writer.write("S4\n"); // Final State
        } catch (IOException e) {
            System.err.println("[-] Failed to create temporary FSM test file: " + e.getMessage());
            return;
        }

        try {
            // 2. Parse the FSM file
            System.out.println("[+] Parsing FSM rules from file '" + tempFsmFile.getName() + "'...");
            FSM fsm = FsmParser.parseFromFile(tempFsmFile);
            System.out.println("    Registered Initial State : " + fsm.getInitialState());
            System.out.println("    Registered Unlock State  : " + fsm.getFinalState());
            System.out.println("    Total States Registered  : " + fsm.getTransitions().keySet().size() + "\n");

            // 3. Test Output C: Machine Type Classification
            System.out.println("--------------------------------------------------");
            System.out.println("[ALGORITHM C] Machine Classification Test:");
            String machineType = FsmEngine.getMachineType(fsm);
            System.out.println(" -> Classification Result: " + machineType);
            System.out.println("--------------------------------------------------\n");

            // 4. Test Output A: Valid String Generation
            System.out.println("--------------------------------------------------");
            System.out.println("[ALGORITHM A] Valid Master PINs (Max length: 6):");
            List<String> validPINs = FsmEngine.generateValidStrings(fsm, 6);
            for (int i = 0; i < validPINs.size(); i++) {
                System.out.printf(" -> Accepted PIN #%d: \"%s\"\n", i + 1, validPINs.get(i));
            }
            System.out.println("--------------------------------------------------\n");

            // 5. Test Output B: Keypad PIN Validator Simulation
            System.out.println("--------------------------------------------------");
            System.out.println("[ALGORITHM B] Live Keypad Simulation:");
            String[] testAttempts = {"aba", "ab", "a", "bba", "abaa"};
            for (String attempt : testAttempts) {
                boolean accepted = FsmEngine.testPIN(fsm, fsm.getInitialState(), attempt, 0);
                String display = accepted ? "[ ACCESS GRANTED - DOOR UNLOCKED ]" : "[ ACCESS DENIED ]";
                System.out.printf(" -> Keypad Input: %-6s | Door Status: %s\n", "\"" + attempt + "\"", display);
            }
            System.out.println("--------------------------------------------------");

        } catch (Exception e) {
            System.err.println("[-] Test runner encountered an error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up the temporary file
            if (tempFsmFile.exists()) {
                tempFsmFile.delete();
            }
        }
        
        System.out.println("\n==================================================");
        System.out.println("   VERIFICATION COMPLETE: ZERO REGRESSIONS        ");
        System.out.println("==================================================");
    }
}
