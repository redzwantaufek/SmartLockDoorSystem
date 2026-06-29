package controller;

import model.FSM;
import service.FsmParser;
import service.FsmEngine;
import gui.DoorLockFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * FSM Door Lock Controller (Milestone 4 - The Nervous System).
 * Wires GUI events to the FSM math engine and parser.
 * Maintains zero business logic inside Swing components.
 */
public class DoorLockController {

    private final DoorLockFrame frame;
    private FSM fsm;

    public DoorLockController(DoorLockFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("Frame cannot be null");
        }
        this.frame = frame;
        initListeners();
        updateMetadataDisplay();
    }

    /**
     * Wires actions to all Swing buttons and controls.
     */
    private void initListeners() {
        // File selection action
        frame.browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoadFsm();
            }
        });

        // String generation action
        frame.generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleGenerateStrings();
            }
        });

        // Keypad input characters
        ActionListener charInputListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                frame.keypadDisplay.setText(frame.keypadDisplay.getText() + cmd);
            }
        };

        frame.btn1.addActionListener(charInputListener);
        frame.btn2.addActionListener(charInputListener);
        frame.btn3.addActionListener(charInputListener);
        frame.btn4.addActionListener(charInputListener);
        frame.btn5.addActionListener(charInputListener);
        frame.btn6.addActionListener(charInputListener);
        frame.btn7.addActionListener(charInputListener);
        frame.btn8.addActionListener(charInputListener);
        frame.btn9.addActionListener(charInputListener);
        frame.btn0.addActionListener(charInputListener);
        frame.btnA.addActionListener(charInputListener);
        frame.btnB.addActionListener(charInputListener);
        frame.btnC.addActionListener(charInputListener);
        frame.btnD.addActionListener(charInputListener);

        // Keypad special actions (CLR / ENT)
        frame.btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.keypadDisplay.setText("");
                resetStatusDisplay();
            }
        });

        frame.btnEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleValidatePIN();
            }
        });
    }

    /**
     * Opens file chooser, parses the chosen FSM specification, and refreshes the metadata views.
     */
    private void handleLoadFsm() {
        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setDialogTitle("Select FSM Specification File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("FSM Configuration (*.txt, *.md)", "txt", "md"));
        
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                FSM parsedFsm = FsmParser.parseFromFile(selectedFile);
                
                // Set FSM only on successful parse
                this.fsm = parsedFsm;
                frame.filePathField.setText(selectedFile.getAbsolutePath());
                updateMetadataDisplay();
                
                // Clear state areas
                frame.resultsArea.setText("");
                frame.keypadDisplay.setText("");
                resetStatusDisplay();

                JOptionPane.showMessageDialog(frame, 
                    "FSM parsed successfully!\nType: " + FsmEngine.getMachineType(fsm),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "Failed to parse FSM file:\n" + ex.getMessage(),
                    "Parsing Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Executes Algorithm A to list accepted password paths.
     */
    private void handleGenerateStrings() {
        if (fsm == null) {
            JOptionPane.showMessageDialog(frame,
                "Please load an FSM specification file first.",
                "FSM Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int maxLength = (Integer) frame.maxLengthSpinner.getValue();
        try {
            List<String> results = FsmEngine.generateValidStrings(fsm, maxLength);
            
            StringBuilder sb = new StringBuilder();
            sb.append("--- ACCEPTED PINs (Max Length: ").append(maxLength).append(") ---\n");
            sb.append("Total Accepted PINs: ").append(results.size()).append("\n\n");
            
            for (int i = 0; i < results.size(); i++) {
                sb.append(String.format(" PIN #%2d: \"%s\"\n", i + 1, results.get(i)));
            }
            
            frame.resultsArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                "Error generating PINs: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Executes Algorithm B to validate the typed PIN code.
     */
    private void handleValidatePIN() {
        if (fsm == null) {
            JOptionPane.showMessageDialog(frame,
                "Please load an FSM specification file first.",
                "FSM Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String inputPin = frame.keypadDisplay.getText();
        
        try {
            boolean accepted = FsmEngine.testPIN(fsm, fsm.getInitialState(), inputPin, 0);
            if (accepted) {
                frame.statusLabel.setText("ACCESS GRANTED - UNLOCKED");
                frame.statusLabel.setBackground(Color.GREEN);
                frame.statusLabel.setForeground(Color.BLACK);
            } else {
                frame.statusLabel.setText("ACCESS DENIED - LOCKED");
                frame.statusLabel.setBackground(Color.RED);
                frame.statusLabel.setForeground(Color.WHITE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                "Error during PIN validation: " + ex.getMessage(),
                "Simulation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes FSM metadata labels.
     */
    private void updateMetadataDisplay() {
        if (fsm == null) {
            frame.startStateVal.setText("None");
            frame.finalStateVal.setText("None");
            frame.fsmTypeVal.setText("Unknown");
        } else {
            frame.startStateVal.setText(fsm.getInitialState());
            frame.finalStateVal.setText(fsm.getFinalState());
            frame.fsmTypeVal.setText(FsmEngine.getMachineType(fsm));
        }
    }

    /**
     * Resets visual security status label to locked.
     */
    private void resetStatusDisplay() {
        frame.statusLabel.setText("LOCKED");
        frame.statusLabel.setBackground(Color.DARK_GRAY);
        frame.statusLabel.setForeground(Color.RED);
    }
}
