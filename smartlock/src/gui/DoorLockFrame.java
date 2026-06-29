package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Dumb GUI Skeleton for FSM Door Lock Simulator (Milestone 3).
 * Contains ONLY layout code and UI component structure. No logic or event wiring.
 * Exposes components for easy external wiring.
 */
public class DoorLockFrame extends JFrame {

    // File Loader Components
    public JButton browseButton;
    public JTextField filePathField;

    // FSM Info Display Components
    public JLabel startStateVal;
    public JLabel finalStateVal;
    public JLabel fsmTypeVal;

    // Algorithm A (Generator) Components
    public JSpinner maxLengthSpinner;
    public JButton generateButton;
    public JTextArea resultsArea;

    // Algorithm B (Keypad Simulator) Components
    public JTextField keypadDisplay;
    public JLabel statusLabel;
    
    // Keypad Buttons
    public JButton btn1, btn2, btn3, btnA;
    public JButton btn4, btn5, btn6, btnB;
    public JButton btn7, btn8, btn9, btnC;
    public JButton btnClear, btn0, btnEnter, btnD;

    public DoorLockFrame() {
        super("FSM Smart Door Lock Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Top Panel: File Loader & FSM Metadata
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        // File Loader Row
        JPanel loaderPanel = new JPanel(new BorderLayout(5, 5));
        loaderPanel.setBorder(BorderFactory.createTitledBorder("1. Load FSM Configuration"));
        filePathField = new JTextField();
        filePathField.setEditable(false);
        browseButton = new JButton("Load FSM file...");
        loaderPanel.add(filePathField, BorderLayout.CENTER);
        loaderPanel.add(browseButton, BorderLayout.EAST);
        topPanel.add(loaderPanel, BorderLayout.NORTH);

        // FSM Metadata Row
        JPanel metadataPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        metadataPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JPanel pStart = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pStart.add(new JLabel("Start State:"));
        startStateVal = new JLabel("None");
        startStateVal.setFont(startStateVal.getFont().deriveFont(Font.BOLD));
        pStart.add(startStateVal);
        
        JPanel pFinal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pFinal.add(new JLabel("Unlock State:"));
        finalStateVal = new JLabel("None");
        finalStateVal.setFont(finalStateVal.getFont().deriveFont(Font.BOLD));
        pFinal.add(finalStateVal);
        
        JPanel pType = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pType.add(new JLabel("FSM Type:"));
        fsmTypeVal = new JLabel("Unknown");
        fsmTypeVal.setFont(fsmTypeVal.getFont().deriveFont(Font.BOLD));
        pType.add(fsmTypeVal);

        metadataPanel.add(pStart);
        metadataPanel.add(pFinal);
        metadataPanel.add(pType);
        
        topPanel.add(metadataPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 2. Main Content Split: Generator (Left) and Keypad (Right)
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // --- Left Panel: Algorithm A (Password Generator) ---
        JPanel generatorPanel = new JPanel(new BorderLayout(5, 5));
        generatorPanel.setBorder(BorderFactory.createTitledBorder("2. Accepted Password Generator (Algorithm A)"));
        
        JPanel genControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        genControlPanel.add(new JLabel("Max PIN Length:"));
        maxLengthSpinner = new JSpinner(new SpinnerNumberModel(6, 1, 15, 1));
        generateButton = new JButton("Generate Master PINs");
        genControlPanel.add(maxLengthSpinner);
        genControlPanel.add(generateButton);
        generatorPanel.add(genControlPanel, BorderLayout.NORTH);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        generatorPanel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);

        // --- Right Panel: Algorithm B (Live Keypad Simulator) ---
        JPanel keypadContainer = new JPanel(new BorderLayout(5, 5));
        keypadContainer.setBorder(BorderFactory.createTitledBorder("3. Keypad Access Simulator (Algorithm B)"));

        // Display and Status panel
        JPanel displayPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        
        keypadDisplay = new JTextField();
        keypadDisplay.setEditable(false);
        keypadDisplay.setHorizontalAlignment(JTextField.CENTER);
        keypadDisplay.setFont(new Font("Monospaced", Font.BOLD, 22));
        displayPanel.add(keypadDisplay);
        
        statusLabel = new JLabel("LOCKED", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.DARK_GRAY);
        statusLabel.setForeground(Color.RED);
        displayPanel.add(statusLabel);
        
        keypadContainer.add(displayPanel, BorderLayout.NORTH);

        // Keypad Grid Layout
        JPanel gridPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        
        btn1 = new JButton("1");
        btn2 = new JButton("2");
        btn3 = new JButton("3");
        btnA = new JButton("a");
        
        btn4 = new JButton("4");
        btn5 = new JButton("5");
        btn6 = new JButton("6");
        btnB = new JButton("b");
        
        btn7 = new JButton("7");
        btn8 = new JButton("8");
        btn9 = new JButton("9");
        btnC = new JButton("c");
        
        btnClear = new JButton("CLR");
        btn0 = new JButton("0");
        btnEnter = new JButton("ENT");
        btnD = new JButton("d");

        // Styling keypad buttons mildly for visual clarity
        JButton[] buttons = {
            btn1, btn2, btn3, btnA,
            btn4, btn5, btn6, btnB,
            btn7, btn8, btn9, btnC,
            btnClear, btn0, btnEnter, btnD
        };
        for (JButton btn : buttons) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        }

        // Add to grid
        gridPanel.add(btn1); gridPanel.add(btn2); gridPanel.add(btn3); gridPanel.add(btnA);
        gridPanel.add(btn4); gridPanel.add(btn5); gridPanel.add(btn6); gridPanel.add(btnB);
        gridPanel.add(btn7); gridPanel.add(btn8); gridPanel.add(btn9); gridPanel.add(btnC);
        gridPanel.add(btnClear); gridPanel.add(btn0); gridPanel.add(btnEnter); gridPanel.add(btnD);

        keypadContainer.add(gridPanel, BorderLayout.CENTER);

        mainContentPanel.add(generatorPanel);
        mainContentPanel.add(keypadContainer);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    /**
     * Dummy Main method for previewing the layout.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DoorLockFrame frame = new DoorLockFrame();
            frame.setVisible(true);
        });
    }
}
