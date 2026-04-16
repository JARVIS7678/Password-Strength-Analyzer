package analyzer;

import java.util.Random;
import java.util.Scanner;

/**
 * Main command-line application entry point.
 * Provides an interactive loop-based menu for users to analyze passwords.
 */
public class Main {
    
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  ADVANCED PASSWORD STRENGTH ANALYZER  ");
        System.out.println("==========================================\n");

        boolean keepRunning = true;

        while (keepRunning) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleAnalysis();
                    break;
                case "2":
                    handleGeneration();
                    break;
                case "3":
                    System.out.println("\nThank you for using the Advanced Password Analyzer! Goodbye.");
                    keepRunning = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid option. Please carefully select 1, 2, or 3.");
            }
        }

        scanner.close();
    }

    /**
     * Displays main menu options to console.
     */
    private static void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Analyze a Password manually");
        System.out.println("2. Generate a Secure Password automatically");
        System.out.println("3. Exit Application");
        System.out.print("Select an option (1-3): ");
    }

    /**
     * Handles manual password analysis logic workflow.
     */
    private static void handleAnalysis() {
        System.out.print("\nType your password to analyze: ");
        String password = scanner.nextLine();

        if (password == null || password.isEmpty()) {
            System.out.println("[!] Warning: Password cannot be totally empty.");
            return;
        }

        System.out.println("\nProcessing rules, entropy & matrices...");
        StrengthResult result = PasswordAnalyzer.analyze(password);
        printResultWithDecorations(password, result);
    }

    /**
     * Handles randomized generation of a robust layout.
     */
    private static void handleGeneration() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        
        String newPass = sb.toString();
        System.out.println("\nGenerated Password: " + newPass);
        System.out.println("Analyzing generated password automatically...\n");
        StrengthResult generatedResult = PasswordAnalyzer.analyze(newPass);
        printResultWithDecorations(newPass, generatedResult);
    }
    
    /**
     * Pretty-prints the data payload cleanly to the standard output terminal.
     * @param password Analyzed text
     * @param result Analyzed object
     */
    private static void printResultWithDecorations(String password, StrengthResult result) {
        System.out.println("------------------------------------------");
        System.out.println("Target: [" + password.replaceAll(".", "*") + "] (Hidden for security)");
        System.out.println("\n" + result.toString());
        System.out.println("------------------------------------------");
    }
}
