# 🚪 Smart Doorlock FSM Simulator

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge)
![Concept](https://img.shields.io/badge/Concept-Automata_Theory-success?style=for-the-badge)

A Java Swing application that simulates a digital secure doorlock system governed by a Finite State Machine (FSM). This project translates abstract automata theory into a tangible software implementation, handling dynamic file parsing, graph traversal, and real-time state evaluation.

## 🧠 Core Engineering Concepts

This system was built with a strict adherence to the **Model-View-Controller (MVC)** architecture to decouple the mathematical FSM logic from the Swing GUI components.

The core engine implements three primary algorithms:
1.  **Defensive Parsing:** Dynamically constructs an Adjacency List graph from custom text files, with explicit edge-case handling for malformed lines and terminal-only states.
2.  **Bounded Depth-First Search (DFS):** Traverses the FSM graph to generate all valid "Master Passwords" (accepted strings), utilizing a hard-capped depth limit to prevent `StackOverflowErrors` from cyclical graph loops.
3.  **Backtracking Validation:** Evaluates real-time keypad inputs against the loaded FSM. It recursively explores all possible branches, seamlessly supporting both Deterministic (DFA) and Non-Deterministic (NFA) state machines.

## 🚀 Features

*   **Dynamic Lock Rules:** Upload any valid `.txt` file containing state transitions to reconfigure the door's security logic instantly.
*   **Machine Classification:** Automatically scans the loaded adjacency list and classifies the system as either **DFA** or **NFA**.
*   **Master Key Generation:** Outputs a complete audit log of all valid PINs that can unlock the current system.
*   **Live Keypad Simulation:** Real-time visual feedback (`ACCESS GRANTED` / `ACCESS DENIED`) based on user input.

## 📸 Demonstration

> **Note:** [Insert an animated GIF or a screenshot of the Swing GUI in action here. Show the transition from LOCKED to UNLOCKED.]

## 🛠️ Project Structure

```text
src/
 ├── model/
 │    └── FSM.java               # Pure mathematical graph representation
 ├── service/
 │    ├── FsmParser.java         # Defensive file input parser
 │    └── FsmEngine.java         # Core algorithms (DFS, Backtracking, Classifiers)
 └── gui/
      └── DoorLockFrame.java     # Swing View & Controller
