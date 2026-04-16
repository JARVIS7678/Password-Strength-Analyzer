# Password Strength Analyzer

A Java project that evaluates the strength of a password based on length, character types, and provides suggestions for improvement. It also estimates the time to crack the password and assigns a strength score.

## Features

* **Multi-file Structure**: Organized into `Main.java`, `PasswordAnalyzer.java`, and `StrengthResult.java`.
* **Core Logic**: Evaluates length, uppercase, lowercase, numbers, and special characters.
* **Scoring Rules**: Assigns points based on complexity and categorizes into WEAK, MEDIUM, STRONG.
* **Crack Time Estimation**: Approximates time needed to crack using brute-force (assuming 1 billion guesses per second).
* **CLI Version**: Default terminal-based interaction.
* **Optional Features (High Marks)**: Includes `MainGUI.java`, a Swing-based graphical user interface with an integrated dark theme, random password generator, multiple password analysis capability, and a text report exporter.

## Project Structure

```
java project/
├── src/
│   └── analyzer/
│       ├── Main.java              (Command-Line Interface)
│       ├── MainGUI.java           (Optional Swing GUI + Dark Theme + Generator + Exporter)
│       ├── PasswordAnalyzer.java  (Core testing and scoring logic)
│       └── StrengthResult.java    (Data model for storing analysis results)
```

## How to run (CLI Version)

1. Make sure you have Java installed (`java -version`).
2. Open your terminal in the project root directory.
3. Compile the code:
   ```bash
   mkdir -p out
   javac -d out src/analyzer/*.java
   ```
4. Run the main class:
   ```bash
   java -cp out analyzer.Main
   ```

## How to run (GUI Version)

If you'd like to check out the **Optional Features (High Marks)** version:
1. Compile the code (as shown above).
2. Run the `MainGUI` class:
   ```bash
   java -cp out analyzer.MainGUI
   ```

## Requirements
* Java 8 or higher.
* The Optional PDF export was simplified to a `.txt` exporter to avoid heavy external dependencies (like iText/PDFBox). This ensures the application remains standalone and simple to run while still implementing the export function!
