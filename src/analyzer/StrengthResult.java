package analyzer;

import java.util.List;

/**
 * Data class representing the comprehensive results of a password analysis.
 * Contains the final score, categorized strength, improvement suggestions,
 * calculated crack time, and the mathematical entropy value.
 */
public class StrengthResult {
    private int score;
    private String strength;
    private List<String> suggestions;
    private String crackTime;
    private double entropyBits;

    /**
     * Constructor for the StrengthResult object.
     * 
     * @param score The mathematical score based on complexity rules.
     * @param strength The text categorization (WEAK, MEDIUM, STRONG).
     * @param suggestions List of actionable advice to improve the password.
     * @param crackTime Human-readable estimate of brute-force crack time.
     * @param entropyBits Calculated Shannon Entropy in bits.
     */
    public StrengthResult(int score, String strength, List<String> suggestions, String crackTime, double entropyBits) {
        this.score = score;
        this.strength = strength;
        this.suggestions = suggestions;
        this.crackTime = crackTime;
        this.entropyBits = entropyBits;
    }

    public int getScore() {
        return score;
    }

    public String getStrength() {
        return strength;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public String getCrackTime() {
        return crackTime;
    }
    
    public double getEntropyBits() {
        return entropyBits;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Score Breakdown: ").append(score).append("/10").append("\n");
        sb.append("Classified Strength: ").append(strength).append("\n");
        sb.append("Calculated Entropy: ").append(String.format("%.2f", entropyBits)).append(" bits\n\n");
        
        sb.append("[ Improvement Suggestions ]\n");
        if (suggestions.isEmpty()) {
            sb.append("* None. You have established a perfect password!\n");
        } else {
            for (int i = 0; i < suggestions.size(); i++) {
                sb.append("* ").append(suggestions.get(i));
                if (i < suggestions.size() - 1) {
                    sb.append("\n");
                }
            }
        }
        
        sb.append("\n\nEstimated Crack Time: ").append(crackTime);
        return sb.toString();
    }
}
