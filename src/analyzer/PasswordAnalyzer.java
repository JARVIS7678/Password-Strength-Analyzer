package analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Engine for testing password strength based on various regex rules,
 * dictionary checks, combinatorial math estimations, and entropy calculation.
 */
public class PasswordAnalyzer {

    // A small subset dictionary of notoriously weak or common passwords
    private static final List<String> COMMON_WEAK_PASSWORDS = Arrays.asList(
        "password", "12345", "123456", "12345678", "qwerty", 
        "admin", "welcome", "iloveyou", "letmein", "111111", 
        "dragon", "sunshine", "monkey", "charlie"
    );

    /**
     * Conducts a full analysis of the provided password string.
     * 
     * @param password The raw text password input to check.
     * @return StrengthResult object containing all metrics.
     */
    public static StrengthResult analyze(String password) {
        int score = 0;
        List<String> suggestions = new ArrayList<>();

        // Core character matching logic using Regex
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNum = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?`~].*");

        int length = password.length();

        // 1. Length Scoring
        if (length >= 8) {
            score += 2;
        } else {
            suggestions.add("Increase password length to at least 8 characters");
        }
        
        if (length >= 12) {
            score += 2; // Bonus points for long passwords
        }

        // 2. Character Diversity Scoring
        if (hasUpper) {
            score += 1;
        } else {
            suggestions.add("Add uppercase letters (A-Z)");
        }

        if (hasLower) {
            score += 1;
        } else {
            suggestions.add("Add lowercase letters (a-z)");
        }

        if (hasNum) {
            score += 1;
        } else {
            suggestions.add("Add numeric digits (0-9)");
        }

        if (hasSpecial) {
            score += 2; // High value for special characters
        } else {
            suggestions.add("Add special characters (e.g. !, @, $, *)");
        }
        
        // 3. Dictionary & Known Patterns Penalty
        String lowerPassword = password.toLowerCase();
        boolean foundWeak = false;
        for (String weakWord : COMMON_WEAK_PASSWORDS) {
            if (lowerPassword.contains(weakWord)) {
                score -= 3; // Severe penalty for common passwords
                suggestions.add("Remove common words/patterns like '" + weakWord + "'");
                foundWeak = true;
                break; // Apply penalty once
            }
        }
        
        // 4. Repeated character sequence penalty
        if (password.matches(".*(.)\\1{2,}.*")) {
            score -= 1;
            suggestions.add("Avoid repeating the same character sequentially (e.g. 'aaa')");
        }

        // Fix lower bound of score
        if (score < 0) score = 0;

        // Strength Categorization Decision
        String strength;
        if (score <= 4) {
            strength = "WEAK";
        } else if (score <= 7) {
            strength = "MEDIUM";
        } else {
            strength = "STRONG";
        }

        // Crack Time & Entropy Mathematics
        int charset = 0;
        if (hasLower) charset += 26;
        if (hasUpper) charset += 26;
        if (hasNum) charset += 10;
        if (hasSpecial) charset += 32;
        if (charset == 0) charset = 26; // Fallback

        // Information Theory: Shannon Entropy = L * log2(R)
        double entropyBits = length * (Math.log(charset) / Math.log(2));
        if(Double.isNaN(entropyBits)) entropyBits = 0.0;

        // Time to crack using combinatorics
        double combinations = Math.pow(charset, length);
        double guessesPerSecond = 1_000_000_000.0; // Assume 1 billion guesses/sec
        double secondsToCrack = combinations / guessesPerSecond;

        // Formatting timeframe
        String crackTime = formatCrackTime(secondsToCrack);

        return new StrengthResult(score, strength, suggestions, crackTime, entropyBits);
    }

    /**
     * Formats the total seconds into a readable timeframe.
     * 
     * @param seconds Total calculated seconds
     * @return Readable string timeframe
     */
    private static String formatCrackTime(double seconds) {
        if (seconds < 1) return "Instantly (Less than a second)";
        if (seconds < 60) return String.format("%.1f seconds", seconds);
        if (seconds < 3600) return (int) (seconds / 60) + " minutes";
        if (seconds < 86400) return (int) (seconds / 3600) + " hours";
        if (seconds < 31536000) return (int) (seconds / 86400) + " days";
        
        double years = seconds / 31536000.0;
        if (years < 100) return String.format("%.1f years", years);
        if (years < 1000) return (int) years + " years";
        if (years < 1000000) return (int) (years / 1000) + " thousand years";
        return "Centuries (Virtually Un-crackable)";
    }
}
