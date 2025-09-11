import java.awt.*;
import javax.swing.*;

public class HomeScreen {

    // Show the main menu screen
    // onStart is what happens when player clicks Start Game
    public static void show(Runnable onStart) {
        // Create the main menu window
        JFrame homeFrame = new JFrame("Welcome to Cup Memory Game");
        homeFrame.setSize(600, 400);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setLocationRelativeTo(null);  // Center the window on screen

        // Main container that holds everything
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(230, 230, 250));  // Light purple background

        // Panel that holds the logo and buttons
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(230, 230, 250));  // Same light purple
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));  // Stacking things top to bottom

        // Game logo image
        try {
            ImageIcon logoIcon = new ImageIcon("src/images/cupmemorytext.png");
            
            // Make the logo smaller if it's too big
            int maxWidth = 400;  // Don't let logo be wider than this
            float scale = (float) maxWidth / logoIcon.getIconWidth();  // How much to shrink it
            int newWidth = (int) (logoIcon.getIconWidth() * scale);
            int newHeight = (int) (logoIcon.getIconHeight() * scale);

            // Resize the image to the new size
            Image scaled = logoIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center the logo
            mainPanel.add(Box.createVerticalStrut(20));  // Add space at the top
            mainPanel.add(logoLabel);  // Add the logo to the panel
        } catch (Exception e) {
            // If the logo image doesn't exist, show text instead
            JLabel fallback = new JLabel("Cup Memory", SwingConstants.CENTER);
            fallback.setFont(new Font("Segoe UI", Font.BOLD, 28));
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(fallback);
        }

        // Create the "Start Game" button
        JButton startButton = createRoundedButton("Start Game", new Color(100, 149, 237));
        startButton.addActionListener(e -> {
            homeFrame.dispose();  // Close the main menu window
            onStart.run(); // Start the game
        });

        // Create the "How to Play" button
        JButton howToPlayButton = createRoundedButton("How to Play", new Color(60, 179, 113));
        howToPlayButton.addActionListener(e -> HowToPlayScreen.showInstructions());

        // Add the buttons to the panel with spacing
        mainPanel.add(Box.createVerticalStrut(10)); // Space before first button
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalStrut(8)); // Space between buttons
        mainPanel.add(howToPlayButton);
        mainPanel.add(Box.createVerticalGlue()); // Push everything up (fills remaining space)

        // Create the credits text at the bottom
        JLabel credits = new JLabel("Program made by Aatalon & Arixen");
        credits.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        credits.setForeground(Color.DARK_GRAY);
        credits.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0)); // Add padding around text

        // Panel to hold the credits in the bottom-left corner
        JPanel creditsPanel = new JPanel(new BorderLayout());
        creditsPanel.setBackground(new Color(230, 230, 250));  // Same background color
        creditsPanel.add(credits, BorderLayout.WEST);  // Put credits on the left side

        // Put everything together in the main wrapper
        wrapper.add(mainPanel, BorderLayout.CENTER);    // Logo and buttons in the center
        wrapper.add(creditsPanel, BorderLayout.SOUTH);  // Credits at the bottom

        // Set up the window and make it visible
        homeFrame.setContentPane(wrapper);
        homeFrame.setVisible(true);
    }

    // Helper method to create nice-looking rounded buttons
    private static JButton createRoundedButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            // Custom painting to make the button rounded
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  // Make edges smooth
                g2.setColor(getBackground());  // Use the button's background color
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);  // Draw rounded rectangle
                super.paintComponent(g2);  // Draw the button text on top
                g2.dispose();
            }

            // Don't draw the normal square border
            @Override
            protected void paintBorder(Graphics g) {
                
            }
        };

        // Set up how the button looks
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));  // Text size and font
        button.setBackground(bgColor); // Background color
        button.setForeground(Color.BLACK); // Text color 
        button.setFocusPainted(false); // Don't show focus outline
        button.setMaximumSize(new Dimension(200, 40)); // Don't let button get too big
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        button.setContentAreaFilled(false); // Don't fill with default background
        button.setOpaque(false); // Make background transparent
        
        return button; 
    }
}