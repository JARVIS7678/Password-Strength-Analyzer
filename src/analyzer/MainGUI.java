package analyzer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Extended Graphical User Interface for the Password Analyzer.
 * Features real-time typing evaluation, dynamic progress bars, dark theme,
 * dynamic checklist markers, copy to clipboard, and mask toggles.
 */
public class MainGUI extends JFrame {
    private JPasswordField passwordField;
    private JCheckBox showPasswordToggle;
    private JTextArea resultArea;
    private JProgressBar strengthBar;
    private JLabel statusLabel;
    
    // Dynamic Checklist
    private JLabel checkLength;
    private JLabel checkUpper;
    private JLabel checkLower;
    private JLabel checkNumber;
    private JLabel checkSymbol;
    
    public MainGUI() {
        setTitle("Password Strength Analyzer Pro (Interactive Edition)");
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        applyCustomDarkTheme();
        buildUI();
        attachRealtimeListeners();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // -- TOP SPLIT CONTAINER --
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        // Left Side: Input & Bars
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Interactive Evaluator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(180, 200, 255));
        gbc.gridy = 0; leftPanel.add(titleLabel, gbc);

        // Password input row with toggle
        JPanel passPanel = new JPanel(new BorderLayout(10, 0));
        passPanel.setOpaque(false);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Monospaced", Font.BOLD, 24));
        passwordField.setMargin(new Insets(8, 8, 8, 8));
        passwordField.setEchoChar('•');
        
        showPasswordToggle = new JCheckBox("👁️ View");
        showPasswordToggle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        showPasswordToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        passPanel.add(passwordField, BorderLayout.CENTER);
        passPanel.add(showPasswordToggle, BorderLayout.EAST);
        
        gbc.gridy = 1; leftPanel.add(passPanel, gbc);

        // Progress Details
        strengthBar = new JProgressBar(0, 10);
        strengthBar.setValue(0);
        strengthBar.setStringPainted(true);
        strengthBar.setString("Awaiting Sequence...");
        strengthBar.setFont(new Font("Consolas", Font.BOLD, 15));
        strengthBar.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 2; leftPanel.add(strengthBar, gbc);
        
        statusLabel = new JLabel("System Idle...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridy = 3; leftPanel.add(statusLabel, gbc);

        // Interactive Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.setOpaque(false);
        JButton generateBtn = new JButton("⚡ Generate");
        JButton copyBtn = new JButton("📋 Copy");
        JButton exportBtn = new JButton("💾 Export");
        JButton clearBtn = new JButton("🗑️ Clear");

        for (JButton btn : new JButton[]{generateBtn, copyBtn, exportBtn, clearBtn}) {
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            buttonsPanel.add(btn);
        }
        gbc.gridy = 4; leftPanel.add(buttonsPanel, gbc);

        // Right Side: Dynamic Checklist Tracker
        JPanel rightPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 2), 
            " Live Constraints Check "
        );
        tb.setTitleColor(new Color(180, 200, 255));
        tb.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 0, 0), tb
        ));
        
        JLabel listTitle = new JLabel(" Minimum Requirements:", SwingConstants.CENTER);
        listTitle.setForeground(new Color(200, 200, 200));
        listTitle.setFont(new Font("SansSerif", Font.ITALIC, 13));
        rightPanel.add(listTitle);

        checkLength = createCheckLabel("❌ 8+ Characters");
        checkUpper = createCheckLabel("❌ Uppercase (A-Z)");
        checkLower = createCheckLabel("❌ Lowercase (a-z)");
        checkNumber = createCheckLabel("❌ Number (0-9)");
        checkSymbol = createCheckLabel("❌ Symbol (!@#$)");

        rightPanel.add(checkLength);
        rightPanel.add(checkUpper);
        rightPanel.add(checkLower);
        rightPanel.add(checkNumber);
        rightPanel.add(checkSymbol);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // Center Output Viewport
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        resultArea.setEditable(false);
        resultArea.setMargin(new Insets(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        TitledBorder logBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 2), 
            " Diagnostic Matrix Stream "
        );
        logBorder.setTitleColor(new Color(180, 200, 255));
        scrollPane.setBorder(logBorder);

        mainPanel.add(splitPane, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Listeners for Buttons
        generateBtn.addActionListener(e -> produceRandomPassword());
        copyBtn.addActionListener(e -> {
            String val = new String(passwordField.getPassword());
            if (!val.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(val), null);
                JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Nothing to copy!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        exportBtn.addActionListener(e -> launchExportSequence());
        clearBtn.addActionListener(e -> clearAllData());

        // Toggle Password Masking
        showPasswordToggle.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                passwordField.setEchoChar((char) 0); // show password text
            } else {
                passwordField.setEchoChar('•'); // hide password text (dot)
            }
        });
    }

    private JLabel createCheckLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        l.setForeground(new Color(255, 90, 90));
        l.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        return l;
    }

    private void attachRealtimeListeners() {
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { triggerAnalysis(); }
            public void removeUpdate(DocumentEvent e) { triggerAnalysis(); }
            public void changedUpdate(DocumentEvent e) { triggerAnalysis(); }
        });
    }

    private void triggerAnalysis() {
        String password = new String(passwordField.getPassword());
        
        // 1. Update the Checklist visuals immediately
        updateChecklistVisuals(password);

        if (password.isEmpty()) {
            strengthBar.setValue(0);
            strengthBar.setString("Awaiting Sequence...");
            statusLabel.setText("System Idle...");
            statusLabel.setForeground(Color.LIGHT_GRAY);
            return;
        }

        // Conduct logic process
        StrengthResult result = PasswordAnalyzer.analyze(password);
        
        // Update live Progress Bar
        int score = result.getScore();
        if(score > 10) score = 10;
        strengthBar.setValue(score);
        
        Color barColor;
        if (result.getStrength().equals("WEAK")) {
            barColor = new Color(255, 69, 58);
            statusLabel.setForeground(barColor);
        } else if (result.getStrength().equals("MEDIUM")) {
            barColor = new Color(255, 159, 10);
            statusLabel.setForeground(barColor);
        } else {
            barColor = new Color(48, 209, 88); 
            statusLabel.setForeground(barColor);
        }
        
        strengthBar.setForeground(barColor);
        strengthBar.setString("Security Score: " + score + "/10");
        statusLabel.setText("Threat Level: " + result.getStrength() + " | Bits: " + String.format("%.2f", result.getEntropyBits()));

        // Push detailed dump strictly to log area
        String hiddenText = "-PROTECTED HASH-"; 
        if(showPasswordToggle.isSelected()) {
            hiddenText = password; // show if toggle is on
        }
        String dump = "\n[ Live Diagnostic for {" + hiddenText + "} ]\n" + result.toString() + "\n";
        resultArea.setText(dump);
        resultArea.setCaretPosition(0);
    }

    private void updateChecklistVisuals(String pwd) {
        Color failColor = new Color(255, 90, 90);
        Color passColor = new Color(48, 209, 88);

        // Length Check
        if (pwd.length() >= 8) {
            checkLength.setText("✅ 8+ Characters"); checkLength.setForeground(passColor);
        } else {
            checkLength.setText("❌ 8+ Characters"); checkLength.setForeground(failColor);
        }

        // Upper Check
        if (pwd.matches(".*[A-Z].*")) {
            checkUpper.setText("✅ Uppercase (A-Z)"); checkUpper.setForeground(passColor);
        } else {
            checkUpper.setText("❌ Uppercase (A-Z)"); checkUpper.setForeground(failColor);
        }

        // Lower Check
        if (pwd.matches(".*[a-z].*")) {
            checkLower.setText("✅ Lowercase (a-z)"); checkLower.setForeground(passColor);
        } else {
            checkLower.setText("❌ Lowercase (a-z)"); checkLower.setForeground(failColor);
        }

        // Digit Check
        if (pwd.matches(".*[0-9].*")) {
            checkNumber.setText("✅ Number (0-9)"); checkNumber.setForeground(passColor);
        } else {
            checkNumber.setText("❌ Number (0-9)"); checkNumber.setForeground(failColor);
        }

        // Symbol Check
        if (pwd.matches(".*[!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?`~].*")) {
            checkSymbol.setText("✅ Symbol (!@#$)"); checkSymbol.setForeground(passColor);
        } else {
            checkSymbol.setText("❌ Symbol (!@#$)"); checkSymbol.setForeground(failColor);
        }
    }

    private void produceRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+={}|<>?-";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(18); 
        for (int i = 0; i < 18; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        passwordField.setText(sb.toString());
    }

    private void clearAllData() {
        passwordField.setText("");
        resultArea.setText("");
        strengthBar.setValue(0);
        strengthBar.setString("Awaiting Sequence...");
        statusLabel.setText("System Idle...");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        // Reset checklist to failing state automatically due to DocumentListener via setText("")
    }

    private void launchExportSequence() {
        if (resultArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "<html><h3 style='color: #FF5A5A; font-family: SansSerif;'>Empty Stream!</h3><p style='font-family: SansSerif;'>Nothing to export right now. Type a password first.</p></html>", 
                "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FileDialog fileDialog = new FileDialog(this, "Save Analysis Metrics", FileDialog.SAVE);
        fileDialog.setFile("cryptography_metrics.txt");
        fileDialog.setVisible(true);

        String d = fileDialog.getDirectory();
        String f = fileDialog.getFile();
        if (d != null && f != null) {
            File destFile = new File(d, f);
            try (FileWriter fileWriter = new FileWriter(destFile)) {
                fileWriter.write("=== SECURITY ENGINE EXPORT ===\n\n");
                fileWriter.write("Timestamp: " + java.time.LocalDateTime.now() + "\n");
                fileWriter.write(resultArea.getText());
                
                // Extra visible HTML styled massive notification
                String successHtml = "<html><body style='width: 380px; padding: 10px; text-align: center; border: 1px solid #444;'>" +
                    "<h2 style='color: #30D158; font-family: SansSerif; margin-bottom: 5px;'>✅ REPORT SAVED SUCCESSFULLY!</h2><hr style='border: 1px solid #444;'>" +
                    "<p style='font-family: SansSerif; font-size: 14px; margin-top: 15px;'>Your analysis metrics file is safely stored at:</p>" + 
                    "<div style='background-color: #222222; color: #CYAN; padding: 10px; font-family: Monospaced; font-size: 12px; margin-top: 10px; border-radius: 5px; word-wrap: break-word;'>" + 
                    destFile.getAbsolutePath() + "</div></body></html>";

                JOptionPane.showMessageDialog(this, successHtml, "Export Complete", JOptionPane.PLAIN_MESSAGE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "<html><h3 style='color: #FF5A5A; font-family: SansSerif;'>Export Failed</h3><p style='font-family: SansSerif;'>" + ex.getMessage() + "</p></html>", 
                    "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyCustomDarkTheme() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        Color bg = new Color(25, 25, 28);       
        Color component = new Color(50, 50, 60);
        Color fg = new Color(230, 230, 230);

        UIManager.put("Panel.background", bg);
        UIManager.put("Label.foreground", fg);
        UIManager.put("Button.background", component);
        UIManager.put("Button.foreground", fg);
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
        
        UIManager.put("TextField.background", new Color(15, 15, 18));
        UIManager.put("TextField.foreground", fg);
        UIManager.put("TextField.caretForeground", Color.CYAN);
        
        UIManager.put("PasswordField.background", new Color(15, 15, 18));
        UIManager.put("PasswordField.foreground", fg);
        UIManager.put("PasswordField.caretForeground", Color.CYAN);
        
        UIManager.put("TextArea.background", new Color(15, 15, 18));
        UIManager.put("TextArea.foreground", new Color(110, 255, 110));
        
        UIManager.put("CheckBox.background", bg);
        UIManager.put("CheckBox.foreground", fg);
        UIManager.put("SplitPane.background", bg);

        SwingUtilities.updateComponentTreeUI(this);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
